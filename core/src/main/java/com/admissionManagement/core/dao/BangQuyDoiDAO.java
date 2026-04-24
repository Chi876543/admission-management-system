package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.BangQuyDoi;
import org.hibernate.Session;

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
}
