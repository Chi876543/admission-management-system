package com.admissionManagement.core.dao;

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

    public void addNguyenVongXetTuyen(NguyenVongXetTuyen nguyenVongXetTuyen) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(nguyenVongXetTuyen);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<NguyenVongXetTuyen> getAllNguyenVongXetTuyen(){
        Session session = factory.openSession();
        String query = "FROM NguyenVongXetTuyen";
        List listNguyenVongXetTuyen = session.createQuery(query).list();
        session.close();
        return listNguyenVongXetTuyen;
    }

    public void updateNguyenVongXetTuyen(int id, NguyenVongXetTuyen newNguyenVongXetTuyen) {
        Session session = factory.openSession();
        Transaction tx = null;
        NguyenVongXetTuyen nguyenVongXetTuyen= session.get(NguyenVongXetTuyen.class, id);
        try {
            tx = session.beginTransaction();
            if(nguyenVongXetTuyen != null) {
                nguyenVongXetTuyen.setCccd(newNguyenVongXetTuyen.getCccd());
                nguyenVongXetTuyen.setMaNganh(newNguyenVongXetTuyen.getMaNganh());
                nguyenVongXetTuyen.setThuTu(newNguyenVongXetTuyen.getThuTu());
                nguyenVongXetTuyen.setDiemThxt(newNguyenVongXetTuyen.getDiemThxt());
                nguyenVongXetTuyen.setDiemUtqd(newNguyenVongXetTuyen.getDiemUtqd());
                nguyenVongXetTuyen.setDiemCong(newNguyenVongXetTuyen.getDiemCong());
                nguyenVongXetTuyen.setDiemXetTuyen(newNguyenVongXetTuyen.getDiemXetTuyen());
                nguyenVongXetTuyen.setKetQua(newNguyenVongXetTuyen.getKetQua());
                nguyenVongXetTuyen.setNvKeys(newNguyenVongXetTuyen.getNvKeys());
                nguyenVongXetTuyen.setPhuongThuc(newNguyenVongXetTuyen.getPhuongThuc());
                nguyenVongXetTuyen.setThm(newNguyenVongXetTuyen.getThm());
            }
                tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteNguyenVongXetTuyen(int id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            NguyenVongXetTuyen nguyenVongXetTuyen = session.get(NguyenVongXetTuyen.class,id);
            if (nguyenVongXetTuyen != null) session.delete(nguyenVongXetTuyen);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
