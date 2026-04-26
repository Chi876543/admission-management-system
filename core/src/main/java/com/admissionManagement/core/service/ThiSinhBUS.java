package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.ThiSinhDAO;
import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.helper.DatabaseHelper;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.time.LocalDate;
import java.util.List;

public class ThiSinhBUS {
    private final ThiSinhDAO dao;
    private final SessionFactory factory;

    public ThiSinhBUS() {
        this.dao = new ThiSinhDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private List<ThiSinhDTO> mapListEntityToListDTO(List<ThiSinh> entities) {
        return entities.stream().map(entity -> {
            String strGioiTinh = (entity.getGioiTinh() != null) ? entity.getGioiTinh().getLabel() : ThiSinh.GioiTinh.KHAC.getLabel();
            return new ThiSinhDTO(
                    entity.getIdThiSinh(),
                    entity.getSoBaoDanh(),
                    entity.getCccd(),
                    entity.getHo(),
                    entity.getTen(),
                    entity.getNgaySinh(),
                    strGioiTinh,
                    entity.getDienThoai(),
                    entity.getEmail(),
                    entity.getPassword(),
                    entity.getNoiSinh(),
                    entity.getDoiTuong(),
                    entity.getKhuVuc(),
                    entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : ""
            );
        }).toList();
    }

    public String addThiSinh(ThiSinhDTO thiSinhDTO){
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh entity = new ThiSinh();
            entity.setSoBaoDanh(thiSinhDTO.getSoBaoDanh());
            entity.setCccd(thiSinhDTO.getCccd());
            entity.setHo(thiSinhDTO.getHo());
            entity.setTen(thiSinhDTO.getTen());
            entity.setNgaySinh(thiSinhDTO.getNgaySinh());
            entity.setDienThoai(thiSinhDTO.getDienThoai());
            entity.setEmail(thiSinhDTO.getEmail());
            entity.setPassword(thiSinhDTO.getPassword() != null ? thiSinhDTO.getPassword() : "123456");
            entity.setNoiSinh(thiSinhDTO.getNoiSinh());
            entity.setDoiTuong(thiSinhDTO.getDoiTuong());
            entity.setKhuVuc(thiSinhDTO.getKhuVuc());
            entity.setUpdatedAt(LocalDate.now());
            entity.setGioiTinh(ThiSinh.GioiTinh.fromLabel(thiSinhDTO.getGioiTinh()));

            dao.addWithSession(session, entity);
            tx.commit();
            return "Thêm mới thành công!";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String addListThiSinh(List<ThiSinhDTO> listDTO) {
        return DatabaseHelper.importBatch(listDTO, dto -> {
            ThiSinh entity = new ThiSinh();

            entity.setSoBaoDanh(dto.getSoBaoDanh());
            entity.setCccd(dto.getCccd());
            entity.setHo(dto.getHo());
            entity.setTen(dto.getTen());
            entity.setNgaySinh(dto.getNgaySinh());
            entity.setGioiTinh(ThiSinh.GioiTinh.fromLabel(dto.getGioiTinh()));
            entity.setDienThoai(dto.getDienThoai());
            entity.setEmail(dto.getEmail());
            entity.setNoiSinh(dto.getNoiSinh());
            entity.setDoiTuong(dto.getDoiTuong());
            entity.setKhuVuc(dto.getKhuVuc());
            entity.setPassword("123456");
            entity.setUpdatedAt(LocalDate.now());

            return entity;
        });
    }

    public ThiSinh getThiSinh(int id){
        try (Session session = factory.openSession()) {
            return dao.getWithSession(session, id);
        }
    }

    public List<ThiSinhDTO> getAllThiSinh(String keyWord, int pageIndex, int pageSize){
        String ho = null;
        String ten = null;
        String cccd = null;

        if(keyWord != null && !keyWord.trim().isEmpty()){
            if(keyWord.matches("^\\d{9,12}$")){
                cccd = keyWord;
            } else {
                int lastSpaceIndex = keyWord.lastIndexOf(" ");
                if(lastSpaceIndex > 0){
                    ho = keyWord.substring(0, lastSpaceIndex);
                    ten = keyWord.substring(lastSpaceIndex + 1);
                } else {
                    ten = keyWord;
                }
            }
        }

        try (Session session = factory.openSession()) {
            List<ThiSinh> entities = dao.getAllWithSession(session, ho, ten ,cccd, pageIndex, pageSize);
            return mapListEntityToListDTO(entities);
        }
    }

    public String updateThiSinh(int id, ThiSinhDTO newThiSinhDTO){
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            ThiSinh entity = dao.getWithSession(session, id);

            if(entity == null){
                return "Lỗi: Không tìm thấy thí sinh với ID " + id;
            }

            entity.setGioiTinh(ThiSinh.GioiTinh.fromLabel(newThiSinhDTO.getGioiTinh()));
            entity.setCccd(newThiSinhDTO.getCccd());
            entity.setSoBaoDanh(newThiSinhDTO.getSoBaoDanh());
            entity.setHo(newThiSinhDTO.getHo());
            entity.setTen(newThiSinhDTO.getTen());
            entity.setDienThoai(newThiSinhDTO.getDienThoai());
            entity.setEmail(newThiSinhDTO.getEmail());
            if (newThiSinhDTO.getPassword() != null && !newThiSinhDTO.getPassword().trim().isEmpty()) {
                entity.setPassword(newThiSinhDTO.getPassword());
            }
            entity.setNgaySinh(newThiSinhDTO.getNgaySinh());
            entity.setNoiSinh(newThiSinhDTO.getNoiSinh());
            entity.setDoiTuong(newThiSinhDTO.getDoiTuong());
            entity.setKhuVuc(newThiSinhDTO.getKhuVuc());
            entity.setUpdatedAt(LocalDate.now());

            tx.commit();
            return "Sửa thành công!";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteThiSinh(int id){
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            ThiSinh thiSinh = dao.getWithSession(session, id);

            if (thiSinh == null) {
                return null;
            }

            dao.deleteWithSession(session, thiSinh);
            tx.commit();
            return "Đã xóa thí sinh!";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi: " + e.getMessage();
        }
    }

    public Long getTotal(String keyWord) {
        String ho = null;
        String ten = null;
        String cccd = null;

        if(keyWord != null && !keyWord.trim().isEmpty()){
            if(keyWord.matches("^\\d{9,12}$")){
                cccd = keyWord;
            } else {
                int lastSpaceIndex = keyWord.lastIndexOf(" ");
                if(lastSpaceIndex > 0){
                    ho = keyWord.substring(0, lastSpaceIndex);
                    ten = keyWord.substring(lastSpaceIndex + 1);
                } else {
                    ten = keyWord;
                }
            }
        }

        try (Session session = factory.openSession()){
            return dao.getTotalWithSession(session, ho, ten ,cccd);
        }
    }
}
