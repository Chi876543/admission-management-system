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

    public String addBangQuyDoi(BangQuyDoiDTO bangQuyDoiDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            BangQuyDoi bangQuyDoi = new BangQuyDoi();
            bangQuyDoi.setPhuongThuc(bangQuyDoiDTO.getPhuongThuc());
            bangQuyDoi.setToHop(bangQuyDoiDTO.getToHop());
            bangQuyDoi.setMon(bangQuyDoiDTO.getMon());
            bangQuyDoi.setDiemA(bangQuyDoiDTO.getDiemA());
            bangQuyDoi.setDiemB(bangQuyDoiDTO.getDiemB());
            bangQuyDoi.setDiemC(bangQuyDoiDTO.getDiemC());
            bangQuyDoi.setDiemD(bangQuyDoiDTO.getDiemD());
            bangQuyDoi.setMaQuyDoi(bangQuyDoiDTO.getMaQuyDoi());
            bangQuyDoi.setPhanVi(bangQuyDoiDTO.getPhanVi());

            dao.addWithSession(session, bangQuyDoi);
            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public BangQuyDoiDTO getBangQuyDoi(int id){
        try(Session session = factory.openSession()) {
            return toDTO(dao.getWithSession(session, id));
        }
    }


    public List<BangQuyDoiDTO> getAllBangQuyDoi(){
        Session session = factory.openSession();
        List<BangQuyDoi> listBangQuyDoi = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listBangQuyDoi);
    }

    public String updateBangQuyDoi(int id, BangQuyDoiDTO newBangQuyDoiDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            BangQuyDoi bangQuyDoi = dao.getWithSession(session, id);

            if(bangQuyDoi == null){
                return "Lỗi: Không tìm thấy bảng quy đổi với ID " + id;
            }

            bangQuyDoi.setPhuongThuc(newBangQuyDoiDTO.getPhuongThuc());
            bangQuyDoi.setToHop(newBangQuyDoiDTO.getToHop());
            bangQuyDoi.setMon(newBangQuyDoiDTO.getMon());
            bangQuyDoi.setDiemA(newBangQuyDoiDTO.getDiemA());
            bangQuyDoi.setDiemB(newBangQuyDoiDTO.getDiemB());
            bangQuyDoi.setDiemC(newBangQuyDoiDTO.getDiemC());
            bangQuyDoi.setDiemD(newBangQuyDoiDTO.getDiemD());
            bangQuyDoi.setMaQuyDoi(newBangQuyDoiDTO.getMaQuyDoi());
            bangQuyDoi.setPhanVi(newBangQuyDoiDTO.getPhanVi());

            dao.updateWithSession(session, bangQuyDoi);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteBangQuyDoi(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            BangQuyDoi bangQuyDoi = dao.getWithSession(session, id);

            if (bangQuyDoi == null) {
                return "Lỗi: Không tìm thấy bảng quy đổi với ID " + id;
            }

            dao.deleteWithSession(session, bangQuyDoi);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
}
