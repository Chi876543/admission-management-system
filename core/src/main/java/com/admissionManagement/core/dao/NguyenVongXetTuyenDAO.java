package com.admissionManagement.core.dao;


import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.NguyenVongXetTuyen;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class NguyenVongXetTuyenDAO {
    private final SessionFactory factory;

    public NguyenVongXetTuyenDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addWithSession(Session session, NguyenVongXetTuyen nguyenVongXetTuyen) {
        session.save(nguyenVongXetTuyen);
    }


    public NguyenVongXetTuyen getWithSession(Session session, int id){
        NguyenVongXetTuyen nguyenVongXetTuyen = session.get(NguyenVongXetTuyen.class, id);
        return nguyenVongXetTuyen;
    }

    public List<NguyenVongXetTuyen> getAllWithSession(Session session){
        String query = "FROM NguyenVongXetTuyen ";
        List listNguyenVongXetTuyen = session.createQuery(query).list();
        return listNguyenVongXetTuyen;
    }

    public void updateWithSession(Session session, NguyenVongXetTuyen newNguyenVongXetTuyen) {
        session.merge(newNguyenVongXetTuyen);
    }

    public void deleteWithSession(Session session, NguyenVongXetTuyen nguyenVongXetTuyen) {
        session.detach(nguyenVongXetTuyen);
    }
}
