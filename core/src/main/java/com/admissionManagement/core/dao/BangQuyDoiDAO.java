package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.BangQuyDoi;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

public class BangQuyDoiDAO {
    // Xóa Thuộc tính session và constructor

    public void addWithSession(Session session, BangQuyDoi bangQuyDoi) {
        session.save(bangQuyDoi);
    }

    public BangQuyDoi getWithSession(Session session, int id){
        BangQuyDoi bangQuyDoi = session.get(BangQuyDoi.class, id);
        return bangQuyDoi;
    }

    public List<BangQuyDoi> getAllWithSession(Session session){
        String query = "FROM BangQuyDoi";
        List listBangQuyDoi = session.createQuery(query).list();
        return listBangQuyDoi;
    }

    public void updateWithSession(Session session, BangQuyDoi newBangQuyDoi) {
        session.merge(newBangQuyDoi);
    }

    public void deleteWithSession(Session session, BangQuyDoi bangQuyDoi) {
        session.detach(bangQuyDoi);
    }

    public BangQuyDoi getLuatQuyDoiWithSession(Session session, String phuongThuc, BigDecimal diem, String mon, String toHop) {
        String hql = "FROM BangQuyDoi b " +
                "WHERE :diemThiSinh BETWEEN b.diemA AND b.diemB " +
                "AND b.phuongThuc = :phuongThuc " +
                "AND (b.mon = :mon OR (:mon IS NULL AND b.mon IS NULL)) " +
                "AND (b.toHop = :toHop OR (:toHop IS NULL AND b.toHop IS NULL))";

        Query<BangQuyDoi> query = session.createQuery(hql, BangQuyDoi.class);
        query.setParameter("diemThiSinh", diem);
        query.setParameter("phuongThuc", phuongThuc);
        query.setParameter("mon", mon);
        query.setParameter("toHop", toHop);

        return query.uniqueResult();
    }
}
