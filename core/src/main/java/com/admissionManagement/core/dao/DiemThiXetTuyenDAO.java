package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.DiemThiXetTuyen;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class DiemThiXetTuyenDAO {
    private final SessionFactory factory;

    public DiemThiXetTuyenDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addDiemThiXetTuyen(DiemThiXetTuyen diemThiXetTuyen) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(diemThiXetTuyen);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<DiemThiXetTuyen> getAllDiemThiXetTuyen(){
        Session session = factory.openSession();
        String query = "FROM DiemThiXetTuyen";
        List listDiemThiXetTuyen = session.createQuery(query).list();
        session.close();
        return listDiemThiXetTuyen;
    }

    public void updateDiemThiXetTuyen(int id, DiemThiXetTuyen newDiemThiXetTuyen) {
        Session session = factory.openSession();
        Transaction tx = null;
        DiemThiXetTuyen diemThiXetTuyen = session.get(DiemThiXetTuyen.class, id);
        try {
            tx = session.beginTransaction();
            if(diemThiXetTuyen != null){
                diemThiXetTuyen.setCccd(newDiemThiXetTuyen.getCccd());
                diemThiXetTuyen.setSoBaoDanh(newDiemThiXetTuyen.getSoBaoDanh());
                diemThiXetTuyen.setPhuongThuc(newDiemThiXetTuyen.getPhuongThuc());
                diemThiXetTuyen.setDiemToan(newDiemThiXetTuyen.getDiemToan());
                diemThiXetTuyen.setDiemLy(newDiemThiXetTuyen.getDiemLy());
                diemThiXetTuyen.setDiemHoa(newDiemThiXetTuyen.getDiemHoa());
                diemThiXetTuyen.setDiemSinh(newDiemThiXetTuyen.getDiemSinh());
                diemThiXetTuyen.setDiemSu(newDiemThiXetTuyen.getDiemSu());
                diemThiXetTuyen.setDiemDia(newDiemThiXetTuyen.getDiemDia());
                diemThiXetTuyen.setDiemVan(newDiemThiXetTuyen.getDiemVan());
                diemThiXetTuyen.setDiemAnh(newDiemThiXetTuyen.getDiemAnh());
                diemThiXetTuyen.setDiemKtpl(newDiemThiXetTuyen.getDiemKtpl());
                diemThiXetTuyen.setN1Thi(newDiemThiXetTuyen.getN1Thi());
                diemThiXetTuyen.setN1Cc(newDiemThiXetTuyen.getN1Cc());
                diemThiXetTuyen.setCncn(newDiemThiXetTuyen.getCncn());
                diemThiXetTuyen.setCnnn(newDiemThiXetTuyen.getCnnn());
                diemThiXetTuyen.setNl1(newDiemThiXetTuyen.getNl1());
                diemThiXetTuyen.setNk1(newDiemThiXetTuyen.getNk1());
                diemThiXetTuyen.setNk2(newDiemThiXetTuyen.getNk2());
            }
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteDiemThiXetTuyen(int id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            DiemThiXetTuyen diemThiXetTuyen = session.get(DiemThiXetTuyen.class,id);
            if (diemThiXetTuyen != null) session.delete(diemThiXetTuyen);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
