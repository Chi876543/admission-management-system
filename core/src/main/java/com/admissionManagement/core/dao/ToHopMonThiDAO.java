package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.ToHopMonThi;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ToHopMonThiDAO {
    private final SessionFactory factory;

    public ToHopMonThiDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addWithSession(Session session, ToHopMonThi toHopMonThi) {
        session.save(toHopMonThi);
    }

    public ToHopMonThi getWithSession(Session session, int id){
        return session.get(ToHopMonThi.class, id);
    }

    public ToHopMonThi getByMaToHopWithSession(Session session, String maToHop){
        return session.createQuery("FROM ToHopMonThi WHERE maToHop = :ma", ToHopMonThi.class)
                .setParameter("ma", maToHop)
                .uniqueResult();
    }

    public List<ToHopMonThi> getAllWithSession(Session session, int pageIndex, int pageSize){
        String query = "FROM ToHopMonThi ";

        if(pageIndex == 0 || pageSize == 0)
            return session.createQuery(query, ToHopMonThi.class).getResultList();
        int offset = pageIndex * pageSize;
        return session.createQuery(query, ToHopMonThi.class).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    public List<String> getAllMaToHop(Session session) {
        return session.createQuery("SELECT t.maToHop FROM ToHopMonThi t", String.class).list();
    }

    public void updateWithSession(Session session, ToHopMonThi newToHopMonThi) {
        session.merge(newToHopMonThi);
    }


    public void deleteWithSession(Session session, ToHopMonThi toHopMonThi) {
        session.remove(toHopMonThi);
    }
}
