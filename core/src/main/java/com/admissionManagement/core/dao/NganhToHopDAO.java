package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.NganhToHop;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class NganhToHopDAO {
    private final SessionFactory factory;

    public NganhToHopDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addNganhToHop(NganhToHop nganhToHop) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(nganhToHop);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<NganhToHop> getAllNganhToHop(){
        Session session = factory.openSession();
        String query = "FROM NganhToHop";
        List listNganhToHop = session.createQuery(query).list();
        session.close();
        return listNganhToHop;
    }

    public void updateNganhToHop(int id, NganhToHop newNganhToHop) {
        Session session = factory.openSession();
        Transaction tx = null;
        NganhToHop nganhToHop = session.get(NganhToHop.class, id);
        try {
            tx = session.beginTransaction();
            if(nganhToHop != null){
                //nganhToHop.set

            }
                tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteNganhToHop(int id) {
        Session session = factory.openSession();
        Transaction tx = null;
        NganhToHop nganhToHop = session.get(NganhToHop.class,id);
        try {
            tx = session.beginTransaction();
            if (nganhToHop != null) session.delete(nganhToHop);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
