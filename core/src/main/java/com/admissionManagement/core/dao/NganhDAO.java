package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.Nganh;
import org.hibernate.Session;

import java.util.List;

public class NganhDAO {

    public void addWithSession(Session session, Nganh nganh) {
        session.save(nganh);
    }

    public Nganh getWithSession(Session session, int id){
        Nganh nganh = session.get(Nganh.class, id);
        return nganh;
    }

    public List<Nganh> getAllWithSession(Session session){
        String query = "FROM Nganh";
        List listNganh = session.createQuery(query).list();
        return listNganh;
    }

    public void updateWithSession(Session session, Nganh newNganh) {
        session.merge(newNganh);
    }

    public void deleteWithSession(Session session, Nganh nganh) {
        session.detach(nganh);
    }
}
