package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.BangQuyDoi;
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

    public void addWithSession(Session session, ToHopMonThi toHopMonThi) {
        session.save(toHopMonThi);
    }

    public ToHopMonThi getWithSession(Session session, int id){
        ToHopMonThi toHopMonThi = session.get(ToHopMonThi.class, id);
        return toHopMonThi;
    }

    public List<ToHopMonThi> getAllWithSession(Session session){
        String query = "FROM ToHopMonThi ";
        List listToHopMonThi= session.createQuery(query).list();
        return listToHopMonThi;
    }

    public void updateWithSession(Session session, ToHopMonThi newToHopMonThi) {
        session.merge(newToHopMonThi);
    }


    public void deleteWithSession(Session session, ToHopMonThi toHopMonThi) {
        session.detach(toHopMonThi);
    }
}
