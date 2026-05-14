package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.ThiSinhDAO;
import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.entity.DiemThiXetTuyen;
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
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Collections;
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
        int batchSize = 1000;
        int successCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            String[] line;
            Transaction tx = session.beginTransaction();

            while ((line = csvReader.readNext()) != null) {
                try {
                    ThiSinh entity = new ThiSinh();
                    entity.setSoBaoDanh(line[0].trim());
                    entity.setCccd(line[1].trim());
                    entity.setHo(line[2].trim());
                    entity.setTen(line[2].trim());
                    entity.setNgaySinh(line[3].trim());
                    entity.setDienThoai(null);
                    entity.setEmail(null);
                    entity.setPassword("123456");
                    entity.setNoiSinh(line[35].trim());
                    String doiTuong = switch (line[5].trim()) {
                        case "01" -> "UT1";
                        case "06a" -> "UT2";
                        default -> null;
                    };
                    entity.setDoiTuong(doiTuong);
                    entity.setKhuVuc("KV" + line[6].trim());
                    entity.setUpdatedAt(LocalDate.now());
                    entity.setGioiTinh(ThiSinh.GioiTinh.fromLabel(line[4].trim()));

                    DiemThiXetTuyen diem = new DiemThiXetTuyen();
                    diem.setDiemToan(DatabaseHelper.parseDiem(line[7].trim()));
                    diem.setDiemVan(DatabaseHelper.parseDiem(line[8].trim()));
                    diem.setDiemLy(DatabaseHelper.parseDiem(line[9].trim()));
                    diem.setDiemHoa(DatabaseHelper.parseDiem(line[10].trim()));
                    diem.setDiemSinh(DatabaseHelper.parseDiem(line[11].trim()));
                    diem.setDiemSu(DatabaseHelper.parseDiem(line[12].trim()));
                    diem.setDiemDia(DatabaseHelper.parseDiem(line[13].trim()));
                    diem.setN1Thi(DatabaseHelper.parseDiem(line[15].trim()));
                    diem.setDiemKtpl(DatabaseHelper.parseDiem(line[17].trim()));
                    diem.setDiemTin(DatabaseHelper.parseDiem(line[18].trim()));
                    diem.setCncn(DatabaseHelper.parseDiem(line[19].trim()));
                    diem.setCnnn(DatabaseHelper.parseDiem(line[20].trim()));
                    diem.setNk1(DatabaseHelper.parseDiem(line[22].trim()));
                    diem.setNk2(DatabaseHelper.parseDiem(line[23].trim()));
                    diem.setNk3(DatabaseHelper.parseDiem(line[24].trim()));
                    diem.setNk4(DatabaseHelper.parseDiem(line[25].trim()));
                    diem.setNk5(DatabaseHelper.parseDiem(line[26].trim()));
                    diem.setNk6(DatabaseHelper.parseDiem(line[27].trim()));
                    entity.asyncDiemThi(diem);

                    session.persist(entity);
                    successCount++;

                    if (successCount % batchSize == 0) {
                        tx.commit();
                        session.clear();
                        tx = session.beginTransaction();
                    }
                } catch (Exception e) {
                    // Bỏ qua dòng lỗi (hoặc ghi log ra file TXT để sau này báo cáo)
                }
            }

            if (tx.isActive()) {
                tx.commit();
            }

            return "Import thành công " + successCount + " bản ghi!";

        } catch (Exception e) {
            return "Lỗi đọc file: " + e.getMessage();
        }
    }

    public ThiSinhDTO getThiSinh(int id){
        try (Session session = factory.openSession()) {
            ThiSinh result = dao.getWithSession(session, id);
            if(result != null)
                return toDTO(result);
            return null;
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
            List<ThiSinh> result = dao.getAllWithSession(session, ho, ten ,cccd, pageIndex, pageSize);
            if(!result.isEmpty())
                return mapListEntityToListDTO(result);
            return Collections.emptyList();
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

            dao.updateWithSession(session, entity);
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

    public ThiSinhDTO getByCccd(String cccd) {

        try (Session session = factory.openSession()) {

            ThiSinh entity = dao.getByCccdWithSession(session, cccd);

            if (entity == null) {
                return null;
            }

            return toDTO(entity);
        }
    }
}
