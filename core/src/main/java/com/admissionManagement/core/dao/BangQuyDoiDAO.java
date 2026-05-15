package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.ThiSinh;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BangQuyDoiDAO {
    // Xóa Thuộc tính session và constructor

    public void addWithSession(Session session, BangQuyDoi bangQuyDoi) {
        session.save(bangQuyDoi);
    }

    public BangQuyDoi getWithSession(Session session, int id){
        return session.get(BangQuyDoi.class, id);
    }

    public List<BangQuyDoi> getAllWithSession(Session session, String phuongthuc, String tohop, String mon, int pageIndex, int pageSize){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<BangQuyDoi> cq = cb.createQuery(BangQuyDoi.class);
        Root<BangQuyDoi> root = cq.from(BangQuyDoi.class);
        List<Predicate> conditions = buildConditions(cb, root, phuongthuc, tohop, mon);

        if(!conditions.isEmpty()){
            cq.where(conditions.toArray(new Predicate[0]));
        }

        cq.orderBy(cb.desc(root.get("idqd")));

        if(pageIndex == 0 || pageSize == 0)
            return session.createQuery(cq).getResultList();

        int offset = pageIndex * pageSize;
        return session.createQuery(cq).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    public List<Predicate> buildConditions(CriteriaBuilder cb, Root<BangQuyDoi> root, String phuongthuc, String tohop, String mon) {
        List<Predicate> conditions = new ArrayList<>();

        if(phuongthuc != null && !phuongthuc.trim().isEmpty()){
            conditions.add(cb.equal(root.get("phuongThuc"), phuongthuc.trim()));
        }

        if(tohop != null && !tohop.trim().isEmpty()){
            conditions.add(cb.equal(root.get("toHop"), tohop.trim()));
        }

        if(mon != null && !mon.trim().isEmpty()){
            conditions.add(cb.equal(root.get("mon"), mon.trim()));
        }

        return conditions;
    }

    public void updateWithSession(Session session, BangQuyDoi newBangQuyDoi) {
        session.merge(newBangQuyDoi);
    }

    public long getTotalWithSession(Session session) {
        return session.createQuery("SELECT COUNT(b) FROM BangQuyDoi b", Long.class).getSingleResult();
    }

    public void deleteWithSession(Session session, BangQuyDoi bangQuyDoi) {
        session.remove(bangQuyDoi);
    }

    public BangQuyDoi getLuatQuyDoiWithSession(Session session, String phuongThuc, BigDecimal diem, String mon, String toHop) {
        String hql = "FROM BangQuyDoi b " +
                "WHERE :diemThiSinh BETWEEN b.diemA AND b.diemB " +
                "AND b.phuongThuc = :phuongThuc " +
                "AND (b.mon = :mon OR (:mon IS NULL AND b.mon IS NULL)) " +
                "AND (b.toHop = :toHop OR (:toHop IS NULL AND b.toHop IS NULL))";

        Query<BangQuyDoi> query = session.createQuery(hql, BangQuyDoi.class);
        query.setParameter("diemThiSinh", diem);
        query.setParameter("phuongThuc", phuongThuc);
        query.setParameter("mon", mon);
        query.setParameter("toHop", toHop);

        return query.uniqueResult();
    }
}
