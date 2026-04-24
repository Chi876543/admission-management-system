package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ThiSinhDAO {
    private final SessionFactory factory;

    public ThiSinhDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addWithSession(Session session, ThiSinh thiSinh) {
        session.save(thiSinh);
    }

    public ThiSinh getWithSession(Session session, int id) {
        ThiSinh thiSinh = session.get(ThiSinh.class, id);
        return thiSinh;
    }
    public List<ThiSinh> getAllWithSession(Session session){
        String query = "FROM ThiSinh";
        List listThiSinh = session.createQuery(query).list();
        return listThiSinh;
    }

    public void updateWithSession(Session session, ThiSinh newThiSinh) {
        session.merge(newThiSinh);
    }

    public void deleteWithSession(Session session, ThiSinh thiSinh) {
        session.detach(thiSinh);
    }
}
