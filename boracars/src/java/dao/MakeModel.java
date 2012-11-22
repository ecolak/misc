/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "make_model")
@NamedQueries({
    @NamedQuery(name = "MakeModel.findAll", query = "SELECT m FROM MakeModel m")})

public class MakeModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String HQL_GET_ALL = "SELECT distinct m.make FROM m";

    private static final String HQL_GET_ALL_MAKES = "SELECT distinct m.make FROM MakeModel m";

    private static final String HQL_GET_ALL_MODELS = "SELECT distinct m.model FROM MakeModel m";

    private static final String HQL_GET_MODELS_BY_MAKE = "SELECT distinct m.model FROM "
            + "MakeModel m WHERE m.make = :make";

    private static final String HQL_GET_MAKES_BY_MODEL = "SELECT distinct m.make FROM "
            + "MakeModel m WHERE m.model = :model";

    private static final String HQL_GET_BY_MAKE_AND_MODEL = "SELECT m FROM MakeModel m "
            + "WHERE m.make = :make and m.model = :model";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "make_model_id")
    private Integer makeModelId;

    @Basic(optional = false)
    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    public MakeModel() {
    }

    public MakeModel(Integer makeModelId) {
        this.makeModelId = makeModelId;
    }

    public MakeModel(Integer makeModelId, String make) {
        this.makeModelId = makeModelId;
        this.make = make;
    }

    public Integer getMakeModelId() {
        return makeModelId;
    }

    public void setMakeModelId(Integer makeModelId) {
        this.makeModelId = makeModelId;
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

    public static List<MakeModel> getAll() {
        List<MakeModel> result = new ArrayList<MakeModel>();
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_ALL);
        result = query.list();
        tx.commit();

        return result;
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

    public static List<String> getAllModels() {
        List<String> result = new ArrayList<String>();
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_ALL_MODELS);

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

    public static String getMakeByModel(String model) {
        String result = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_MAKES_BY_MODEL);
        query.setString("model", model);

        List<String> list = query.list();
        tx.commit();
        
        if(list != null && !list.isEmpty()) {
            result = list.get(0);
        }

        return result;
    }

    public static MakeModel getByMakeAndModel(String make, String model) {
        MakeModel result = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_BY_MAKE_AND_MODEL);
        query.setString("make", make);
        query.setString("model", model);

        result = (MakeModel)query.uniqueResult();
        tx.commit();

        return result;
    }

    public static String getAllMakesRegex() {
        String result = null;
        StringBuilder regex = new StringBuilder("(");
        List<String> allMakes = MakeModel.getAllMakes();
        if(!allMakes.isEmpty()) {
            for(int i = 0; i < allMakes.size(); i++) {
                String make = allMakes.get(i);
                if(i > 0) {
                    regex.append("|");
                }
                regex.append(make);
            }
            regex.append(")");
            result = regex.toString();
        } else {
            result = null;
        }
        return result;
    }

    public void save() throws HibernateException {
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(this);
        tx.commit();
    }

}
