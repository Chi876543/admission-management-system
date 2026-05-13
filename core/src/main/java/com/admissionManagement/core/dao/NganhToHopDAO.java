package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.NganhToHop;
import com.admissionManagement.core.entity.NganhToHop;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class NganhToHopDAO {

    public void addWithSession(Session session, NganhToHop nganhToHop) {
        session.save(nganhToHop);
    }


    public NganhToHop getWithSession(Session session, int id){
        NganhToHop nganhToHop  = session.get(NganhToHop.class, id);
        return nganhToHop;
    }

    public List<NganhToHop> getAllByMaNganhWithSession(Session session, String maNganh) {
        try {
            // Sử dụng SQL thuần (Native Query)
            // Thêm COLLATE để xử lý triệt để lỗi xung đột bảng mã ký tự
            String sql = "SELECT nth.* FROM xt_nganh_tohop nth " +
                    "JOIN xt_nganh n ON nth.manganh = n.manganh COLLATE utf8mb3_general_ci " +
                    "WHERE n.manganh = :maNganh";

            return session.createNativeQuery(sql, NganhToHop.class)
                    .setParameter("maNganh", maNganh)
                    .getResultList();

        } catch (Exception e) {
            System.err.println("Lỗi Native Query tại NganhToHopDAO: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<NganhToHop> getAllWithSession(Session session){
        String query = "FROM NganhToHop ";
        List listNganhToHop = session.createQuery(query).list();
        return listNganhToHop;
    }

    public void updateWithSession(Session session, NganhToHop newNganhToHop) {
        session.merge(newNganhToHop);
    }

    public void deleteWithSession(Session session, NganhToHop nganhToHop) {
        session.remove(nganhToHop);
    }
}
