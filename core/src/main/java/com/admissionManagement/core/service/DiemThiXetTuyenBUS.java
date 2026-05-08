package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.DiemThiXetTuyenDAO;
import com.admissionManagement.core.dao.ThiSinhDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.DiemThiXetTuyenDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.DiemThiXetTuyen;
import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiemThiXetTuyenBUS {
    private final DiemThiXetTuyenDAO dao;
    private final ThiSinhDAO thisinhdao;
    private final SessionFactory factory;

    public DiemThiXetTuyenBUS() {
        this.dao = new DiemThiXetTuyenDAO();
        this.thisinhdao = new ThiSinhDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private DiemThiXetTuyenDTO toDTO(DiemThiXetTuyen entity){
        return new DiemThiXetTuyenDTO(
                entity.getIdDiemThi(),
                entity.getThiSinh().getCccd(),
                entity.getSoBaoDanh(),
                entity.getPhuongThuc(),
                entity.getDiemToan(),
                entity.getDiemLy(),
                entity.getDiemHoa(),
                entity.getDiemSinh(),
                entity.getDiemSu(),
                entity.getDiemDia(),
                entity.getDiemVan(),
                entity.getDiemTin(),
                entity.getDiemKtpl(),
                entity.getN1Thi(),
                entity.getN1Cc(),
                entity.getCncn(),
                entity.getCnnn(),
                entity.getNl1(),
                entity.getNk1(),
                entity.getNk2()
        );
    }

    private List<DiemThiXetTuyenDTO> mapListEntityToListDTO(List<DiemThiXetTuyen> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addDiemThiXetTuyen(DiemThiXetTuyenDTO diemThiXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, diemThiXetTuyenDTO.getCccd());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + diemThiXetTuyenDTO.getCccd();
            }

            DiemThiXetTuyen diemThiXetTuyen = new DiemThiXetTuyen();
            diemThiXetTuyen.setThiSinh(thiSinhGoc);
            diemThiXetTuyen.setSoBaoDanh(diemThiXetTuyenDTO.getSoBaoDanh());
            diemThiXetTuyen.setPhuongThuc(diemThiXetTuyenDTO.getPhuongThuc());
            diemThiXetTuyen.setDiemToan(diemThiXetTuyenDTO.getDiemToan());
            diemThiXetTuyen.setDiemLy(diemThiXetTuyenDTO.getDiemLy());
            diemThiXetTuyen.setDiemHoa(diemThiXetTuyenDTO.getDiemHoa());
            diemThiXetTuyen.setDiemSinh(diemThiXetTuyenDTO.getDiemSinh());
            diemThiXetTuyen.setDiemSu(diemThiXetTuyenDTO.getDiemSu());
            diemThiXetTuyen.setDiemDia(diemThiXetTuyenDTO.getDiemDia());
            diemThiXetTuyen.setDiemVan(diemThiXetTuyenDTO.getDiemVan());
            diemThiXetTuyen.setDiemTin(diemThiXetTuyenDTO.getDiemTin());
            diemThiXetTuyen.setDiemKtpl(diemThiXetTuyenDTO.getDiemKtpl());
            diemThiXetTuyen.setN1Thi(diemThiXetTuyenDTO.getN1Thi());
            diemThiXetTuyen.setN1Cc(diemThiXetTuyenDTO.getN1Cc());
            diemThiXetTuyen.setCncn(diemThiXetTuyenDTO.getCncn());
            diemThiXetTuyen.setCnnn(diemThiXetTuyenDTO.getCnnn());
            diemThiXetTuyen.setNl1(diemThiXetTuyenDTO.getNl1());
            diemThiXetTuyen.setNk1(diemThiXetTuyenDTO.getNk1());
            diemThiXetTuyen.setNk2(diemThiXetTuyenDTO.getNk2());

            dao.addWithSession(session, diemThiXetTuyen);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public DiemThiXetTuyenDTO getDiemThiXetTuyen(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<DiemThiXetTuyenDTO> getAllDiemThiXetTuyen(){
        Session session = factory.openSession();
        List<DiemThiXetTuyen> listDiemThiXetTuyen = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listDiemThiXetTuyen);
    }

    public String updateDiemThiXetTuyen(int id, DiemThiXetTuyenDTO newDiemThiXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, newDiemThiXetTuyenDTO.getCccd());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + newDiemThiXetTuyenDTO.getCccd();
            }

            DiemThiXetTuyen diemThiXetTuyen = dao.getWithSession(session, id);

            if(diemThiXetTuyen == null){
                return "Lỗi: Không tìm thấy điểm thi với ID " + id;
            }

            diemThiXetTuyen.setThiSinh(thiSinhGoc);
            diemThiXetTuyen.setSoBaoDanh(newDiemThiXetTuyenDTO.getSoBaoDanh());
            diemThiXetTuyen.setPhuongThuc(newDiemThiXetTuyenDTO.getPhuongThuc());
            diemThiXetTuyen.setDiemToan(newDiemThiXetTuyenDTO.getDiemToan());
            diemThiXetTuyen.setDiemLy(newDiemThiXetTuyenDTO.getDiemLy());
            diemThiXetTuyen.setDiemHoa(newDiemThiXetTuyenDTO.getDiemHoa());
            diemThiXetTuyen.setDiemSinh(newDiemThiXetTuyenDTO.getDiemSinh());
            diemThiXetTuyen.setDiemSu(newDiemThiXetTuyenDTO.getDiemSu());
            diemThiXetTuyen.setDiemDia(newDiemThiXetTuyenDTO.getDiemDia());
            diemThiXetTuyen.setDiemVan(newDiemThiXetTuyenDTO.getDiemVan());
            diemThiXetTuyen.setDiemTin(newDiemThiXetTuyenDTO.getDiemTin());
            diemThiXetTuyen.setDiemKtpl(newDiemThiXetTuyenDTO.getDiemKtpl());
            diemThiXetTuyen.setN1Thi(newDiemThiXetTuyenDTO.getN1Thi());
            diemThiXetTuyen.setN1Cc(newDiemThiXetTuyenDTO.getN1Cc());
            diemThiXetTuyen.setCncn(newDiemThiXetTuyenDTO.getCncn());
            diemThiXetTuyen.setCnnn(newDiemThiXetTuyenDTO.getCnnn());
            diemThiXetTuyen.setNl1(newDiemThiXetTuyenDTO.getNl1());
            diemThiXetTuyen.setNk1(newDiemThiXetTuyenDTO.getNk1());
            diemThiXetTuyen.setNk2(newDiemThiXetTuyenDTO.getNk2());

            dao.updateWithSession(session, diemThiXetTuyen);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteDiemThiXetTuyen(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            DiemThiXetTuyen diemThiXetTuyen = dao.getWithSession(session, id);

            if(diemThiXetTuyen == null){
                return "Lỗi: Không tìm thấy điểm thi với ID " + id;
            }

            dao.deleteWithSession(session, diemThiXetTuyen);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

}
