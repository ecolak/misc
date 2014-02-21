package self.ec.btcbots.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import self.ec.btcbots.model.Bot;
import self.ec.btcbots.model.BotConfig;
import self.ec.btcbots.model.BotState;
import self.ec.btcbots.model.Duration;
import self.ec.btcbots.model.PointInTime;
import self.ec.btcbots.model.TimeUnit;
import self.ec.btcbots.util.JobUtil;

@Path("/bots")
@Produces(MediaType.APPLICATION_JSON)
public class BotResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(BotResource.class);
	private static final String DEFAULT_JOB_GROUP = "default_job_group";
	private static final String DEFAULT_TRIGGER_GROUP = "default_trigger_group";
	private static final String DEFAULT_TRIGGER = "default_trigger";
	private static final int DEFAULT_TRIGGER_PERIOD_MINS = 1;

	@GET
	public List<Bot> getBots() {
		List<Bot> result = new ArrayList<>();
		try {		
			Map<String, JobExecutionContext> runningJobMap = getRunningJobMap();
			
			Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
			Set<JobKey> jobKeys = sched.getJobKeys(GroupMatcher.anyJobGroup());
			for (JobKey jobKey : jobKeys) {
				List<Trigger> triggers = (List<Trigger>)sched.getTriggersOfJob(jobKey);			
				Trigger trigger = triggers.size() > 0 ? triggers.get(0) : null;
				JobDetail jobDetail = sched.getJobDetail(jobKey);
				JobDataMap params = jobDetail.getJobDataMap();
				
				Bot bot = new Bot();
				BotConfig config = new BotConfig();
				config.setName(jobDetail.getKey().toString());
				config.setBudget(JobUtil.getFloatFromJobDataMap(params, BotConfig.PARAM_BUDGET));
				config.setStartPrice(JobUtil.getFloatFromJobDataMap(params, BotConfig.PARAM_START_PRICE));
				config.setTradeDiffPct(JobUtil.getFloatFromJobDataMap(params, BotConfig.PARAM_TRADE_DIFF_PCT));
				bot.setConfig(config);
				
				String status = runningJobMap.containsKey(jobKey.toString()) 
						? BotState.RUNNING : sched.getTriggerState(trigger.getKey()).name();
				
				BotState state = new BotState();
				state.setStatus(status);
				bot.setState(state);
				
				result.add(bot);
			} 		
		} catch (SchedulerException se) {
			LOG.error("Error retrieving running bots", se);
			throw new WebApplicationException("Error retrieving running bots");
		}
		return result;
	}

	private Map<String, JobExecutionContext> getRunningJobMap() throws SchedulerException {
		Map<String, JobExecutionContext> result = new HashMap<>();
		List<JobExecutionContext> jobs = StdSchedulerFactory.getDefaultScheduler().getCurrentlyExecutingJobs();
		for (JobExecutionContext jec : jobs) {
			result.put(jec.getJobDetail().getKey().toString(), jec);
		}
		return result;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response startBot(BotConfig config) {
		JobDetail jd = JobBuilder.newJob(config.getBotType().getClazz())
				.withIdentity(config.getName(), DEFAULT_JOB_GROUP)
				.usingJobData(BotConfig.PARAM_BUDGET, config.getBudget())
				.usingJobData(BotConfig.PARAM_START_PRICE, config.getStartPrice())
				.usingJobData(BotConfig.PARAM_TRADE_DIFF_PCT, config.getTradeDiffPct())
				.build();

		Duration period = config.getSchedule().getPeriod();
		SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();
		if (TimeUnit.DAYS == period.getTimeUnit()) {
			ssb = ssb.withIntervalInHours(period.getInterval()*24);
		} else if (TimeUnit.HOURS == period.getTimeUnit()) {
			ssb = ssb.withIntervalInHours(period.getInterval());
		} else if (TimeUnit.MINUTES == period.getTimeUnit()) {
			ssb = ssb.withIntervalInMinutes(period.getInterval());
		} else if (TimeUnit.SECONDS == period.getTimeUnit()) {
			ssb = ssb.withIntervalInSeconds(period.getInterval());
		} else {
			ssb = ssb.withIntervalInMinutes(DEFAULT_TRIGGER_PERIOD_MINS);
		}
		
		PointInTime startTime = config.getSchedule().getStartTime();
		TriggerBuilder tb = TriggerBuilder
				.newTrigger()
				.withIdentity("trigger of " + config.getName(), DEFAULT_TRIGGER_GROUP);
		if (startTime.isNow()) {
			tb = tb.startNow();
		} else if (startTime.getDelay() != null) {
			tb = tb.startAt(new Date(System.currentTimeMillis() + 
									 startTime.getDelay().toMillis()));
		} else if (startTime.getTimeInMillis() != null) {
			tb = tb.startAt(new Date(startTime.getTimeInMillis()));
		}
		
		Trigger trigger = tb.withSchedule(ssb.repeatForever()).build();

		try {
			StdSchedulerFactory.getDefaultScheduler().scheduleJob(jd, trigger);
		} catch (SchedulerException se) {
			LOG.error("Error starting bot", se);
			throw new WebApplicationException("Error starting bot");
		}
		return Response.status(Status.CREATED).build();
	}

	@DELETE
	public Response deleteBot(@QueryParam("name") String name, @QueryParam("group") String group) {
		JobKey jobKey = JobKey.jobKey(name, group);

		try {
			assertBotExists(jobKey);
			StdSchedulerFactory.getDefaultScheduler().deleteJob(jobKey);
		} catch (SchedulerException se) {
			String error = "Error deleting bot with job key: "
					+ jobKey.toString();
			LOG.error(error, se);
			throw new WebApplicationException(error);
		}
		return Response.ok().build();
	}

	@PUT
	@Path("/pause")
	public Response pauseBot(@QueryParam("name") String name, @QueryParam("group") String group) {
		JobKey jobKey = JobKey.jobKey(name, group);
		try {
			assertBotExists(jobKey);
			StdSchedulerFactory.getDefaultScheduler().pauseJob(jobKey);
		} catch (SchedulerException se) {
			String error = "Error pausing bot with job key: " + jobKey.toString();
			LOG.error(error, se);
			throw new WebApplicationException(error);
		}
		return Response.ok().build();
	}

	@PUT
	@Path("/resume")
	public Response resumeBot(@QueryParam("name") String name,
			@QueryParam("group") String group) {
		JobKey jobKey = JobKey.jobKey(name, group);
		try {
			assertBotExists(jobKey);
			StdSchedulerFactory.getDefaultScheduler().resumeJob(jobKey);
		} catch (SchedulerException se) {
			String error = "Error resuming bot with job key: "
					+ jobKey.toString();
			LOG.error(error, se);
			throw new WebApplicationException(error);
		}
		return Response.ok().build();
	}

	private void assertBotExists(JobKey jobKey) throws SchedulerException {
		boolean exists = StdSchedulerFactory.getDefaultScheduler().checkExists(jobKey);
		if (!exists) {
			throw new WebApplicationException("Bot does not exist", Response.Status.NOT_FOUND);
		}
	}
}