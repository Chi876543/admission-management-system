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

    public String addToHopMonThi(ToHopMonThiDTO toHopMonThiDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ToHopMonThi toHopMonThi = new ToHopMonThi();
            toHopMonThi.setMaToHop(toHopMonThiDTO.getMaToHop());
            toHopMonThi.setMon1(toHopMonThiDTO.getMon1());
            toHopMonThi.setMon2(toHopMonThiDTO.getMon2());
            toHopMonThi.setMon3(toHopMonThiDTO.getMon3());
            toHopMonThi.setTenToHop(toHopMonThiDTO.getTenToHop());

            dao.addWithSession(session, toHopMonThi);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public ToHopMonThiDTO getToHopMonThi(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<ToHopMonThiDTO> getAllToHopMonThi(){
        Session session = factory.openSession();
        List<ToHopMonThi> listToHopMonThi = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listToHopMonThi);
    }

    public String updateToHopMonThi(int id, ToHopMonThiDTO newToHopMonThiDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            ToHopMonThi toHopMonThi = dao.getWithSession(session, id);

            if(toHopMonThi == null){
                return "Lỗi: Không tìm thấy tổ hợp môn với ID " + id;
            }

            toHopMonThi.setMaToHop(newToHopMonThiDTO.getMaToHop());
            toHopMonThi.setMon1(newToHopMonThiDTO.getMon1());
            toHopMonThi.setMon2(newToHopMonThiDTO.getMon2());
            toHopMonThi.setMon3(newToHopMonThiDTO.getMon3());
            toHopMonThi.setTenToHop(newToHopMonThiDTO.getTenToHop());

            dao.updateWithSession(session, toHopMonThi);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteToHopMonThi(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            ToHopMonThi toHopMonThi = dao.getWithSession(session, id);

            if(toHopMonThi == null){
                return "Lỗi: Không tìm thấy tổ hợp môn với ID " + id;
            }

            dao.deleteWithSession(session, toHopMonThi);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
}
