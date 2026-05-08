package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.NganhDAO;
import com.admissionManagement.core.dao.NguyenVongXetTuyenDAO;
import com.admissionManagement.core.dao.ThiSinhDAO;
import com.admissionManagement.core.dao.ToHopMonThiDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.entity.*;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class NguyenVongXetTuyenBUS {

    private final NguyenVongXetTuyenDAO dao;
    private final ThiSinhDAO thisinhdao;
    private final NganhDAO nganhdao;
    private final ToHopMonThiDAO tohopdao;
    private final SessionFactory factory;

    public NguyenVongXetTuyenBUS() {
        this.dao = new NguyenVongXetTuyenDAO();
        this.thisinhdao = new ThiSinhDAO();
        this.nganhdao = new NganhDAO();
        this.tohopdao = new ToHopMonThiDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private NguyenVongXetTuyenDTO toDTO(NguyenVongXetTuyen entity){
        return new NguyenVongXetTuyenDTO(
                entity.getIdNv(),
                entity.getThiSinh().getCccd(),
                entity.getNganh().getMaNganh(),
                entity.getThuTu(),
                entity.getDiemThxt(),
                entity.getDiemUtqd(),
                entity.getDiemCong(),
                entity.getDiemXetTuyen(),
                entity.getKetQua(),
                entity.getNvKeys(),
                entity.getPhuongThuc(),
                entity.getToHopMonThi().getMaToHop()
        );
    }

    private List<NguyenVongXetTuyenDTO> mapListEntityToListDTO(List<NguyenVongXetTuyen> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addNguyenVongXetTuyen(NguyenVongXetTuyenDTO nguyenVongXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, nguyenVongXetTuyenDTO.getCccd());
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, nguyenVongXetTuyenDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopdao.getByMaToHopWithSession(session, nguyenVongXetTuyenDTO.getThm());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + nguyenVongXetTuyenDTO.getCccd();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + nguyenVongXetTuyenDTO.getThm();
            }
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + nguyenVongXetTuyenDTO.getMaNganh();
            }

            NguyenVongXetTuyen nguyenVongXetTuyen = new NguyenVongXetTuyen();
            nguyenVongXetTuyen.setThiSinh(thiSinhGoc);
            nguyenVongXetTuyen.setNganh(nganhGoc);
            nguyenVongXetTuyen.setThuTu(nguyenVongXetTuyenDTO.getThuTu());
            nguyenVongXetTuyen.setDiemThxt(nguyenVongXetTuyenDTO.getDiemThxt());
            nguyenVongXetTuyen.setDiemUtqd(nguyenVongXetTuyenDTO.getDiemUtqd());
            nguyenVongXetTuyen.setDiemCong(nguyenVongXetTuyenDTO.getDiemCong());
            nguyenVongXetTuyen.setDiemXetTuyen(nguyenVongXetTuyenDTO.getDiemXetTuyen());
            nguyenVongXetTuyen.setKetQua(nguyenVongXetTuyenDTO.getKetQua());
            nguyenVongXetTuyen.setNvKeys(nguyenVongXetTuyenDTO.getNvKeys());
            nguyenVongXetTuyen.setPhuongThuc(nguyenVongXetTuyenDTO.getPhuongThuc());
            nguyenVongXetTuyen.setToHopMonThi(toHopGoc);

            dao.addWithSession(session, nguyenVongXetTuyen);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public NguyenVongXetTuyenDTO getNguyenVongXetTuyen(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<NguyenVongXetTuyenDTO> getAllNganhToHop(){
        Session session = factory.openSession();
        List<NguyenVongXetTuyen> listNguyenVongXetTuyen = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listNguyenVongXetTuyen);
    }

    public String updateNguyenVongXetTuyen(int id, NguyenVongXetTuyenDTO newNguyenVongXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, newNguyenVongXetTuyenDTO.getCccd());
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, newNguyenVongXetTuyenDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopdao.getByMaToHopWithSession(session, newNguyenVongXetTuyenDTO.getThm());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + newNguyenVongXetTuyenDTO.getCccd();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + newNguyenVongXetTuyenDTO.getThm();
            }
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + newNguyenVongXetTuyenDTO.getMaNganh();
            }

            NguyenVongXetTuyen nguyenVongXetTuyen = dao.getWithSession(session, id);

            if(nguyenVongXetTuyen == null){
                return "Lỗi: Không tìm thấy Nguyện vọng với ID " + id;
            }

            nguyenVongXetTuyen.setThiSinh(thiSinhGoc);
            nguyenVongXetTuyen.setNganh(nganhGoc);
            nguyenVongXetTuyen.setThuTu(newNguyenVongXetTuyenDTO.getThuTu());
            nguyenVongXetTuyen.setDiemThxt(newNguyenVongXetTuyenDTO.getDiemThxt());
            nguyenVongXetTuyen.setDiemUtqd(newNguyenVongXetTuyenDTO.getDiemUtqd());
            nguyenVongXetTuyen.setDiemCong(newNguyenVongXetTuyenDTO.getDiemCong());
            nguyenVongXetTuyen.setDiemXetTuyen(newNguyenVongXetTuyenDTO.getDiemXetTuyen());
            nguyenVongXetTuyen.setKetQua(newNguyenVongXetTuyenDTO.getKetQua());
            nguyenVongXetTuyen.setNvKeys(newNguyenVongXetTuyenDTO.getNvKeys());
            nguyenVongXetTuyen.setPhuongThuc(newNguyenVongXetTuyenDTO.getPhuongThuc());
            nguyenVongXetTuyen.setToHopMonThi(toHopGoc);

            dao.updateWithSession(session, nguyenVongXetTuyen);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteNguyenVongXetTuyen(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            NguyenVongXetTuyen nguyenVongXetTuyen = dao.getWithSession(session, id);

            if(nguyenVongXetTuyen == null){
                return "Lỗi: Không tìm thấy Nguyện vọng với ID " + id;
            }

            dao.deleteWithSession(session, nguyenVongXetTuyen);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

}
