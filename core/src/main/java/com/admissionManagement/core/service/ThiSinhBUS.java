package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.ThiSinhDAO;
import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.helper.DatabaseHelper;
import com.admissionManagement.core.util.HibernateUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThiSinhBUS {
    private final ThiSinhDAO dao;
    private final SessionFactory factory;

    public ThiSinhBUS() {
        this.dao = new ThiSinhDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private ThiSinhDTO toDTO(ThiSinh entity){
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
    }

    private List<ThiSinhDTO> mapListEntityToListDTO(List<ThiSinh> entities) {
        return entities.stream().map(this::toDTO).toList();
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

    public String importCsvData(File file) {
        int batchSize = 50; // Với Hibernate, batch nên để từ 20-50 để tối ưu memory
        int successCount = 0;
        int errorCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            String[] line;
            Transaction tx = session.beginTransaction();

            while ((line = csvReader.readNext()) != null) {
                try {
                    // Kiểm tra số lượng cột để tránh IndexOutOfBoundsException
                    if (line.length < 12) continue;

                    ThiSinh entity = new ThiSinh();

                    // MAPPING CHÍNH XÁC THEO FILE thi_sinh_100.csv:
                    // 0:cccd, 1:dien_thoai, 2:doi_tuong, 3:email, 4:gioi_tinh, 5:ho,
                    // 6:khu_vuc, 7:ngay_sinh, 8:noi_sinh, 9:password, 10:sobaodanh, 11:ten

                    entity.setCccd(line[0].trim());
                    entity.setDienThoai(line[1].trim());
                    entity.setDoiTuong(line[2].trim());
                    entity.setEmail(line[3].trim());

                    // Xử lý Enum GioiTinh (Đảm bảo file CSV là "NAM"/"NỮ" khớp với logic của bạn)
                    String genderRaw = line[4].trim().toUpperCase();
                    entity.setGioiTinh(ThiSinh.GioiTinh.fromLabel(genderRaw));

                    entity.setHo(line[5].trim());
                    entity.setKhuVuc(line[6].trim());

                    // Xử lý ngày sinh: File là 2005-01-15 (ISO_LOCAL_DATE)
                    entity.setNgaySinh(String.valueOf(LocalDate.parse(line[7].trim())));

                    entity.setNoiSinh(line[8].trim());
                    entity.setPassword(line[9].trim());
                    entity.setSoBaoDanh(line[10].trim());
                    entity.setTen(line[11].trim());

                    // Fix lỗi updatedAt không được null
                    entity.setUpdatedAt(LocalDate.from(LocalDateTime.now()));

                    session.persist(entity);
                    successCount++;

                    // Batch Flush
                    if (successCount % batchSize == 0) {
                        session.flush();
                        session.clear();
                    }
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("Lỗi dòng " + (successCount + errorCount) + ": " + e.getMessage());
                }
            }

            if (tx != null && tx.isActive()) {
                session.flush();
                tx.commit();
            }

            session.close();

            return "Import thành công " + successCount + " bản ghi! (Thất bại: " + errorCount + ")";

        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    public ThiSinhDTO getThiSinh(int id){
        try (Session session = factory.openSession()) {
            return toDTO(dao.getWithSession(session, id));
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

//            dao.updateWithSession(session, entity);
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

    // Dùng cho Login: tìm theo email hoặc CCCD, kiểm tra password
    public ThiSinhDTO findByEmailOrCccd(String emailOrCccd, String password) {
        try (Session session = factory.openSession()) {
            // Thử tìm theo email trước
            ThiSinh thiSinh = dao.getByEmailWithSession(session, emailOrCccd);

            // Nếu không có thì tìm theo CCCD
            if (thiSinh == null) {
                thiSinh = dao.getByCccdWithSesstion(session, emailOrCccd);
            }

            // Không tìm thấy hoặc sai password
            if (thiSinh == null || !thiSinh.getPassword().equals(password)) {
                return null;
            }

            return toDTO(thiSinh);
        }
    }

    // Dùng cho Dashboard: tra cứu theo SBD + ngày sinh
    public ThiSinhDTO findBySbd(String sbd, String ngaySinh) {
        try (Session session = factory.openSession()) {
            ThiSinh thiSinh = dao.getBySbdWithSession(session, sbd);

            if (thiSinh == null || !thiSinh.getNgaySinh().equals(ngaySinh)) {
                return null;
            }

            return toDTO(thiSinh);
        }
    }
}
