package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.DiemCongXetTuyen;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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

    public List<DiemCongXetTuyen> getAllWithSession(Session session){
        String query = "FROM DiemCongXetTuyen";
        List listDiemCongXetTuyen = session.createQuery(query).list();
        return listDiemCongXetTuyen;
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
