package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.NganhDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NganhWithRegistryCountDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.Nganh;
import com.admissionManagement.core.entity.ToHopMonThi;
import com.admissionManagement.core.helper.DatabaseHelper;
import com.admissionManagement.core.util.HibernateUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;

@Service
public class NganhBUS {
    private final NganhDAO dao;
    private final SessionFactory factory;

    public NganhBUS() {
        this.dao = new NganhDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private NganhDTO toDTO(Nganh entity){
        return new NganhDTO(
                entity.getIdNganh(),
                entity.getMaNganh(),
                entity.getTenNganh(),
                entity.getToHopGoc(),
                entity.getChiTieu(),
                entity.getDiemSan(),
                entity.getDiemTrungTuyen(),
                entity.getTuyenThang(),
                entity.getDgnl(),
                entity.getThpt(),
                entity.getVsat(),
                entity.getSlXtt(),
                entity.getSlDgnl(),
                entity.getSlVsat(),
                entity.getSlThpt()
        );
    }

    private List<NganhDTO> mapListEntityToListDTO(List<Nganh> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addBangQuyDoi(NganhDTO nganhDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Nganh nganh = new Nganh();
            nganh.setMaNganh(nganhDTO.getMaNganh());
            nganh.setTenNganh(nganhDTO.getTenNganh());
            nganh.setToHopGoc(nganhDTO.getToHopGoc());
            nganh.setChiTieu(nganhDTO.getChiTieu());
            nganh.setDiemSan(nganhDTO.getDiemSan());
            nganh.setDiemTrungTuyen(nganhDTO.getDiemTrungTuyen());
            nganh.setTuyenThang(nganhDTO.getTuyenThang());
            nganh.setDgnl(nganhDTO.getDgnl());
            nganh.setThpt(nganhDTO.getThpt());
            nganh.setVsat(nganhDTO.getVsat());
            nganh.setSlXtt(nganhDTO.getSlXtt());
            nganh.setSlDgnl(nganhDTO.getSlDgnl());
            nganh.setSlThpt(nganhDTO.getSlThpt());
            nganh.setSlVsat(nganhDTO.getSlVsat());

            dao.addWithSession(session, nganh);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public NganhDTO getNganh(int id){
        try(Session session = factory.openSession()){
            Nganh result = dao.getWithSession(session, id);
            if(result != null)
                return toDTO(result);
            return null;
        }
    }

    public String importCsvData(File file) {
        int successCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            Transaction tx = session.beginTransaction();

            String[] line;

            while ((line = csvReader.readNext()) != null) {
                try {
                    Nganh entity = new Nganh();
                    entity.setMaNganh(line[1].trim());
                    entity.setTenNganh(line[2].trim());
                    entity.setToHopGoc(line[3].trim());
                    entity.setChiTieu(Integer.parseInt(line[4].trim()));
                    entity.setDiemSan(new BigDecimal(line[5].trim()));
                    entity.setTuyenThang("1");
                    entity.setDgnl("1");
                    entity.setThpt("1");
                    entity.setVsat("1");
                    session.persist(entity);
                    successCount++;
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

    public List<NganhDTO> getAllNganh(){
        try(Session session = factory.openSession()){
            List<Nganh> result = dao.getAllWithSession(session);
            if(!result.isEmpty())
                return mapListEntityToListDTO(result);
            return Collections.emptyList();
        }
    }

    public List<NganhWithRegistryCountDTO> getAllNganhWithRegistryCount(){
        try(Session session = factory.openSession()){
            List<NganhWithRegistryCountDTO> result = dao.getAllWithCountWithSession(session);
            if(!result.isEmpty())
                return result;
            return Collections.emptyList();
        }
    }

    public String updateNganh(int id, NganhDTO newNganhDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Nganh nganh = dao.getWithSession(session, id);

            if(nganh == null){
                return "Lỗi: Không tìm thấy Ngành với ID " + id;
            }

            nganh.setMaNganh(newNganhDTO.getMaNganh());
            nganh.setTenNganh(newNganhDTO.getTenNganh());
            nganh.setToHopGoc(newNganhDTO.getToHopGoc());
            nganh.setChiTieu(newNganhDTO.getChiTieu());
            nganh.setDiemSan(newNganhDTO.getDiemSan());
            nganh.setDiemTrungTuyen(newNganhDTO.getDiemTrungTuyen());
            nganh.setTuyenThang(newNganhDTO.getTuyenThang());
            nganh.setDgnl(newNganhDTO.getDgnl());
            nganh.setThpt(newNganhDTO.getThpt());
            nganh.setVsat(newNganhDTO.getVsat());
            nganh.setSlXtt(newNganhDTO.getSlXtt());
            nganh.setSlDgnl(newNganhDTO.getSlDgnl());
            nganh.setSlThpt(newNganhDTO.getSlThpt());
            nganh.setSlVsat(newNganhDTO.getSlVsat());

            dao.updateWithSession(session, nganh);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteNganh(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Nganh nganh = dao.getWithSession(session, id);

            if(nganh == null){
                return "Lỗi: Không tìm thấy Ngành với ID " + id;
            }

            dao.deleteWithSession(session, nganh);

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

    public String importNganhFromExcelDesktop(File file) {
        if (file == null || !file.exists()) {
            return "Lỗi: File không tồn tại!";
        }

        Transaction tx = null;
        // Sử dụng try-with-resources để đảm bảo đóng file sau khi đọc xong
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis);
             Session session = factory.openSession()) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator(); // Sử dụng Row của POI

            // 1. Bỏ qua dòng tiêu đề (Dòng đầu tiên)
            if (rows.hasNext()) {
                rows.next();
            }

            tx = session.beginTransaction();
            int count = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next(); // Không cần ép kiểu (Row) thủ công
                if (currentRow == null) continue;

                // 2. Cột G (Index 6) là cột xác định "Gốc"
                String labelGoc = getCellValueAsString(currentRow.getCell(6));

                // Logic: Chỉ xử lý những dòng có cột G là chữ "Gốc"
                if ("Gốc".equalsIgnoreCase(labelGoc)) {
                    Nganh nganh = new Nganh();

                    // 3. Đọc Mã Ngành (Cột B - Index 1)
                    nganh.setMaNganh(getCellValueAsString(currentRow.getCell(1)));

                    // 4. Đọc Tên Ngành (Cột C - Index 2)
                    nganh.setTenNganh(getCellValueAsString(currentRow.getCell(2)));

                    // 5. Đọc Tổ hợp gốc (Cột D - Index 3)
                    // Ví dụ: "D01(TO-3,VA-3,N1-1)" -> "D01"
                    String rawToHop = getCellValueAsString(currentRow.getCell(3));
                    if (rawToHop != null && rawToHop.contains("(")) {
                        rawToHop = rawToHop.substring(0, rawToHop.indexOf("(")).trim();
                    }
                    nganh.setToHopGoc(rawToHop);

                    // 6. Gán giá trị mặc định cho các cột không cho phép null (ChiTieu)
                    nganh.setChiTieu(0);

                    // Gọi DAO để lưu vào DB
                    dao.addWithSession(session, nganh);
                    count++;
                }
            }

            tx.commit();
            return "Import thành công! Đã thêm " + count + " ngành có tổ hợp gốc.";

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    public String importChiTieu(File file) {
        if (file == null || !file.exists()) return "Lỗi: File không tồn tại!";
        Transaction tx = null;
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis);
             Session session = factory.openSession()) {

            Sheet sheet = workbook.getSheetAt(0);
            tx = session.beginTransaction();
            int count = 0;

            // File này có dòng tiêu đề phụ ở dòng 0, tiêu đề chính ở dòng 1
            // Ta bắt đầu đọc từ dòng 2 (Index 2)
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String maNganh = getCellValueAsString(row.getCell(1));
                String chiTieuStr = getCellValueAsString(row.getCell(3));

                if (!maNganh.isEmpty() && !chiTieuStr.isEmpty()) {
                    // Tìm ngành đã có trong DB bằng mã ngành
                    Nganh nganh = dao.getByMaNganhWithSession(session, maNganh);
                    if (nganh != null) {
                        nganh.setChiTieu(Integer.parseInt(chiTieuStr));
                        dao.updateWithSession(session, nganh);
                        count++;
                    }
                }
            }
            tx.commit();
            return "Cập nhật chỉ tiêu thành công cho " + count + " ngành.";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi import chỉ tiêu: " + e.getMessage();
        }
    }

    public String importNguongDauVao(File file) {
        if (file == null || !file.exists()) return "Lỗi: File không tồn tại!";
        Transaction tx = null;
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis);
             Session session = factory.openSession()) {

            Sheet sheet = workbook.getSheetAt(0);
            tx = session.beginTransaction();
            int count = 0;

            // Bắt đầu từ dòng 1 (bỏ qua header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String maNganh = getCellValueAsString(row.getCell(1));
                String diemSanStr = getCellValueAsString(row.getCell(3));

                if (!maNganh.isEmpty() && !diemSanStr.isEmpty()) {
                    Nganh nganh = dao.getByMaNganhWithSession(session, maNganh);
                    if (nganh != null) {
                        // Chuyển đổi chuỗi điểm sang BigDecimal
                        nganh.setDiemSan(new java.math.BigDecimal(diemSanStr));
                        dao.updateWithSession(session, nganh);
                        count++;
                    }
                }
            }
            tx.commit();
            return "Cập nhật ngưỡng đầu vào thành công cho " + count + " ngành.";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi import ngưỡng đầu vào: " + e.getMessage();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        // Xử lý tùy theo kiểu dữ liệu của ô trong Excel
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Nếu là số (như mã ngành), trả về số nguyên dưới dạng chuỗi để tránh bị thêm .0
                return String.format("%.0f", cell.getNumericCellValue());
            case FORMULA:
                // Nếu ô là công thức, lấy kết quả cuối cùng của công thức đó
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
