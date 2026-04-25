package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.ThiSinh;
import org.hibernate.Session;
import java.util.List;

public class ThiSinhDAO {
    public void addWithSession(Session session, ThiSinh thiSinh) {
        session.persist(thiSinh);
    }

    public ThiSinh getWithSession(Session session, int id) {
        return session.get(ThiSinh.class, id);
    }

    public List<ThiSinh> getAllWithSession(Session session){
        return session.createQuery("FROM ThiSinh", ThiSinh.class).getResultList();
    }

    public void updateWithSession(Session session, ThiSinh newThiSinh) {
        session.merge(newThiSinh);
    }

    public void deleteWithSession(Session session, ThiSinh thiSinh) {
        session.remove(thiSinh);
    }
}
