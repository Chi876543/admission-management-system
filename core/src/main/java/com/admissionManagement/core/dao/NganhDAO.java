package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.Nganh;
import com.admissionManagement.core.entity.ToHopMonThi;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NganhDAO {

    public void addWithSession(Session session, Nganh nganh) {
        session.save(nganh);
    }

    public Nganh getWithSession(Session session, int id){
        Nganh nganh = session.get(Nganh.class, id);
        return nganh;
    }

    public Nganh getByMaNganhWithSession(Session session, String maNganh){
        return session.createQuery("FROM Nganh WHERE maNganh = :ma", Nganh.class)
                .setParameter("ma", maNganh)
                .uniqueResult();
    }

    public List<Nganh> getAllWithSession(Session session){
        String query = "FROM Nganh";
        List listNganh = session.createQuery(query).list();
        return listNganh;
    }

    public List<String> getAllMaNganh(Session session) {
        return session.createQuery("SELECT n.maNganh FROM Nganh n", String.class).list();
    }

    public void updateWithSession(Session session, Nganh newNganh) {
        session.merge(newNganh);
    }

    public void deleteWithSession(Session session, Nganh nganh) {
        session.remove(nganh);
    }
}
