package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.DiemCongXetTuyen;
import com.admissionManagement.core.entity.DiemCongXetTuyen;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

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

    public void updateWithSession(Session session, DiemCongXetTuyen newdiemCongXetTuyen) {
        session.merge(newdiemCongXetTuyen);
    }

    public void deleteWithSession(Session session, DiemCongXetTuyen diemCongXetTuyen) {
        session.detach(diemCongXetTuyen);
    }
}
