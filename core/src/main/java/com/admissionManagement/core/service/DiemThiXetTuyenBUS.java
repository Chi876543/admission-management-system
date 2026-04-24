package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.DiemThiXetTuyenDAO;
import com.admissionManagement.core.entity.DiemThiXetTuyen;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class DiemThiXetTuyenBUS {
    private DiemThiXetTuyenDAO dao;
    private final SessionFactory factory;

    public DiemThiXetTuyenBUS() {
        this.dao = new DiemThiXetTuyenDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    public String addDiemThiXetTuyen(DiemThiXetTuyen diemThiXetTuyen){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            dao.addWithSession(session, diemThiXetTuyen);

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

    public DiemThiXetTuyen getDiemThiXetTuyen(int id){
        Session session = factory.openSession();
        DiemThiXetTuyen diemThiXetTuyen = dao.getWithSession(session, id);
        session.close();
        return diemThiXetTuyen;
    }

    public List<DiemThiXetTuyen> getAllDiemThiXetTuyen(){
        Session session = factory.openSession();
        List<DiemThiXetTuyen> listDiemThiXetTuyen = dao.getAllWithSession(session);
        session.close();
        return listDiemThiXetTuyen;
    }

    public String updateDiemThiXetTuyen(int id, DiemThiXetTuyen newDiemThiXetTuyen){
        Session session = factory.openSession();
        Transaction tx = null;
        DiemThiXetTuyen diemThiXetTuyen = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            if(diemThiXetTuyen != null){
                diemThiXetTuyen.setCccd(newDiemThiXetTuyen.getCccd());
                diemThiXetTuyen.setSoBaoDanh(newDiemThiXetTuyen.getSoBaoDanh());
                diemThiXetTuyen.setPhuongThuc(newDiemThiXetTuyen.getPhuongThuc());
                diemThiXetTuyen.setDiemToan(newDiemThiXetTuyen.getDiemToan());
                diemThiXetTuyen.setDiemLy(newDiemThiXetTuyen.getDiemLy());
                diemThiXetTuyen.setDiemHoa(newDiemThiXetTuyen.getDiemHoa());
                diemThiXetTuyen.setDiemSinh(newDiemThiXetTuyen.getDiemSinh());
                diemThiXetTuyen.setDiemSu(newDiemThiXetTuyen.getDiemSu());
                diemThiXetTuyen.setDiemDia(newDiemThiXetTuyen.getDiemDia());
                diemThiXetTuyen.setDiemVan(newDiemThiXetTuyen.getDiemVan());
                diemThiXetTuyen.setDiemAnh(newDiemThiXetTuyen.getDiemAnh());
                diemThiXetTuyen.setDiemKtpl(newDiemThiXetTuyen.getDiemKtpl());
                diemThiXetTuyen.setN1Thi(newDiemThiXetTuyen.getN1Thi());
                diemThiXetTuyen.setN1Cc(newDiemThiXetTuyen.getN1Cc());
                diemThiXetTuyen.setCncn(newDiemThiXetTuyen.getCncn());
                diemThiXetTuyen.setCnnn(newDiemThiXetTuyen.getCnnn());
                diemThiXetTuyen.setNl1(newDiemThiXetTuyen.getNl1());
                diemThiXetTuyen.setNk1(newDiemThiXetTuyen.getNk1());
                diemThiXetTuyen.setNk2(newDiemThiXetTuyen.getNk2());
            }

            dao.updateWithSession(session, diemThiXetTuyen);

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

    public String deleteDiemThiXetTuyen(int id){
        Session session = factory.openSession();
        Transaction tx = null;
        DiemThiXetTuyen diemThiXetTuyen = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            dao.deleteWithSession(session, diemThiXetTuyen);

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
