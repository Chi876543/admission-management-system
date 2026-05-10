package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.DiemThiXetTuyen;
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
        DiemThiXetTuyen diemThiXetTuyen = session.get(DiemThiXetTuyen.class, id);
        return diemThiXetTuyen;
    }

    public List<DiemThiXetTuyen> getAllWithSession(Session session){
        String query = "FROM DiemThiXetTuyen";
        List listDiemThiXetTuyen = session.createQuery(query).list();
        return listDiemThiXetTuyen;
    }

    public void updateWithSession(Session session, DiemThiXetTuyen newDiemThiXetTuyen) {
        session.merge(newDiemThiXetTuyen);
    }

    public void deleteWithSession(Session session, DiemThiXetTuyen diemThiXetTuyen) {
        session.detach(diemThiXetTuyen);
    }
}
