package com.admissionManagement.core.service;


import com.admissionManagement.core.dao.NganhDAO;
import com.admissionManagement.core.dao.NganhToHopDAO;
import com.admissionManagement.core.dao.NganhToHopDAO;

import com.admissionManagement.core.dao.ToHopMonThiDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NganhToHopDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.Nganh;
import com.admissionManagement.core.entity.NganhToHop;
import com.admissionManagement.core.entity.ToHopMonThi;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NganhToHopBUS {

    private final NganhToHopDAO dao;
    private final NganhDAO nganhdao;
    private final ToHopMonThiDAO tohopmonthidao;
    private final SessionFactory factory;

    public NganhToHopBUS() {
        this.dao = new NganhToHopDAO();
        this.nganhdao = new NganhDAO();
        this.tohopmonthidao = new ToHopMonThiDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private NganhToHopDTO toDTO(NganhToHop entity){
        return new NganhToHopDTO(
                entity.getId(),
                entity.getNganh().getMaNganh(),
                entity.getToHopMonThi().getMaToHop(),
                entity.getThMon1(),
                entity.getHsMon1(),
                entity.getThMon2(),
                entity.getHsMon2(),
                entity.getThMon3(),
                entity.getHsMon3(),
                entity.getTbKeys(),
                entity.getAnh(),
                entity.getToan(),
                entity.getLy(),
                entity.getHoa(),
                entity.getSinh(),
                entity.getVan(),
                entity.getSu(),
                entity.getDia(),
                entity.getTin(),
                entity.getKhac(),
                entity.getKtpl(),
                entity.getDoLech()
        );
    }

    private List<NganhToHopDTO> mapListEntityToListDTO(List<NganhToHop> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addNganhToHop(NganhToHopDTO nganhToHopDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, nganhToHopDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopmonthidao.getByMaToHopWithSession(session, nganhToHopDTO.getMaToHop());
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + nganhToHopDTO.getMaNganh();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + nganhToHopDTO.getMaToHop();
            }

            NganhToHop nganhToHop = new NganhToHop();
            nganhToHop.setNganh(nganhGoc);
            nganhToHop.setToHopMonThi(toHopGoc);
            nganhToHop.setHsMon1(nganhToHopDTO.getHsMon1());
            nganhToHop.setHsMon2(nganhToHopDTO.getHsMon2());
            nganhToHop.setHsMon3(nganhToHopDTO.getHsMon3());
            nganhToHop.setThMon1(nganhToHopDTO.getThMon1());
            nganhToHop.setThMon2(nganhToHopDTO.getThMon2());
            nganhToHop.setThMon3(nganhToHopDTO.getThMon3());
            nganhToHop.setTbKeys(nganhToHopDTO.getTbKeys());
            nganhToHop.setAnh(nganhToHopDTO.getAnh());
            nganhToHop.setToan(nganhToHopDTO.getToan());
            nganhToHop.setLy(nganhToHopDTO.getLy());
            nganhToHop.setHoa(nganhToHopDTO.getHoa());
            nganhToHop.setSinh(nganhToHopDTO.getSinh());
            nganhToHop.setVan(nganhToHopDTO.getVan());
            nganhToHop.setSu(nganhToHopDTO.getSu());
            nganhToHop.setDia(nganhToHopDTO.getDia());
            nganhToHop.setTin(nganhToHopDTO.getTin());
            nganhToHop.setKhac(nganhToHopDTO.getKhac());
            nganhToHop.setKtpl(nganhToHopDTO.getKtpl());
            nganhToHop.setDoLech(nganhToHopDTO.getDoLech());

            dao.addWithSession(session, nganhToHop);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public NganhToHopDTO getNganhToHop(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<NganhToHopDTO> getAllNganhToHop(){
        Session session = factory.openSession();
        List<NganhToHop> listNganhToHop = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listNganhToHop);
    }

    public String updateNganhToHop(int id, NganhToHopDTO newNganhToHopDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, newNganhToHopDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopmonthidao.getByMaToHopWithSession(session, newNganhToHopDTO.getMaToHop());
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + newNganhToHopDTO.getMaNganh();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + newNganhToHopDTO.getMaToHop();
            }

            NganhToHop nganhToHop = dao.getWithSession(session, id);
            if(nganhToHop == null){
                return "Lỗi: Không tìm thấy Ngành tổ hợp với ID " + id;
            }

            nganhToHop.setNganh(nganhGoc);
            nganhToHop.setToHopMonThi(toHopGoc);
            nganhToHop.setHsMon1(newNganhToHopDTO.getHsMon1());
            nganhToHop.setHsMon2(newNganhToHopDTO.getHsMon2());
            nganhToHop.setHsMon3(newNganhToHopDTO.getHsMon3());
            nganhToHop.setThMon1(newNganhToHopDTO.getThMon1());
            nganhToHop.setThMon2(newNganhToHopDTO.getThMon2());
            nganhToHop.setThMon3(newNganhToHopDTO.getThMon3());
            nganhToHop.setTbKeys(newNganhToHopDTO.getTbKeys());
            nganhToHop.setAnh(newNganhToHopDTO.getAnh());
            nganhToHop.setToan(newNganhToHopDTO.getToan());
            nganhToHop.setLy(newNganhToHopDTO.getLy());
            nganhToHop.setHoa(newNganhToHopDTO.getHoa());
            nganhToHop.setSinh(newNganhToHopDTO.getSinh());
            nganhToHop.setVan(newNganhToHopDTO.getVan());
            nganhToHop.setSu(newNganhToHopDTO.getSu());
            nganhToHop.setDia(newNganhToHopDTO.getDia());
            nganhToHop.setTin(newNganhToHopDTO.getTin());
            nganhToHop.setKhac(newNganhToHopDTO.getKhac());
            nganhToHop.setKtpl(newNganhToHopDTO.getKtpl());
            nganhToHop.setDoLech(newNganhToHopDTO.getDoLech());

            dao.updateWithSession(session, nganhToHop);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteNganhToHop(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            NganhToHop nganhToHop = dao.getWithSession(session, id);

            if(nganhToHop == null){
                return "Lỗi: Không tìm thấy Ngành tổ hợp với ID " + id;
            }

            dao.deleteWithSession(session, nganhToHop);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

}
