package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.NguyenVongXetTuyenDAO;
import com.admissionManagement.core.entity.NguyenVongXetTuyen;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class NguyenVongXetTuyenBUS {

    private final NguyenVongXetTuyenDAO dao;
    private final SessionFactory factory;


    public NguyenVongXetTuyenBUS() {
        this.dao = new NguyenVongXetTuyenDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    public String addNguyenVongXetTuyen(NguyenVongXetTuyen nguyenVongXetTuyen){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            dao.addWithSession(session, nguyenVongXetTuyen);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        } finally {
            session.close();
        }
    }

    public NguyenVongXetTuyen getNguyenVongXetTuyen(int id){
        Session session = factory.openSession();
        NguyenVongXetTuyen nguyenVongXetTuyen = dao.getWithSession(session, id);
        session.close();
        return nguyenVongXetTuyen;
    }

    public List<NguyenVongXetTuyen> getAllNganhToHop(){
        Session session = factory.openSession();
        List<NguyenVongXetTuyen> listNguyenVongXetTuyen = dao.getAllWithSession(session);
        session.close();
        return listNguyenVongXetTuyen;
    }

    public String updateNguyenVongXetTuyen(int id, NguyenVongXetTuyen newNguyenVongXetTuyen){
        Session session = factory.openSession();
        Transaction tx = null;
        NguyenVongXetTuyen nguyenVongXetTuyen = dao.getWithSession(session, id);
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

            dao.updateWithSession(session, nguyenVongXetTuyen);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        } finally {
            session.close();
        }
    }

    public String deleteNguyenVongXetTuyen(int id){
        Session session = factory.openSession();
        Transaction tx = null;
        NguyenVongXetTuyen nguyenVongXetTuyen = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            dao.deleteWithSession(session, nguyenVongXetTuyen);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        } finally {
            session.close();
        }
    }

}
