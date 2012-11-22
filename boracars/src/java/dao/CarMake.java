/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "car_makes")

public class CarMake implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String HQL_GET_BY_MAKE_MODEL_YEAR = "SELECT c FROM CarMake c WHERE "
            + "c.make = :make and c.model = :model and c.year = :year";

    private static final String HQL_GET_ALL_MAKES = "SELECT distinct c.make FROM CarMake c";

    private static final String HQL_GET_MODELS_BY_MAKE = "SELECT distinct c.model FROM CarMake c WHERE c.make = :make";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "car_make_id")
    private Integer carMakeId;

    @Basic(optional = false)
    @Column(name = "make")
    private String make;

    @Basic(optional = false)
    @Column(name = "model")
    private String model;

    @Column(name = "year")
    private Integer year;

    public CarMake() {
    }

    public CarMake(Integer carMakeId) {
        this.carMakeId = carMakeId;
    }

    public CarMake(Integer carMakeId, String make, String model) {
        this.carMakeId = carMakeId;
        this.make = make;
        this.model = model;
    }

    public Integer getCarMakeId() {
        return carMakeId;
    }

    public void setCarMakeId(Integer carMakeId) {
        this.carMakeId = carMakeId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public static List<String> getAllMakes() {
        List<String> result = new ArrayList<String>();
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_ALL_MAKES);

        result = query.list();
        tx.commit();

        return result;
    }

    public static List<String> getModelsByMake(String make) {
        List<String> result = new ArrayList<String>();
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_MODELS_BY_MAKE);
        query.setString("make", make);

        result = query.list();
        tx.commit();

        return result;
    }

    public static CarMake getByMakeModelYear(String make, String model, int year) {
        CarMake result = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_BY_MAKE_MODEL_YEAR);
        query.setString("make", make);
        query.setString("model", model);
        query.setInteger("year", year);
      
        result = (CarMake)query.uniqueResult();
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
