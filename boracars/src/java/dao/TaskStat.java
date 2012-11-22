/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 *
 * @author ecolak
 */
@Entity
@Table(name = "task_stats")
public class TaskStat implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String HQL_GET_BY_TASK_ID = "SELECT t FROM TaskStat t WHERE t.taskId = :taskId";
    private static final String HQL_GET_BY_TASK_STAT_ID = "SELECT t FROM TaskStat t WHERE t.taskStatId = :taskStatId";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "task_stat_id")
    private Long taskStatId;

    @Basic(optional = false)
    @Column(name = "task_id")
    private long taskId;

    @Basic(optional = false)
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(name = "log_file")
    private String logFile;

    public TaskStat() {
    }

    public TaskStat(Long taskStatId) {
        this.taskStatId = taskStatId;
    }

    public TaskStat(Long taskStatId, long taskId, Date startTime) {
        this.taskStatId = taskStatId;
        this.taskId = taskId;
        this.startTime = startTime;
    }

    public Long getTaskStatId() {
        return taskStatId;
    }

    public void setTaskStatId(Long taskStatId) {
        this.taskStatId = taskStatId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public long getDurationInMillis() {
        long result = -1;
        if(startTime != null && endTime != null) {
            result = endTime.getTime() - startTime.getTime();
        }
        return result;
    }

    public static TaskStat getById(long taskStatId) throws HibernateException {
        TaskStat result = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_BY_TASK_STAT_ID);
        query.setLong("taskStatId", taskStatId);
        result = (TaskStat)query.uniqueResult();
        tx.commit();

        return result;
    }

    public static List<TaskStat> getByTaskId(long taskId) throws HibernateException {
        List<TaskStat> result = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_BY_TASK_ID);
        query.setLong("taskId", taskId);
        result = query.list();
        tx.commit();

        return result;
    }

    public void save() throws HibernateException {
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(this);
        tx.commit();
    }

}
