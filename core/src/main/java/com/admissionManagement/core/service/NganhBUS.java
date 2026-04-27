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

    public String addBangQuyDoi(Nganh nganh){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            dao.addWithSession(session, nganh);

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

    public Nganh getNganh(int id){
        Session session = factory.openSession();
        Nganh nganh = dao.getWithSession(session, id);
        session.close();
        return nganh;
    }

    public List<Nganh> getAllNganh(){
        Session session = factory.openSession();
        List<Nganh> listNganh = dao.getAllWithSession(session);
        session.close();
        return listNganh;
    }

    public String updateNganh(int id, Nganh newNganh){
        Session session = factory.openSession();
        Transaction tx = null;
        Nganh nganh = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            if(nganh != null){
                nganh.setMaNganh(newNganh.getMaNganh());
                nganh.setTenNganh(newNganh.getTenNganh());
                nganh.setToHopGoc(newNganh.getToHopGoc());
                nganh.setChiTieu(newNganh.getChiTieu());
                nganh.setDiemSan(newNganh.getDiemSan());
                nganh.setDiemTrungTuyen(newNganh.getDiemTrungTuyen());
                nganh.setTuyenThang(newNganh.getTuyenThang());
                nganh.setDgnl(newNganh.getDgnl());
                nganh.setThpt(newNganh.getThpt());
                nganh.setVsat(newNganh.getVsat());
                nganh.setSlXtt(newNganh.getSlXtt());
                nganh.setSlDgnl(newNganh.getSlDgnl());
                nganh.setSlThpt(newNganh.getSlThpt());
                nganh.setSlVsat(newNganh.getSlVsat());
            }

            dao.updateWithSession(session, nganh);

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

    public String deleteNganh(int id){
        Session session = factory.openSession();
        Transaction tx = null;
        Nganh nganh = dao.getWithSession(session, id);
        try {
            tx = session.beginTransaction();

            dao.deleteWithSession(session, nganh);

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
