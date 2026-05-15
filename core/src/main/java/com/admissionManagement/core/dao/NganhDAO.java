package com.admissionManagement.core.dao;

import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NganhWithRegistryCountDTO;
import com.admissionManagement.core.entity.Nganh;
import com.admissionManagement.core.entity.ToHopMonThi;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NganhDAO {

    public void addWithSession(Session session, Nganh nganh) {
        session.save(nganh);
    }

    public Nganh getWithSession(Session session, int id){
        return session.get(Nganh.class, id);
    }

    public Nganh getByMaNganhWithSession(Session session, String maNganh){
        return session.createQuery("FROM Nganh WHERE maNganh = :ma", Nganh.class)
                .setParameter("ma", maNganh)
                .uniqueResult();
    }

    public List<Nganh> getAllWithSession(Session session, int pageIndex, int pageSize){
        String query = "FROM Nganh";

        if(pageIndex == 0 || pageSize == 0)
            return session.createQuery(query, Nganh.class).getResultList();
        int offset = pageIndex * pageSize;
        return session.createQuery(query, Nganh.class).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    public List<NganhWithRegistryCountDTO> getAllWithCountWithSession(Session session, int pageIndex, int pageSize){
        String hql =
                "SELECT new com.admissionManagement.core.dto.NganhWithRegistryCountDTO(" +
                "n.idNganh, n.maNganh, n.tenNganh, n.toHopGoc, n.chiTieu, " +
                "n.diemSan, n.diemTrungTuyen, n.tuyenThang, n.dgnl, n.thpt, n.vsat, " +
                "n.slXtt, n.slDgnl, n.slVsat, n.slThpt, " +
                "COUNT(nv.idNv)) " +
                "FROM Nganh n " +
                "LEFT JOIN n.danhSachNguyenVongCuaNganh nv " +
                "GROUP BY n.idNganh, n.maNganh, n.tenNganh, n.toHopGoc, n.chiTieu, " +
                "n.diemSan, n.diemTrungTuyen, n.tuyenThang, n.dgnl, n.thpt, n.vsat, " +
                "n.slXtt, n.slDgnl, n.slVsat, n.slThpt";

        if(pageIndex == 0 || pageSize == 0)
            return session.createQuery(hql, NganhWithRegistryCountDTO.class).getResultList();

        int offset = pageIndex * pageSize;
        return session.createQuery(hql, NganhWithRegistryCountDTO.class).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    public List<String> getAllMaNganh(Session session) {
        return session.createQuery("SELECT n.maNganh FROM Nganh n", String.class).list();
    }

    public void updateWithSession(Session session, Nganh newNganh) {
        session.merge(newNganh);
    }

    public void deleteWithSession(Session session, Nganh nganh) {
        session.remove(nganh);
    }
}
