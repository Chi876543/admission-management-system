package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.BangQuyDoiDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.util.HibernateUtil;
import org.apache.commons.lang3.builder.DiffResult;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BangQuyDoiBUS {
    private final BangQuyDoiDAO dao;
    private final SessionFactory factory;

    public BangQuyDoiBUS() {
        this.dao = new BangQuyDoiDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private BangQuyDoiDTO toDTO(BangQuyDoi entity){
        return new BangQuyDoiDTO(
                entity.getIdqd(),
                entity.getPhuongThuc(),
                entity.getToHop(),
                entity.getMon(),
                entity.getDiemA(),
                entity.getDiemB(),
                entity.getDiemC(),
                entity.getDiemD(),
                entity.getMaQuyDoi(),
                entity.getPhanVi()
        );
    }

    private List<BangQuyDoiDTO> mapListEntityToListDTO(List<BangQuyDoi> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addBangQuyDoi(BangQuyDoiDTO bangQuyDoiDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            BangQuyDoi bangQuyDoi = new BangQuyDoi();
            bangQuyDoi.setPhuongThuc(bangQuyDoiDTO.getPhuongThuc());
            bangQuyDoi.setToHop(bangQuyDoiDTO.getToHop());
            bangQuyDoi.setMon(bangQuyDoiDTO.getMon());
            bangQuyDoi.setDiemA(bangQuyDoiDTO.getDiemA());
            bangQuyDoi.setDiemB(bangQuyDoiDTO.getDiemB());
            bangQuyDoi.setDiemC(bangQuyDoiDTO.getDiemC());
            bangQuyDoi.setDiemD(bangQuyDoiDTO.getDiemD());
            bangQuyDoi.setMaQuyDoi(bangQuyDoiDTO.getMaQuyDoi());
            bangQuyDoi.setPhanVi(bangQuyDoiDTO.getPhanVi());

            dao.addWithSession(session, bangQuyDoi);
            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public BangQuyDoiDTO getBangQuyDoi(int id){
        try(Session session = factory.openSession()) {
            BangQuyDoi result = dao.getWithSession(session, id);
            if(result != null)
                return toDTO(result);
            return null;
        }
    }

    public List<BangQuyDoiDTO> getAllBangQuyDoi(String keyWord) {
        String phuongThuc = null;
        String toHop = null;
        String mon = null;

        if (keyWord != null && !keyWord.trim().isEmpty()) {
            String key = keyWord.trim().toUpperCase();
            if (key.matches("[A-Z]\\d{2}")) {
                toHop = keyWord.trim();
            } else if (key.equals("DGNL") || key.equals("VSAT") || key.equals("THPT")) {
                phuongThuc = keyWord.trim();
            } else {
                mon = keyWord.trim();
            }
        }

        try (Session session = factory.openSession()) {
            List<BangQuyDoi> result = dao.getAllWithSession(session, phuongThuc, toHop, mon);
            if(!result.isEmpty())
                return mapListEntityToListDTO(result);
            return java.util.Collections.emptyList();
        }
    }

    public String updateBangQuyDoi(int id, BangQuyDoiDTO newBangQuyDoiDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            BangQuyDoi bangQuyDoi = dao.getWithSession(session, id);

            if(bangQuyDoi == null){
                return "Lỗi: Không tìm thấy bảng quy đổi với ID " + id;
            }

            bangQuyDoi.setPhuongThuc(newBangQuyDoiDTO.getPhuongThuc());
            bangQuyDoi.setToHop(newBangQuyDoiDTO.getToHop());
            bangQuyDoi.setMon(newBangQuyDoiDTO.getMon());
            bangQuyDoi.setDiemA(newBangQuyDoiDTO.getDiemA());
            bangQuyDoi.setDiemB(newBangQuyDoiDTO.getDiemB());
            bangQuyDoi.setDiemC(newBangQuyDoiDTO.getDiemC());
            bangQuyDoi.setDiemD(newBangQuyDoiDTO.getDiemD());
            bangQuyDoi.setMaQuyDoi(newBangQuyDoiDTO.getMaQuyDoi());
            bangQuyDoi.setPhanVi(newBangQuyDoiDTO.getPhanVi());

            dao.updateWithSession(session, bangQuyDoi);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteBangQuyDoi(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            BangQuyDoi bangQuyDoi = dao.getWithSession(session, id);

            if (bangQuyDoi == null) {
                return "Lỗi: Không tìm thấy bảng quy đổi với ID " + id;
            }

            dao.deleteWithSession(session, bangQuyDoi);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    /**
     * Import CSV bảng quy đổi.
     * Format dòng (bỏ header): phuongThuc,toHop,mon,diemA,diemB,diemC,diemD
     */
    public String importCsv(java.io.File file) {
        int success = 0, error = 0;
        StringBuilder report = new StringBuilder();
        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(file),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (cols.length < 7) { error++; continue; }
                try {
                    BangQuyDoiDTO dto = new BangQuyDoiDTO(
                            0,
                            cols[0].replace("\"", "").trim(),
                            cols[1].replace("\"", "").trim(),
                            cols[2].replace("\"", "").trim(),
                            new BigDecimal(cols[3].replace("\"", "").trim()),
                            new BigDecimal(cols[4].replace("\"", "").trim()),
                            new BigDecimal(cols[5].replace("\"", "").trim()),
                            new BigDecimal(cols[6].replace("\"", "").trim()),
                            "QD-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                            null
                    );
                    String res = addBangQuyDoi(dto);
                    if (res.startsWith("Lỗi")) { error++; report.append("- ").append(res).append("\n"); }
                    else success++;
                } catch (Exception e) {
                    error++;
                    report.append("- Lỗi dòng: ").append(line).append("\n");
                }
            }
        } catch (Exception e) {
            return "Lỗi đọc file: " + e.getMessage();
        }
        return report.append("Thành công: ").append(success)
                .append(" | Lỗi: ").append(error).toString();
    }

    public BangQuyDoiDTO getBangQuyDoiWithScore(String phuongThuc, BigDecimal diem, String mon, String toHop) {
        try (Session session = factory.openSession()) {

            BangQuyDoi entity = dao.getLuatQuyDoiWithSession(session, phuongThuc, diem, mon, toHop);

            if (entity == null) {
                return null;
            }

            return toDTO(entity);
        }
    }
}