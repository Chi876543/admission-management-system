package com.admissionManagement.core.service;


import com.admissionManagement.core.dao.NganhToHopDAO;
import com.admissionManagement.core.dao.NganhToHopDAO;

import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NganhToHopDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.NganhToHop;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class NganhToHopBUS {

    private final NganhToHopDAO dao;
    private final SessionFactory factory;

    public NganhToHopBUS() {
        this.dao = new NganhToHopDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private NganhToHopDTO toDTO(NganhToHop entity){
        return new NganhToHopDTO(
                entity.getId(),
                entity.getMaNganh(),
                entity.getMaToHop(),
                entity.getThMon1(),
                entity.getHsMon1(),
                entity.getThMon2(),
                entity.getHsMon2(),
                entity.getThMon3(),
                entity.getHsMon3(),
                entity.getTbKeys(),
                entity.getN1(),
                entity.getToan(),
                entity.getLy(),
                entity.getHoa(),
                entity.getSinh(),
                entity.getVan(),
                entity.getSu(),
                entity.getDia(),
                entity.getAnh(),
                entity.getKhac(),
                entity.getKtpl(),
                entity.getDoLech()
        );
    }

    private List<NganhToHopDTO> mapListEntityToListDTO(List<NganhToHop> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addNganhToHop(NganhToHop nganhToHop){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            dao.addWithSession(session, nganhToHop);

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

    public NganhToHop getNganhToHop(int id){
        Session session = factory.openSession();
        NganhToHop nganhTohop = dao.getWithSession(session, id);
        session.close();
        return nganhTohop;
    }

    public List<NganhToHop> getAllNganhToHop(){
        Session session = factory.openSession();
        List<NganhToHop> listNganhToHop = dao.getAllWithSession(session);
        session.close();
        return listNganhToHop;
    }

    public String updateNganhToHop(int id, NganhToHop newNganhToHop){
        Session session = factory.openSession();
        Transaction tx = null;
        NganhToHop nganhToHop = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            if(nganhToHop != null) {
                nganhToHop.setMaNganh(newNganhToHop.getMaNganh());
                nganhToHop.setMaToHop(newNganhToHop.getMaToHop());
                nganhToHop.setHsMon1(newNganhToHop.getHsMon1());
                nganhToHop.setHsMon2(newNganhToHop.getHsMon2());
                nganhToHop.setHsMon3(newNganhToHop.getHsMon3());
                nganhToHop.setThMon1(newNganhToHop.getThMon1());
                nganhToHop.setThMon2(newNganhToHop.getThMon2());
                nganhToHop.setThMon3(newNganhToHop.getThMon3());
                nganhToHop.setTbKeys(newNganhToHop.getTbKeys());
                nganhToHop.setN1(newNganhToHop.getN1());
                nganhToHop.setToan(newNganhToHop.getToan());
                nganhToHop.setLy(newNganhToHop.getLy());
                nganhToHop.setHoa(newNganhToHop.getHoa());
                nganhToHop.setSinh(newNganhToHop.getSinh());
                nganhToHop.setVan(newNganhToHop.getVan());
                nganhToHop.setSu(newNganhToHop.getSu());
                nganhToHop.setDia(newNganhToHop.getDia());
                nganhToHop.setAnh(newNganhToHop.getAnh());
                nganhToHop.setKhac(newNganhToHop.getKhac());
                nganhToHop.setKtpl(newNganhToHop.getKtpl());
                nganhToHop.setDoLech(newNganhToHop.getDoLech());
            }

            dao.updateWithSession(session, nganhToHop);

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

    public String deleteNganhToHop(int id){
        Session session = factory.openSession();
        Transaction tx = null;
        NganhToHop nganhToHop = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            dao.deleteWithSession(session, nganhToHop);

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
