package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.ThiSinh;
import org.hibernate.Session;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ThiSinhDAO {
    public void addWithSession(Session session, ThiSinh thiSinh) {
        session.persist(thiSinh);
    }

    public ThiSinh getWithSession(Session session, int id) {
        return session.get(ThiSinh.class, id);
    }

    public List<ThiSinh> getAllWithSession(Session session, String ho, String ten, String cccd, int pageIndex, int pageSize){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ThiSinh> cq = cb.createQuery(ThiSinh.class);
        Root<ThiSinh> root = cq.from(ThiSinh.class);
        List<Predicate> conditions = buildConditions(cb, root, ho, ten, cccd);

        if(!conditions.isEmpty()){
            cq.where(conditions.toArray(new Predicate[0]));
        }
        cq.orderBy(cb.desc(root.get("idThiSinh")));
        int offset = pageIndex * pageSize;

        return session.createQuery(cq).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    public void updateWithSession(Session session, ThiSinh newThiSinh) {
        session.merge(newThiSinh);
    }

    public void deleteWithSession(Session session, ThiSinh thiSinh) {
        session.remove(thiSinh);
    }

    public List<Predicate> buildConditions(CriteriaBuilder cb, Root<ThiSinh> root, String ho, String ten, String cccd) {
        List<Predicate> conditions = new ArrayList<>();

        if(cccd != null && !cccd.trim().isEmpty()){
            conditions.add(cb.equal(root.get("cccd"), cccd.trim()));
        }

        if(ho != null && !ho.trim().isEmpty()){
            conditions.add(cb.like(root.get("ho"), "%" + ho.trim() + "%"));
        }

        if(ten != null && !ten.trim().isEmpty()){
            conditions.add(cb.like(root.get("ten"), ten.trim() + "%"));
        }

        return conditions;
    }

    public long getTotalWithSession(Session session, String ho, String ten, String cccd) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ThiSinh> root = cq.from(ThiSinh.class);

        cq.select(cb.count(root));

        List<Predicate> conditions = buildConditions(cb, root, ho, ten, cccd);
        if(!conditions.isEmpty()){
            cq.where(conditions.toArray(new Predicate[0]));
        }
        return session.createQuery(cq).getSingleResult();
    }
}
