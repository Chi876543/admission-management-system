package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.NganhToHop;
import com.admissionManagement.core.entity.NganhToHop;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class NganhToHopDAO {

    public void addWithSession(Session session, NganhToHop nganhToHop) {
        session.save(nganhToHop);
    }


    public NganhToHop getWithSession(Session session, int id){
        return session.get(NganhToHop.class, id);
    }

    public List<NganhToHop> getAllByMaNganhWithSession(Session session, String maNganh) {
        String hql = "SELECT nth FROM NganhToHop nth " +
                "WHERE nth.nganh.maNganh = :maNganh";

        return session.createQuery(hql, NganhToHop.class)
                .setParameter("maNganh", maNganh)
                .getResultList();

    }

    public List<NganhToHop> getAllWithSession(Session session, int pageIndex, int pageSize){
        String query = "FROM NganhToHop ";

        if (pageIndex == 0 || pageSize == 0)
            return session.createQuery(query, NganhToHop.class).getResultList();

        int offset = pageIndex * pageSize;
        return session.createQuery(query, NganhToHop.class).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    public void updateWithSession(Session session, NganhToHop newNganhToHop) {
        session.merge(newNganhToHop);
    }

    public void deleteWithSession(Session session, NganhToHop nganhToHop) {
        session.remove(nganhToHop);
    }
}
