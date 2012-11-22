/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author ecolak
 */
public abstract class AbstractTask extends TimerTask {
    protected int delay;
    protected int period;
    protected String parameters;
    protected Timer timer;
    protected long taskId;

    public AbstractTask() {}

    public AbstractTask(int delay, int period, String parameters) {
        this.delay = delay;
        this.period = period;
        this.parameters = parameters;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public void startTask() {
        timer = new Timer();
        timer.scheduleAtFixedRate(this, delay, period);
    }

    public String getParamValue(String paramName) {
        String result = null;
        String[] tokens = StringUtils.split(parameters, '|');
        if(tokens != null && tokens.length > 0) {
            for(String t : tokens) {
                String[] pair = StringUtils.split(t, '=');
                if(pair != null && pair.length == 2) {
                    if(paramName.equals(pair[0])) {
                        result = pair[1];
                        break;
                    }
                }
            }
        }
        return result;
    }

    public abstract void initParams();
    public abstract void run();
}
