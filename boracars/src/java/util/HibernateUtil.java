/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

/**
 *
 * @author ecolak
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml)
            // config file.
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception.
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession(){
        Session session = null;
        if(sessionFactory != null){
            try {
                try {
                    session = sessionFactory.getCurrentSession();
                } catch(Exception e) {
                    // ignore
                }

                if(session == null)
                    session = sessionFactory.openSession();
            } catch (HibernateException e) {
                e.getMessage();
            }
        }
        return session;
    }

    public static void closeSession(Session session) {
        if(session != null){
            try{
                session.close();
            } catch(HibernateException he){
                System.err.println(he);
            }
        }
    }
}

