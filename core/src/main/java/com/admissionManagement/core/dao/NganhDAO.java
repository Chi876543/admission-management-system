package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.Nganh;
import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class NganhDAO {
    private final SessionFactory factory;

    public NganhDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addNganh(Nganh nganh) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(nganh);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<Nganh> getAllNganh(){
        Session session = factory.openSession();
        String query = "FROM Nganh";
        List listNganh = session.createQuery(query).list();
        session.close();
        return listNganh;
    }

    public void updateNganh(int id, Nganh newNganh) {
        Session session = factory.openSession();
        Transaction tx = null;
        Nganh nganh = session.get(Nganh.class, id);
        try {
            tx = session.beginTransaction();
            if(nganh != null){
                nganh.setMaNganh(newNganh.getMaNganh());
                nganh.setTenNganh(newNganh.getTenNganh());
                nganh.setToHopGoc(newNganh.getToHopGoc());
                nganh.setChiTieu(newNganh.getChiTieu());
                nganh.setDiemSan(newNganh.getDiemSan());
                nganh.setDiemTrungTuyen(newNganh.getDiemTrungTuyen());
                nganh.setTuyenThang(newNganh.getTuyenThang());
                nganh.setDgnl(newNganh.getDgnl());
                nganh.setThpt(newNganh.getThpt());
                nganh.setVsat(newNganh.getVsat());
                nganh.setSlXtt(newNganh.getSlXtt());
                nganh.setSlDgnl(newNganh.getSlDgnl());
                nganh.setSlThpt(newNganh.getSlThpt());
                nganh.setSlVsat(newNganh.getSlVsat());
            }
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteNganh(int id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Nganh nganh = session.get(Nganh.class,id);
            if (nganh != null) session.delete(nganh);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
