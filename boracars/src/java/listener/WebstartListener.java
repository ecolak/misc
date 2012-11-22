/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package listener;

import dao.Task;
import util.TaskUtil;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author ecolak
 */
public class WebstartListener implements ServletContextListener {
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("app server is about to get destroyed");
    }

    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("app server started");
        
        List<Task> enabledTasks = null;
        try {
            enabledTasks = Task.getEnabledTasks();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        if(enabledTasks != null) {
            for(Task task : enabledTasks) {
                try {
                    TaskUtil.enableTask(task.getTaskId());
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } 
    }
}
