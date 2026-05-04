package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.DiemCongXetTuyenDAO;
import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
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

    private DiemCongXetTuyenDTO toDTO(DiemCongXetTuyen entity){
        return new DiemCongXetTuyenDTO(
                entity.getIdDiemCong(),
                entity.getTsCccd(),
                entity.getMaNganh(),
                entity.getMaToHop(),
                entity.getPhuongThuc(),
                entity.getDiemCC(),
                entity.getDiemUtxt(),
                entity.getDiemTong(),
                entity.getGhiChu(),
                entity.getDcKeys()
        );
    }

    private List<DiemCongXetTuyenDTO> mapListEntityToListDTO(List<DiemCongXetTuyen> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addDiemCongXetTuyen(DiemCongXetTuyenDTO diemCongXetTuyenDTO){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            DiemCongXetTuyen diemCongXetTuyen = new DiemCongXetTuyen();
            diemCongXetTuyen.setTsCccd(diemCongXetTuyenDTO.getTsCccd());
            diemCongXetTuyen.setMaNganh(diemCongXetTuyenDTO.getMaNganh());
            diemCongXetTuyen.setMaToHop(diemCongXetTuyenDTO.getMaToHop());
            diemCongXetTuyen.setPhuongThuc(diemCongXetTuyenDTO.getPhuongThuc());
            diemCongXetTuyen.setDiemCC(diemCongXetTuyenDTO.getDiemCC());
            diemCongXetTuyen.setDiemUtxt(diemCongXetTuyenDTO.getDiemUtxt());
            diemCongXetTuyen.setDiemTong(diemCongXetTuyenDTO.getDiemTong());
            diemCongXetTuyen.setGhiChu(diemCongXetTuyenDTO.getGhiChu());
            diemCongXetTuyen.setDcKeys(diemCongXetTuyenDTO.getDcKeys());

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

    public DiemCongXetTuyenDTO getDiemCongXetTuyen(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<DiemCongXetTuyenDTO> getAllDiemCongXetTuyen(){
        Session session = factory.openSession();
        List<DiemCongXetTuyen> listDiemCongXetTuyen = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listDiemCongXetTuyen);
    }

    public String updateDiemCongXetTuyen(int id, DiemCongXetTuyenDTO newDiemCongXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);

            if(diemCongXetTuyen == null){
                return "Lỗi: Không tìm thấy bảng điểm cộng với ID " + id;
            }

            diemCongXetTuyen.setTsCccd(newDiemCongXetTuyenDTO.getTsCccd());
            diemCongXetTuyen.setMaNganh(newDiemCongXetTuyenDTO.getMaNganh());
            diemCongXetTuyen.setMaToHop(newDiemCongXetTuyenDTO.getMaToHop());
            diemCongXetTuyen.setPhuongThuc(newDiemCongXetTuyenDTO.getPhuongThuc());
            diemCongXetTuyen.setDiemCC(newDiemCongXetTuyenDTO.getDiemCC());
            diemCongXetTuyen.setDiemUtxt(newDiemCongXetTuyenDTO.getDiemUtxt());
            diemCongXetTuyen.setDiemTong(newDiemCongXetTuyenDTO.getDiemTong());
            diemCongXetTuyen.setGhiChu(newDiemCongXetTuyenDTO.getGhiChu());
            diemCongXetTuyen.setDcKeys(newDiemCongXetTuyenDTO.getDcKeys());

            dao.updateWithSession(session, diemCongXetTuyen);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteDiemCongXetTuyen(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);

            if(diemCongXetTuyen == null){
                return "Lỗi: Không tìm thấy bảng điểm cộng với ID " + id;
            }

            dao.deleteWithSession(session, diemCongXetTuyen);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
}
