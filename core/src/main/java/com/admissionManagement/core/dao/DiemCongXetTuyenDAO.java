package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.DiemCongXetTuyen;
import com.admissionManagement.core.entity.DiemThiXetTuyen;
import com.admissionManagement.core.entity.ThiSinh;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DiemCongXetTuyenDAO {

    public void addWithSession(Session session, DiemCongXetTuyen diemCongXetTuyen) {
        session.save(diemCongXetTuyen);
    }

    public DiemCongXetTuyen getWithSession(Session session, int id){
        return session.get(DiemCongXetTuyen.class, id);
    }

    public List<DiemCongXetTuyen> getAllWithSession(Session session, String cccd, int pageSize, int pageIndex){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DiemCongXetTuyen> cq = cb.createQuery(DiemCongXetTuyen.class);
        Root<DiemCongXetTuyen> root = cq.from(DiemCongXetTuyen.class);
        List<Predicate> conditions = buildConditions(cb, root, cccd);

        if(!conditions.isEmpty())
            cq.where(conditions.toArray(new Predicate[0]));

        cq.orderBy(cb.desc(root.get("idDiemCong")));

        if(pageSize <= 0)
            return session.createQuery(cq).getResultList();

        int offset = pageIndex * pageSize;
        return session.createQuery(cq).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    public List<DiemCongXetTuyen> getAll(Session session, int pageSize, int pageIndex){
        String query = "FROM DiemCongXetTuyen ORDER BY idDiemCong DESC";

        if(pageSize <= 0)
            return session.createQuery(query, DiemCongXetTuyen.class).list();

        int offset = pageIndex * pageSize;
        return session.createQuery(query, DiemCongXetTuyen.class).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    /**
     * Build search conditions: keyword khớp CCCD, môn, hoặc phương thức (OR).
     */
    public List<Predicate> buildConditions(CriteriaBuilder cb, Root<DiemCongXetTuyen> root, String keyword) {
        List<Predicate> conditions = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            Predicate byCccd       = cb.like(cb.lower(root.get("thiSinh").get("cccd")), kw);
            Predicate byMon        = cb.like(cb.lower(root.get("mon")), kw);
            Predicate byPhuongThuc = cb.like(cb.lower(root.get("phuongThuc")), kw);
            conditions.add(cb.or(byCccd, byMon, byPhuongThuc));
        }

        return conditions;
    }

    public List<DiemCongXetTuyen> getListByCccdWithSession(Session session, String cccd){
        return session.createQuery("FROM DiemCongXetTuyen WHERE thiSinh.cccd = :cccd", DiemCongXetTuyen.class)
                .setParameter("cccd", cccd)
                .getResultList();
    }

    public void updateWithSession(Session session, DiemCongXetTuyen newdiemCongXetTuyen) {
        session.merge(newdiemCongXetTuyen);
    }

    public long getTotalWithSession(Session session) {
        return session.createQuery("SELECT COUNT(d) FROM DiemCongXetTuyen d", Long.class).getSingleResult();
    }

    public long getTotalByKeyword(Session session, String keyword) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<DiemCongXetTuyen> root = cq.from(DiemCongXetTuyen.class);
        cq.select(cb.count(root));

        List<Predicate> conditions = buildConditions(cb, root, keyword);
        if (!conditions.isEmpty()) {
            cq.where(conditions.toArray(new Predicate[0]));
        }

        return session.createQuery(cq).getSingleResult();
    }

    public void deleteWithSession(Session session, DiemCongXetTuyen diemCongXetTuyen) {
        session.remove(diemCongXetTuyen);
    }
}
