package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ThiSinhDAO {
    private final SessionFactory factory;

    public ThiSinhDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addThiSinh(ThiSinh ts) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(ts);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<ThiSinh> getAllThiSinh(){
        Session session = factory.openSession();
        String query = "FROM ThiSinh";
        List listThiSinh = session.createQuery(query).list();
        session.close();
        return listThiSinh;
    }

    public void updateThiSinh(int id, ThiSinh newThiSinh) {
        Session session = factory.openSession();
        Transaction tx = null;
        ThiSinh thiSinh = session.get(ThiSinh.class, id);
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
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteThiSinh(int id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ThiSinh ts = session.get(ThiSinh.class,id);
            if (ts != null) session.delete(ts);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
