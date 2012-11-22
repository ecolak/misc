/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import dao.Task;
import dao.TaskStat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.displaytag.decorator.TableDecorator;

/**
 *
 * @author ecolak
 */
public class TaskStatsDecorator extends TableDecorator {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd, h:mm:ss a z");

    public TaskStat getData() {
        return (TaskStat)this.getCurrentRowObject();
    }

    public String getFormattedStartTime() {
        String result = "";
        try {
            TaskStat stat = getData();
            if(stat != null) {
                result = DATE_FORMAT.format(stat.getStartTime());
            }
        } catch(Exception e) { }
        return result;
    }

    public String getFormattedEndTime() {
        String result = "";
        try {
            TaskStat stat = getData();
            if(stat != null) {
                result = DATE_FORMAT.format(stat.getEndTime());
            }
        } catch(Exception e) { }
        return result;
    }

    public String getFormattedDuration() {
        String result = "";
        TaskStat stat = getData();
        if(stat != null) {
            long durationInMillis = stat.getDurationInMillis();
            if(durationInMillis != -1) {
                long seconds = Math.round(durationInMillis / (double)1000);
                long minutes = 0;
                if(seconds > 59) {
                    minutes = seconds / 60;
                    seconds = seconds % 60;
                }

                if(minutes > 0) {
                    result += minutes + " minute" + (minutes > 1 ? "s" : "") + " ";
                }
                result += seconds + " second" + (seconds > 1 ? "s" : "");
            }
        }
        return result;
    }

    public String getTaskName() {
        String result = "";
        TaskStat stat = getData();
        if(stat != null) {
            Task task = Task.getById(stat.getTaskId());
            if(task != null) {
                result = task.getTaskName();
            }
        }
        return result;
    }
}
