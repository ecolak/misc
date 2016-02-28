package self.ec.btcbots.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebappListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(WebappListener.class);

  public void contextDestroyed(ServletContextEvent event) {
    LOG.info("Stopping Quartz scheduler");
    try {
      StdSchedulerFactory.getDefaultScheduler().shutdown();
    } catch (SchedulerException e) {
      LOG.error("Stopping Quartz scheduler failed", e);
    }
  }

  public void contextInitialized(ServletContextEvent event) {
    LOG.info("Starting Quartz scheduler");
    try {
      StdSchedulerFactory.getDefaultScheduler().start();
    } catch (SchedulerException e) {
      LOG.error("Starting Quartz scheduler failed", e);
      throw new RuntimeException(e);
    }
  }

}
