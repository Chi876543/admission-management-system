package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class BangQuyDoiDAO {
    private final SessionFactory factory;

    public BangQuyDoiDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addBangQuyDoi(BangQuyDoi bangQuyDoi) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(bangQuyDoi);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<BangQuyDoi> getAllBangQuyDoi(){
        Session session = factory.openSession();
        String query = "FROM BangQuyDoi";
        List listBangQuyDoi = session.createQuery(query).list();
        session.close();
        return listBangQuyDoi;
    }

    public void updateBangQuyDoi(int id, BangQuyDoi newBangQuyDoi) {
        Session session = factory.openSession();
        Transaction tx = null;
        BangQuyDoi bangQuyDoi = session.get(BangQuyDoi.class, id);
        try {
            tx = session.beginTransaction();
            if(bangQuyDoi != null){
                bangQuyDoi.setPhuongThuc(newBangQuyDoi.getPhuongThuc());
                bangQuyDoi.setToHop(newBangQuyDoi.getToHop());
                bangQuyDoi.setMon(newBangQuyDoi.getMon());
                bangQuyDoi.setDiemA(newBangQuyDoi.getDiemA());
                bangQuyDoi.setDiemB(newBangQuyDoi.getDiemB());
                bangQuyDoi.setDiemC(newBangQuyDoi.getDiemC());
                bangQuyDoi.setDiemD(newBangQuyDoi.getDiemD());
                bangQuyDoi.setMaQuyDoi(newBangQuyDoi.getMaQuyDoi());
                bangQuyDoi.setPhanVi(newBangQuyDoi.getPhanVi());
            }
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteBangQuyDoi(int id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            BangQuyDoi bangQuyDoi = session.get(BangQuyDoi.class,id);
            if (bangQuyDoi != null) session.delete(bangQuyDoi);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
