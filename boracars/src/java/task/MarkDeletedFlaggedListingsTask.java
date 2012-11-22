/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import dao.CLCar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;
import util.HttpUtil;
import util.TaskUtil;

/**
 *
 * @author ecolak
 */
public class MarkDeletedFlaggedListingsTask extends AbstractTask {

    public static final int DEFAULT_HOURS_BACK = 24;
    public static final int DEFAULT_START_HOURS = 3;
    public static final String DELETED_MSG = "This posting has been deleted by its author";
    public static final String FLAGGED_MSG = "This posting has been flagged for removal";
    private Date fromTime;
    private Date toTime;
    private int hoursBack;

    public MarkDeletedFlaggedListingsTask() {}

    public MarkDeletedFlaggedListingsTask(int delay, int period, String params) {
        super(delay, period, params);
    }

    @Override
    public final void initParams() {
        try {
            toTime = TaskUtil.TASK_DATE_FORMAT.parse(getParamValue("to-time"));
        } catch(Exception e) {
            toTime = DateUtils.addHours(new Date(), -DEFAULT_START_HOURS);
        }
        try {
            hoursBack = Integer.parseInt(getParamValue("hours-back"));
        } catch(Exception e) {
            hoursBack = DEFAULT_HOURS_BACK;
        }
        fromTime = DateUtils.addHours(toTime, -hoursBack);
    }

    @Override
    public void run() {
        long taskStatId = TaskUtil.logStart(taskId, new Date());
        List<CLCar> cars = CLCar.getCars(null, null, null, null, null, null, null, null, fromTime, toTime, null, false, false);
        System.out.println("Will go through " + cars.size() + " cars");
        for(CLCar car : cars) {
            if(car.getListingUrl() != null) {
                try {
                    String response = HttpUtil.httpGet(car.getListingUrl());
                    if(response.contains(DELETED_MSG)) {
                        System.out.println(car.getListingUrl() + " has been marked deleted");                       
                        car.setDeleted(true);
                        car.save();
                    } else if(response.contains(FLAGGED_MSG)) {
                        System.out.println(car.getListingUrl() + " has been marked flagged");                        
                        car.setFlagged(true);
                        car.save();
                    }
                } catch(Exception ioe) {
                    System.err.println(ioe.getMessage());
                }
            }
        }
        System.out.println("Finished marking deleted and flagged postings");
        TaskUtil.logEnd(taskStatId, new Date(), null);
    }

}
