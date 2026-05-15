package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.DiemThiXetTuyen;
import com.admissionManagement.core.entity.ThiSinh;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class DiemThiXetTuyenDAO {

    public void addWithSession(Session session, DiemThiXetTuyen diemThiXetTuyen) {
        session.save(diemThiXetTuyen);
    }

    public DiemThiXetTuyen getWithSession(Session session, int id){
        return session.get(DiemThiXetTuyen.class, id);
    }

    public DiemThiXetTuyen getByCccdWithSession(Session session, String cccd) {
        return session.createQuery("FROM DiemThiXetTuyen WHERE thiSinh.cccd = :cccd", DiemThiXetTuyen.class)
                .setParameter("cccd", cccd)
                .uniqueResult();
    }

    public List<DiemThiXetTuyen> getAllWithSession(Session session, int pageIndex, int pageSize){
        String query = "FROM DiemThiXetTuyen ORDER BY idDiemThi DESC";

        if(pageIndex == 0 || pageSize == 0)
            return session.createQuery(query, DiemThiXetTuyen.class).getResultList();

        int offset = pageIndex * pageSize;
        return session.createQuery(query, DiemThiXetTuyen.class).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    public void updateWithSession(Session session, DiemThiXetTuyen newDiemThiXetTuyen) {
        session.merge(newDiemThiXetTuyen);
    }

    public void deleteWithSession(Session session, DiemThiXetTuyen diemThiXetTuyen) {
        session.remove(diemThiXetTuyen);
    }
}