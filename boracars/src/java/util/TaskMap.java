/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import task.AbstractTask;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ecolak
 */
public class TaskMap {
    private Map<Long, AbstractTask> map;
    private static TaskMap instance;

    private TaskMap() {
        map = Collections.synchronizedMap(new HashMap<Long, AbstractTask>());
    }

    public static synchronized TaskMap getInstance() {
        if(instance == null)
            instance = new TaskMap();

        return instance;
    }

    public void put(long taskId, AbstractTask task) {
        map.put(taskId, task);
    }

    public AbstractTask get(long taskId) {
        return map.get(taskId);
    }

    public void remove(long taskId) {
        map.remove(taskId);
    }
}
