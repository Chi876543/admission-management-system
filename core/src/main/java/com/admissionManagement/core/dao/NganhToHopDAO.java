package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.NganhToHop;
import com.admissionManagement.core.entity.NganhToHop;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class NganhToHopDAO {

    public void addWithSession(Session session, NganhToHop nganhToHop) {
        session.save(nganhToHop);
    }


    public NganhToHop getWithSession(Session session, int id){
        NganhToHop nganhToHop  = session.get(NganhToHop.class, id);
        return nganhToHop;
    }



    public List<NganhToHop> getAllWithSession(Session session){
        String query = "FROM NganhToHop ";
        List listNganhToHop = session.createQuery(query).list();
        return listNganhToHop;
    }

    public void updateWithSession(Session session, NganhToHop newNganhToHop) {
        session.merge(newNganhToHop);
    }

    public void deleteWithSession(Session session, NganhToHop nganhToHop) {
        session.detach(nganhToHop);
    }
}
