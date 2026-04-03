package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.ToHopMonThi;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ToHopMonThiDAO {
    private final SessionFactory factory;

    public ToHopMonThiDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addToHopMonThi(ToHopMonThi toHopMonThi) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(toHopMonThi);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<ToHopMonThi> getAllToHopMonThi(){
        Session session = factory.openSession();
        String query = "FROM ToHopMonThi";
        List listToHopMonThi = session.createQuery(query).list();
        session.close();
        return listToHopMonThi;
    }

    public void updateToHopMonThi(int id, ToHopMonThi newToHopMonThi) {
        Session session = factory.openSession();
        Transaction tx = null;
        ToHopMonThi ToHopMonThi = session.get(ToHopMonThi.class, id);
        try {
            tx = session.beginTransaction();
            if(ToHopMonThi != null) {

            }
                tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteToHopMonThi(int id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ToHopMonThi toHopMonThi = session.get(ToHopMonThi.class,id);
            if (toHopMonThi != null) session.delete(toHopMonThi);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
