package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.DiemCongXetTuyenDAO;
import com.admissionManagement.core.dao.NganhDAO;
import com.admissionManagement.core.dao.ThiSinhDAO;
import com.admissionManagement.core.dao.ToHopMonThiDAO;
import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.entity.DiemCongXetTuyen;
import com.admissionManagement.core.entity.Nganh;
import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.entity.ToHopMonThi;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiemCongXetTuyenBUS {
    private final DiemCongXetTuyenDAO dao;
    private final ThiSinhDAO thisinhdao;
    private final NganhDAO nganhdao;
    private final ToHopMonThiDAO tohopdao;
    private final SessionFactory factory;

    public DiemCongXetTuyenBUS() {
        this.dao = new DiemCongXetTuyenDAO();
        this.thisinhdao = new ThiSinhDAO();
        this.nganhdao = new NganhDAO();
        this.tohopdao = new ToHopMonThiDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private DiemCongXetTuyenDTO toDTO(DiemCongXetTuyen entity){
        return new DiemCongXetTuyenDTO(
                entity.getIdDiemCong(),
                entity.getThiSinh().getCccd(),
                entity.getNganh().getMaNganh(),
                entity.getToHopMonThi().getMaToHop(),
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

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, diemCongXetTuyenDTO.getTsCccd());
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, diemCongXetTuyenDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopdao.getByMaToHopWithSession(session, diemCongXetTuyenDTO.getMaToHop());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + diemCongXetTuyenDTO.getTsCccd();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + diemCongXetTuyenDTO.getMaToHop();
            }
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + diemCongXetTuyenDTO.getMaNganh();
            }

            DiemCongXetTuyen diemCongXetTuyen = new DiemCongXetTuyen();
            diemCongXetTuyen.setThiSinh(thiSinhGoc);
            diemCongXetTuyen.setNganh(nganhGoc);
            diemCongXetTuyen.setToHopMonThi(toHopGoc);
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

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, newDiemCongXetTuyenDTO.getTsCccd());
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, newDiemCongXetTuyenDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopdao.getByMaToHopWithSession(session, newDiemCongXetTuyenDTO.getMaToHop());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + newDiemCongXetTuyenDTO.getTsCccd();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + newDiemCongXetTuyenDTO.getMaToHop();
            }
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + newDiemCongXetTuyenDTO.getMaNganh();
            }

            DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);

            if(diemCongXetTuyen == null){
                return "Lỗi: Không tìm thấy bảng điểm cộng với ID " + id;
            }

            diemCongXetTuyen.setThiSinh(thiSinhGoc);
            diemCongXetTuyen.setNganh(nganhGoc);
            diemCongXetTuyen.setToHopMonThi(toHopGoc);
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
