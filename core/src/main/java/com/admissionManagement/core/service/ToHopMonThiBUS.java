package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.BangQuyDoiDAO;
import com.admissionManagement.core.dao.ToHopMonThiDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.ToHopMonThiDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.entity.ToHopMonThi;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ToHopMonThiBUS {

    private final ToHopMonThiDAO dao;
    private final SessionFactory factory;

    public ToHopMonThiBUS() {
        this.dao = new ToHopMonThiDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private ToHopMonThiDTO toDTO(ToHopMonThi entity){
        return new ToHopMonThiDTO(
                entity.getIdToHop(),
                entity.getMaToHop(),
                entity.getMon1(),
                entity.getMon2(),
                entity.getMon3(),
                entity.getTenToHop()
        );
    }

    private List<ToHopMonThiDTO> mapListEntityToListDTO(List<ToHopMonThi> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addToHopMonThi(ToHopMonThi toHopMonThi){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            dao.addWithSession(session, toHopMonThi);

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

    public ToHopMonThi getToHopMonThi(int id){
        Session session = factory.openSession();
        ToHopMonThi toHopMonThi = dao.getWithSession(session, id);
        session.close();
        return toHopMonThi;
    }

    public List<ToHopMonThi> getAllToHopMonThi(){
        Session session = factory.openSession();
        List<ToHopMonThi> listToHopMonThi = dao.getAllWithSession(session);
        session.close();
        return listToHopMonThi;
    }

    public String updateToHopMonThi(int id, ToHopMonThi newToHopMonThi){
        Session session = factory.openSession();
        Transaction tx = null;
        ToHopMonThi toHopMonThi = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            if(toHopMonThi != null) {
                toHopMonThi.setMaToHop(newToHopMonThi.getMaToHop());
                toHopMonThi.setMon1(newToHopMonThi.getMon1());
                toHopMonThi.setMon2(newToHopMonThi.getMon2());
                toHopMonThi.setMon3(newToHopMonThi.getMon3());
                toHopMonThi.setTenToHop(newToHopMonThi.getTenToHop());
            }

            dao.updateWithSession(session, toHopMonThi);

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

    public String deletetToHopMonThi(int id){
        Session session = factory.openSession();
        Transaction tx = null;
        ToHopMonThi toHopMonThi = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            dao.deleteWithSession(session, toHopMonThi);

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
