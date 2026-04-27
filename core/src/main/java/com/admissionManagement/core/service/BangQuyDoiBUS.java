package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.BangQuyDoiDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class BangQuyDoiBUS {
    private final BangQuyDoiDAO dao;
    private final SessionFactory factory;

    public BangQuyDoiBUS() {
        this.dao = new BangQuyDoiDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private BangQuyDoiDTO toDTO(BangQuyDoi entity){
        return new BangQuyDoiDTO(
                entity.getIdqd(),
                entity.getPhuongThuc(),
                entity.getToHop(),
                entity.getMon(),
                entity.getDiemA(),
                entity.getDiemB(),
                entity.getDiemC(),
                entity.getDiemD(),
                entity.getMaQuyDoi(),
                entity.getPhanVi()
        );
    }

    private List<BangQuyDoiDTO> mapListEntityToListDTO(List<BangQuyDoi> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addBangQuyDoi(BangQuyDoi bangQuyDoi){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            dao.addWithSession(session, bangQuyDoi);

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

    public BangQuyDoi getBangQuyDoi(int id){
        Session session = factory.openSession();
        BangQuyDoi bangQuyDoi = dao.getWithSession(session, id);
        session.close();
        return bangQuyDoi;
    }

    public List<BangQuyDoi> getAllBangQuyDoi(){
        Session session = factory.openSession();
        List<BangQuyDoi> listBangQuyDoi = dao.getAllWithSession(session);
        session.close();
        return listBangQuyDoi;
    }

    public String updateBangQuyDoi(int id, BangQuyDoi newBangQuyDoi){
        Session session = factory.openSession();
        Transaction tx = null;
        BangQuyDoi bangQuyDoi = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            if (bangQuyDoi != null) {
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

            dao.updateWithSession(session, bangQuyDoi);

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

    public String deletetBangQuyDoi(int id){
        Session session = factory.openSession();
        Transaction tx = null;
        BangQuyDoi bangQuyDoi = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            dao.deleteWithSession(session, bangQuyDoi);

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
