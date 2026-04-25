package com.admissionManagement.core.service;


import com.admissionManagement.core.dao.ThiSinhDAO;

import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class ThiSinhBUS {
    private final ThiSinhDAO dao;
    private final SessionFactory factory;

    public ThiSinhBUS() {
        this.dao = new ThiSinhDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    public String addThiSinh(ThiSinh thiSinh){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            dao.addWithSession(session, thiSinh);

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

    public ThiSinh getThiSinh(int id){
        Session session = factory.openSession();
        ThiSinh thiSinh = dao.getWithSession(session, id);
        session.close();
        return thiSinh;
    }

    public List<ThiSinh> getAllThiSinh(){
        Session session = factory.openSession();
        List<ThiSinh> listThiSinh = dao.getAllWithSession(session);
        session.close();
        return listThiSinh;
    }

    public String updateThiSinh(int id, ThiSinh newThiSinh){
        Session session = factory.openSession();
        Transaction tx = null;
        ThiSinh thiSinh = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            if(thiSinh != null){
                thiSinh.setGioiTinh(newThiSinh.getGioiTinh());
                thiSinh.setCccd(newThiSinh.getCccd());
                thiSinh.setSoBaoDanh(newThiSinh.getSoBaoDanh());
                thiSinh.setHo(newThiSinh.getHo());
                thiSinh.setTen(newThiSinh.getTen());
                thiSinh.setDienThoai(newThiSinh.getDienThoai());
                thiSinh.setEmail(newThiSinh.getEmail());
                thiSinh.setPassword(newThiSinh.getPassword());
                thiSinh.setNgaySinh(newThiSinh.getNgaySinh());
                thiSinh.setNoiSinh(newThiSinh.getNoiSinh());
                thiSinh.setDoiTuong(newThiSinh.getDoiTuong());
                thiSinh.setKhuVuc(newThiSinh.getKhuVuc());
                thiSinh.setUpdatedAt(LocalDate.now());
            }

            dao.updateWithSession(session, thiSinh);

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

    public String deletetThiSinh(int id){
        Session session = factory.openSession();
        Transaction tx = null;
        ThiSinh thiSinh = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            dao.deleteWithSession(session, thiSinh);

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
