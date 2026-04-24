package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.DiemCongXetTuyenDAO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.DiemCongXetTuyen;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class DiemCongXetTuyenBUS {
    private DiemCongXetTuyenDAO dao;
    private final SessionFactory factory;

    public DiemCongXetTuyenBUS() {
        this.dao = new DiemCongXetTuyenDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    public String addDiemCongXetTuyen(DiemCongXetTuyen diemCongXetTuyen){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            dao.addWithSession(session, diemCongXetTuyen);

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

    public DiemCongXetTuyen getDiemCongXetTuyen(int id){
        Session session = factory.openSession();
        DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);
        session.close();
        return diemCongXetTuyen;
    }

    public List<DiemCongXetTuyen> getAllDiemCongXetTuyen(){
        Session session = factory.openSession();
        List<DiemCongXetTuyen> listDiemCongXetTuyen = dao.getAllWithSession(session);
        session.close();
        return listDiemCongXetTuyen;
    }

    public String updateDiemCongXetTuyen(int id, DiemCongXetTuyen newDiemCongXetTuyen){
        Session session = factory.openSession();
        Transaction tx = null;
        DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);
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

            dao.updateWithSession(session, diemCongXetTuyen);

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

    public String deleteDiemCongXetTuyen(int id){
        Session session = factory.openSession();
        Transaction tx = null;
        DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            dao.deleteWithSession(session, diemCongXetTuyen);

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
