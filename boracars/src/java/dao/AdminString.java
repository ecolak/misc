/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "admin_string")

public class AdminString implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String HQL_GET_ALL = "SELECT a FROM AdminString a";
    private static final String HQL_GET_BY_NAME = "SELECT a FROM AdminString a WHERE a.name = :name";

    @Id
    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @Column(name = "value")
    private String value;

    public AdminString() {
    }

    public AdminString(String name) {
        this.name = name;
    }

    public AdminString(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static List<AdminString> getAll() throws HibernateException {
        List<AdminString> result = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_ALL);
        result = query.list();
        tx.commit();
        return result;
    }

    public static String getValueByName(String name, String defaultValue) throws HibernateException {
        AdminString row = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_BY_NAME);
        query.setString("name", name);
        row = (AdminString)query.uniqueResult();
        tx.commit();

        return row != null ? row.getValue() : defaultValue;
    }

    public void save() throws HibernateException {
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(this);
        tx.commit();
    }
}
