package com.admissionManagement.core.dao;


import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.NguyenVongXetTuyen;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NguyenVongXetTuyenDAO {
    private final SessionFactory factory;

    public NguyenVongXetTuyenDAO() {
        this.factory = HibernateUtil.getSessionFactory();
    }

    public void addWithSession(Session session, NguyenVongXetTuyen nguyenVongXetTuyen) {
        session.save(nguyenVongXetTuyen);
    }

    public NguyenVongXetTuyen getByKeyWithSession(Session session, String key) {
            return session.createNativeQuery(
                            "SELECT * FROM xt_nguyenvongxettuyen WHERE nv_keys = :key",
                            NguyenVongXetTuyen.class)
                    .setParameter("key", key)
                    .uniqueResult();
    }

    public NguyenVongXetTuyen getWithSession(Session session, int id){
        NguyenVongXetTuyen nguyenVongXetTuyen = session.get(NguyenVongXetTuyen.class, id);
        return nguyenVongXetTuyen;
    }

    public List<NguyenVongXetTuyen> getAllWithSession(Session session){
        String query = "FROM NguyenVongXetTuyen ";
        List listNguyenVongXetTuyen = session.createQuery(query).list();
        return listNguyenVongXetTuyen;
    }

    public void updateWithSession(Session session, NguyenVongXetTuyen newNguyenVongXetTuyen) {
        session.merge(newNguyenVongXetTuyen);
    }

    public void deleteWithSession(Session session, NguyenVongXetTuyen nguyenVongXetTuyen) {
        session.remove(nguyenVongXetTuyen);
    }

    public List<NguyenVongXetTuyen> getByThiSinhCccdWithSession(Session session, String cccd) {
        return session.createQuery(
                        "FROM NguyenVongXetTuyen nv WHERE nv.thiSinh.cccd = :cccd ORDER BY nv.thuTu ASC",
                        NguyenVongXetTuyen.class)
                .setParameter("cccd", cccd)
                .getResultList();
    }
}
