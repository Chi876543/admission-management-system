package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.NganhDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.Nganh;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class NganhBUS {
    private final NganhDAO dao;
    private final SessionFactory factory;

    public NganhBUS() {
        this.dao = new NganhDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private NganhDTO toDTO(Nganh entity){
        return new NganhDTO(
                entity.getIdNganh(),
                entity.getMaNganh(),
                entity.getTenNganh(),
                entity.getToHopGoc(),
                entity.getChiTieu(),
                entity.getDiemSan(),
                entity.getDiemTrungTuyen(),
                entity.getTuyenThang(),
                entity.getDgnl(),
                entity.getThpt(),
                entity.getVsat(),
                entity.getSlXtt(),
                entity.getSlDgnl(),
                entity.getSlVsat(),
                entity.getSlThpt()
        );
    }

    private List<NganhDTO> mapListEntityToListDTO(List<Nganh> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addBangQuyDoi(NganhDTO nganhDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Nganh nganh = new Nganh();
            nganh.setMaNganh(nganhDTO.getMaNganh());
            nganh.setTenNganh(nganhDTO.getTenNganh());
            nganh.setToHopGoc(nganhDTO.getToHopGoc());
            nganh.setChiTieu(nganhDTO.getChiTieu());
            nganh.setDiemSan(nganhDTO.getDiemSan());
            nganh.setDiemTrungTuyen(nganhDTO.getDiemTrungTuyen());
            nganh.setTuyenThang(nganhDTO.getTuyenThang());
            nganh.setDgnl(nganhDTO.getDgnl());
            nganh.setThpt(nganhDTO.getThpt());
            nganh.setVsat(nganhDTO.getVsat());
            nganh.setSlXtt(nganhDTO.getSlXtt());
            nganh.setSlDgnl(nganhDTO.getSlDgnl());
            nganh.setSlThpt(nganhDTO.getSlThpt());
            nganh.setSlVsat(nganhDTO.getSlVsat());

            dao.addWithSession(session, nganh);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public NganhDTO getNganh(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<NganhDTO> getAllNganh(){
        Session session = factory.openSession();
        List<Nganh> listNganh = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listNganh);
    }

    public String updateNganh(int id, NganhDTO newNganhDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Nganh nganh = dao.getWithSession(session, id);

            if(nganh == null){
                return "Lỗi: Không tìm thấy Ngành với ID " + id;
            }

            nganh.setMaNganh(newNganhDTO.getMaNganh());
            nganh.setTenNganh(newNganhDTO.getTenNganh());
            nganh.setToHopGoc(newNganhDTO.getToHopGoc());
            nganh.setChiTieu(newNganhDTO.getChiTieu());
            nganh.setDiemSan(newNganhDTO.getDiemSan());
            nganh.setDiemTrungTuyen(newNganhDTO.getDiemTrungTuyen());
            nganh.setTuyenThang(newNganhDTO.getTuyenThang());
            nganh.setDgnl(newNganhDTO.getDgnl());
            nganh.setThpt(newNganhDTO.getThpt());
            nganh.setVsat(newNganhDTO.getVsat());
            nganh.setSlXtt(newNganhDTO.getSlXtt());
            nganh.setSlDgnl(newNganhDTO.getSlDgnl());
            nganh.setSlThpt(newNganhDTO.getSlThpt());
            nganh.setSlVsat(newNganhDTO.getSlVsat());

            dao.updateWithSession(session, nganh);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteNganh(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Nganh nganh = dao.getWithSession(session, id);

            if(nganh == null){
                return "Lỗi: Không tìm thấy Ngành với ID " + id;
            }

            dao.deleteWithSession(session, nganh);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
}
