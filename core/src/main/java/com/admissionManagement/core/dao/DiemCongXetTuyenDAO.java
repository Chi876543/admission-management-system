package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.DiemCongXetTuyen;
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
        DiemCongXetTuyen diemCongXetTuyen = session.get(DiemCongXetTuyen.class, id);
        return diemCongXetTuyen;
    }

    public List<DiemCongXetTuyen> getAllWithSession(Session session, String cccd){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DiemCongXetTuyen> cq = cb.createQuery(DiemCongXetTuyen.class);
        Root<DiemCongXetTuyen> root = cq.from(DiemCongXetTuyen.class);
        List<Predicate> conditions = buildConditions(cb, root, cccd);

        if(!conditions.isEmpty())
            cq.where(conditions.toArray(new Predicate[0]));

        cq.orderBy(cb.desc(root.get("idDiemCong")));

        return session.createQuery(cq).getResultList();
    }

    public List<Predicate> buildConditions(CriteriaBuilder cb, Root<DiemCongXetTuyen> root, String cccd) {
        List<Predicate> conditions = new ArrayList<>();

        if(cccd != null && !cccd.trim().isEmpty())
            conditions.add(cb.equal(root.get("cccd"), cccd.trim()));

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

    public void deleteWithSession(Session session, DiemCongXetTuyen diemCongXetTuyen) {
        session.detach(diemCongXetTuyen);
    }
}
