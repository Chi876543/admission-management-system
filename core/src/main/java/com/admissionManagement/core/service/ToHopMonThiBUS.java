package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.BangQuyDoiDAO;
import com.admissionManagement.core.dao.ToHopMonThiDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.ToHopMonThiDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.DiemThiXetTuyen;
import com.admissionManagement.core.entity.ThiSinh;
import com.admissionManagement.core.entity.ToHopMonThi;
import com.admissionManagement.core.helper.DatabaseHelper;
import com.admissionManagement.core.util.HibernateUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

@Service
public class ToHopMonThiBUS {

    private final ToHopMonThiDAO dao;
    private final SessionFactory factory;

    public ToHopMonThiBUS() {
        this.dao = new ToHopMonThiDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private ToHopMonThiDTO toDTO(ToHopMonThi entity){
        return new ToHopMonThiDTO(
                entity.getIdToHop(),
                entity.getMaToHop(),
                entity.getMon1(),
                entity.getMon2(),
                entity.getMon3(),
                entity.getTenToHop()
        );
    }

    private List<ToHopMonThiDTO> mapListEntityToListDTO(List<ToHopMonThi> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addToHopMonThi(ToHopMonThiDTO toHopMonThiDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ToHopMonThi toHopMonThi = new ToHopMonThi();
            toHopMonThi.setMaToHop(toHopMonThiDTO.getMaToHop());
            toHopMonThi.setMon1(toHopMonThiDTO.getMon1());
            toHopMonThi.setMon2(toHopMonThiDTO.getMon2());
            toHopMonThi.setMon3(toHopMonThiDTO.getMon3());
            toHopMonThi.setTenToHop(toHopMonThiDTO.getTenToHop());

            dao.addWithSession(session, toHopMonThi);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String importCsvData(File file) {
        int successCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            Transaction tx = session.beginTransaction();

            List<String> listMaDaCo = dao.getAllMaToHop(session);
            Set<String> cacheMaToHop = new HashSet<>(listMaDaCo);

            String[] line;

            while ((line = csvReader.readNext()) != null) {
                try {
                    if (line.length <= 3) continue;
                    ToHopMonThi entity = DatabaseHelper.parseToHopEntity(line[3].trim());

                    if (entity != null) {
                        if (!cacheMaToHop.contains(entity.getMaToHop())) {
                            session.persist(entity);
                            cacheMaToHop.add(entity.getMaToHop());
                            successCount++;
                        }
                    }
                } catch (Exception e) {
                    // Ghi log lỗi dòng
                    System.out.println("Lỗi parse dòng tổ hợp: " + e.getMessage());
                }
            }

            if (tx.isActive()) {
                tx.commit();
            }

            return "Import thành công " + successCount + " tổ hợp môn thi mới!";

        } catch (Exception e) {
            return "Lỗi đọc file: " + e.getMessage();
        }
    }

    public ToHopMonThiDTO getToHopMonThi(int id){
        try(Session session = factory.openSession()){
            ToHopMonThi result = dao.getWithSession(session, id);
            if(result != null)
                return toDTO(result);
            return null;
        }
    }

    public List<ToHopMonThiDTO> getAllToHopMonThi(int pageIndex, int pageSize){
        try(Session session = factory.openSession()){
            List<ToHopMonThi> result = dao.getAllWithSession(session, pageIndex, pageSize);
            if(!result.isEmpty())
                return mapListEntityToListDTO(result);
            return Collections.emptyList();
        }
    }

    public String updateToHopMonThi(int id, ToHopMonThiDTO newToHopMonThiDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            ToHopMonThi toHopMonThi = dao.getWithSession(session, id);

            if(toHopMonThi == null){
                return "Lỗi: Không tìm thấy tổ hợp môn với ID " + id;
            }

            toHopMonThi.setMaToHop(newToHopMonThiDTO.getMaToHop());
            toHopMonThi.setMon1(newToHopMonThiDTO.getMon1());
            toHopMonThi.setMon2(newToHopMonThiDTO.getMon2());
            toHopMonThi.setMon3(newToHopMonThiDTO.getMon3());
            toHopMonThi.setTenToHop(newToHopMonThiDTO.getTenToHop());

            dao.updateWithSession(session, toHopMonThi);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteToHopMonThi(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            ToHopMonThi toHopMonThi = dao.getWithSession(session, id);

            if(toHopMonThi == null){
                return "Lỗi: Không tìm thấy tổ hợp môn với ID " + id;
            }

            dao.deleteWithSession(session, toHopMonThi);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }




    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Import file tùm lum
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private static final Map<String, String> MON_HOC_MAP = new HashMap<>();
    static {
        MON_HOC_MAP.put("TO", "Toán");
        MON_HOC_MAP.put("VA", "Ngữ Văn");
        MON_HOC_MAP.put("LI", "Vật lý");
        MON_HOC_MAP.put("HO", "Hóa");
        MON_HOC_MAP.put("SI", "Sinh học");
        MON_HOC_MAP.put("DI", "Địa lí");
        MON_HOC_MAP.put("SU", "Lịch sử");
        MON_HOC_MAP.put("N1", "Tiếng Anh");
        MON_HOC_MAP.put("NK1", "Kể chuyện - Đọc diễn cảm");
        MON_HOC_MAP.put("NK2", "Hát - Nhạc");
        MON_HOC_MAP.put("NK3", "Hình họa");
        MON_HOC_MAP.put("NK4", "Trang trí");
        MON_HOC_MAP.put("NK5", "Hát - Nhạc cụ");
        MON_HOC_MAP.put("NK6", "Xướng âm - Thẩm âm - Tiết tấu");
        MON_HOC_MAP.put("KTPL", "Giáo dục pháp luật và kinh tế");
        MON_HOC_MAP.put("TI", "Tin học");
        MON_HOC_MAP.put("CNCN", "Công nghệ công nghiệp");
        MON_HOC_MAP.put("CNNN", "Công nghệ nông nghiệp");
    }

    public String importToHopMonThi(File file) {
        if (file == null || !file.exists()) {
            return "Lỗi: File không tồn tại!";
        }

        Transaction tx = null;
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis);
             Session session = factory.openSession()) {

            Sheet sheet = workbook.getSheetAt(0);
            tx = session.beginTransaction();
            int count = 0;

            // Dùng Set để tránh xử lý trùng lặp mã tổ hợp ngay trong file Excel
            java.util.Set<String> processedInFile = new java.util.HashSet<>();

            // Duyệt từ dòng 1 (bỏ qua tiêu đề)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Lấy cột MA_TO_HOP (Cột D - Index 3). VD: "B03(TO-3,VA-3,SI-1)"
                String rawValue = getCellValueAsString(row.getCell(3));
                if (rawValue.isEmpty() || !rawValue.contains("(") || !rawValue.contains(")")) continue;

                // a. Tách lấy mã tổ hợp chính (phần trước dấu ngoặc) -> B03
                String maToHop = rawValue.substring(0, rawValue.indexOf("(")).trim();

                // b. Kiểm tra duy nhất: Nếu đã xử lý trong vòng lặp này hoặc đã có trong DB thì bỏ qua
                if (processedInFile.contains(maToHop) || isToHopExisted(session, maToHop)) {
                    continue;
                }

                // c. Tách nội dung môn học bên trong dấu ngoặc -> TO-3,VA-3,SI-1
                String inner = rawValue.substring(rawValue.indexOf("(") + 1, rawValue.indexOf(")"));
                String[] parts = inner.split(",");

                if (parts.length >= 3) {
                    ToHopMonThi entity = new ToHopMonThi();
                    entity.setMaToHop(maToHop);

                    // d. Tách mã môn (Lấy phần trước dấu gạch ngang)
                    String m1 = parts[0].split("-")[0].trim();
                    String m2 = parts[1].split("-")[0].trim();
                    String m3 = parts[2].split("-")[0].trim();

                    entity.setMon1(m1);
                    entity.setMon2(m2);
                    entity.setMon3(m3);

                    // e. Ánh xạ sang tên đầy đủ
                    String t1 = MON_HOC_MAP.getOrDefault(m1, m1);
                    String t2 = MON_HOC_MAP.getOrDefault(m2, m2);
                    String t3 = MON_HOC_MAP.getOrDefault(m3, m3);
                    entity.setTenToHop(t1 + ", " + t2 + ", " + t3);

                    dao.addWithSession(session, entity);
                    processedInFile.add(maToHop);
                    count++;
                }
            }

            tx.commit();
            return "Import thành công " + count + " tổ hợp môn mới.";

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi import: " + e.getMessage();
        }
    }

    // Hàm kiểm tra mã tổ hợp đã tồn tại trong DB chưa
    private boolean isToHopExisted(Session session, String maToHop) {
        String hql = "SELECT count(t) FROM ToHopMonThi t WHERE t.maToHop = :ma";
        Long count = (Long) session.createQuery(hql)
                .setParameter("ma", maToHop)
                .uniqueResult();
        return count > 0;
    }

    // Hàm đọc giá trị ô Excel (Hỗ trợ định dạng chuỗi và số)
    private String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.format("%.0f", cell.getNumericCellValue());
            default: return "";
        }
    }
}
