package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.DiemCongXetTuyen;
import com.admissionManagement.core.entity.DiemCongXetTuyen;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class DiemCongXetTuyenDAO {
    private final SessionFactory factory;

    public DiemCongXetTuyenDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addDiemCongXetTuyen(DiemCongXetTuyen diemCongXetTuyen) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(diemCongXetTuyen);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<DiemCongXetTuyen> getAllDiemCongXetTuyen(){
        Session session = factory.openSession();
        String query = "FROM DiemCongXetTuyen";
        List listDiemCongXetTuyen = session.createQuery(query).list();
        session.close();
        return listDiemCongXetTuyen;
    }

    public void updateDiemCongXetTuyen(int id, DiemCongXetTuyen newDiemCongXetTuyen) {
        Session session = factory.openSession();
        Transaction tx = null;
        DiemCongXetTuyen diemCongXetTuyen = session.get(DiemCongXetTuyen.class, id);
        try {
            tx = session.beginTransaction();
            if(diemCongXetTuyen != null){
                diemCongXetTuyen.setTsCccd(newDiemCongXetTuyen.getTsCccd());
                diemCongXetTuyen.setMaNganh(newDiemCongXetTuyen.getMaNganh());
                diemCongXetTuyen.setMaToHop(newDiemCongXetTuyen.getMaToHop());
                diemCongXetTuyen.setPhuongThuc(newDiemCongXetTuyen.getPhuongThuc());
                diemCongXetTuyen.setDiemCC(newDiemCongXetTuyen.getDiemCC());
                diemCongXetTuyen.setDiemUtxt(newDiemCongXetTuyen.getDiemUtxt());
                diemCongXetTuyen.setDiemTong(newDiemCongXetTuyen.getDiemTong());
                diemCongXetTuyen.setGhiChu(newDiemCongXetTuyen.getGhiChu());
                diemCongXetTuyen.setDcKeys(newDiemCongXetTuyen.getDcKeys());
            }
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteDiemCongXetTuyen(int id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            DiemCongXetTuyen diemCongXetTuyen = session.get(DiemCongXetTuyen.class,id);
            if (diemCongXetTuyen != null) session.delete(diemCongXetTuyen);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
