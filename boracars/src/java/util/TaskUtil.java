/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import task.AbstractTask;
import dao.Task;
import dao.TaskStat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import ui.TaskStatsDecorator;

/**
 *
 * @author ecolak
 */
public class TaskUtil {

    public static final DateFormat TASK_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public static void enableTask(long taskId) throws Exception {
        Task task = Task.getById(taskId);
        if(task != null) {
            task.setEnabled(true);
            task.save();
            Object o = Class.forName(task.getClassName()).newInstance();
            if(o != null && o instanceof AbstractTask) {
                AbstractTask at = (AbstractTask)o;
                TaskMap.getInstance().put(taskId, at);
                at.setTaskId(taskId);
                at.setDelay(task.getDelay());
                at.setPeriod(task.getPeriod());
                at.setParameters(task.getParameters());
                at.initParams();
                at.startTask();
            }
        }
    }

    public static void disableTask(long taskId) throws Exception {
        AbstractTask at = TaskMap.getInstance().get(taskId);
        if(at != null) {
            at.cancel();
        }
        Task task = Task.getById(taskId);
        if(task != null) {
            task.setEnabled(false);
            task.save();
        }
    }

    public static long logStart(long taskId, Date startTime) {
        System.out.println("Task " + taskId + " started at " + TaskStatsDecorator.DATE_FORMAT.format(startTime));
        TaskStat stat = new TaskStat();
        stat.setTaskId(taskId);
        stat.setStartTime(startTime);
        stat.save();

        return stat.getTaskStatId();
    }

    public static void logEnd(long taskStatId, Date endTime, String logFile) {
        TaskStat stat = TaskStat.getById(taskStatId);
        if(stat != null) {
            stat.setEndTime(endTime);
            stat.setLogFile(logFile);
            stat.save();
            System.out.println("Task " + stat.getTaskId() + " ended at " + TaskStatsDecorator.DATE_FORMAT.format(endTime));
        }
    }
}
