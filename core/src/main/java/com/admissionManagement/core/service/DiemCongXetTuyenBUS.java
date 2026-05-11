package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.*;
import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.entity.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DiemCongXetTuyenBUS {
    private final DiemCongXetTuyenDAO dao;
    private final ThiSinhDAO thisinhdao;
    private final NganhDAO nganhdao;
    private final ToHopMonThiDAO tohopdao;
    private final SessionFactory factory;
    private final NganhToHopDAO nganhtohopdao;

    public DiemCongXetTuyenBUS() {
        this.dao = new DiemCongXetTuyenDAO();
        this.thisinhdao = new ThiSinhDAO();
        this.nganhdao = new NganhDAO();
        this.tohopdao = new ToHopMonThiDAO();
        this.nganhtohopdao = new NganhToHopDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private DiemCongXetTuyenDTO toDTO(DiemCongXetTuyen entity){
        return new DiemCongXetTuyenDTO(
                entity.getIdDiemCong(),
                entity.getThiSinh().getCccd(),
                entity.getMon(),
                entity.getPhuongThuc(),
                entity.getDiemCongToHopXetTuyen(),
                entity.getDiemCongKhongXetToHopXetTuyen(),
                entity.getGhiChu()
        );
    }

    private List<DiemCongXetTuyenDTO> mapListEntityToListDTO(List<DiemCongXetTuyen> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addDiemCongXetTuyen(DiemCongXetTuyenDTO diemCongXetTuyenDTO){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, diemCongXetTuyenDTO.getTsCccd());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + diemCongXetTuyenDTO.getTsCccd();
            }

            DiemCongXetTuyen diemCongXetTuyen = new DiemCongXetTuyen();
            diemCongXetTuyen.setThiSinh(thiSinhGoc);
            diemCongXetTuyen.setMon(diemCongXetTuyenDTO.getMon());
            diemCongXetTuyen.setPhuongThuc(diemCongXetTuyenDTO.getPhuongThuc());
            diemCongXetTuyen.setDiemCongToHopXetTuyen(diemCongXetTuyenDTO.getDiemCongToHopXetTuyen());
            diemCongXetTuyen.setDiemCongKhongXetToHopXetTuyen(diemCongXetTuyenDTO.getDiemCongKhongXetToHopXetTuyen());
            diemCongXetTuyen.setGhiChu(diemCongXetTuyenDTO.getGhiChu());

            dao.addWithSession(session, diemCongXetTuyen);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        } finally {
            session.close();
        }
    }

    public String importUtxtCsvData(File file) {
        int batchSize = 1000;
        int successCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            Map<String, ThiSinh> cacheThiSinh = thisinhdao.getAll(session).stream()
                    .collect(Collectors.toMap(ThiSinh::getCccd, n -> n));

            String[] line;
            Transaction tx = session.beginTransaction();

            String[] danhSachPhuongThuc = {"THPT", "VSAT"};

            while ((line = csvReader.readNext()) != null) {
                try {
                    ThiSinh thiSinh = cacheThiSinh.get(line[1].trim());
                    if (thiSinh == null) continue;

                    String cap = line[2].trim().toLowerCase();
                    String monDoatGiai = line[4].trim();
                    String loaiGiai = line[5].trim().toLowerCase();
                    String ghiChu = line[5].trim() + " - " + line[3].trim() + " cấp " + line[2].trim();

                    for (String pt : danhSachPhuongThuc) {
                        BigDecimal[] diemQuyDinh = DatabaseHelper.tinhDiemUtxt(cap, loaiGiai, pt);

                        DiemCongXetTuyen entity = new DiemCongXetTuyen();
                        entity.setThiSinh(thiSinh);
                        entity.setMon(monDoatGiai);
                        entity.setPhuongThuc(pt);
                        entity.setDiemCongToHopXetTuyen(diemQuyDinh[0]);
                        entity.setDiemCongKhongXetToHopXetTuyen(diemQuyDinh[1]);
                        entity.setGhiChu(ghiChu);

                        session.persist(entity);
                        successCount++;

                        if (successCount % batchSize == 0) {
                            tx.commit();
                            session.clear();
                            tx = session.beginTransaction();
                        }
                    }
                } catch (Exception e) {
                    // System.out.println("Lỗi dòng CSV: " + String.join(",", line));
                }
            }

            if (tx.isActive()) {
                tx.commit();
            }

            return "Import thành công " + successCount + " bản ghi điểm UTXT!";

        } catch (Exception e) {
            return "Lỗi đọc file: " + e.getMessage();
        }
    }

    public String importCcCsvData(File file) {
        int batchSize = 1000;
        int successCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            Map<String, ThiSinh> cacheThiSinh = thisinhdao.getAll(session).stream()
                    .collect(Collectors.toMap(ThiSinh::getCccd, n -> n));

            String[] line;
            Transaction tx = session.beginTransaction();
            String[] danhSachPhuongThuc = {"THPT", "DGNL", "VSAT"};

            while ((line = csvReader.readNext()) != null) {
                try {
                    ThiSinh thiSinh = cacheThiSinh.get(line[1].trim());
                    if (thiSinh == null) continue;

                    String ghiChuMoi = line[2].trim();
                    String mucDiemQuyDoi = line[4].trim();
                    String monMoi = "TA";

                    List<DiemCongXetTuyen> listDiemCu = dao.getListByCccdWithSession(session, thiSinh.getCccd());

                    Map<String, DiemCongXetTuyen> mapDiemCu = listDiemCu.stream()
                            .collect(Collectors.toMap(d -> d.getPhuongThuc() + "_" + d.getMon(), d -> d));

                    for (String pt : danhSachPhuongThuc) {
                        BigDecimal diemQuyDoi = DatabaseHelper.tinhDiemChungChiTiengAnh(mucDiemQuyDoi, pt);

                        String searchKey = pt + "_" + monMoi;
                        DiemCongXetTuyen entity = mapDiemCu.get(searchKey);
                        boolean isNew = false;

                        if (entity == null) {
                            entity = new DiemCongXetTuyen();
                            entity.setThiSinh(thiSinh);
                            entity.setPhuongThuc(pt);
                            entity.setMon(monMoi);
                            entity.setGhiChu(ghiChuMoi);

                            entity.setDiemCongToHopXetTuyen(BigDecimal.ZERO);
                            entity.setDiemCongKhongXetToHopXetTuyen(BigDecimal.ZERO);
                            isNew = true;
                        }
                        else {
                            String ghiChuCu = entity.getGhiChu() == null ? "" : entity.getGhiChu() + ", ";
                            if (!ghiChuCu.contains(ghiChuMoi)) {
                                entity.setGhiChu(ghiChuCu + ghiChuMoi);
                            }
                        }

                        BigDecimal diemKhongToHopHienTai = entity.getDiemCongKhongXetToHopXetTuyen() != null ? entity.getDiemCongKhongXetToHopXetTuyen() : BigDecimal.ZERO;
                        entity.setDiemCongKhongXetToHopXetTuyen(diemKhongToHopHienTai.add(diemQuyDoi));

                        // Lưu xuống DB (Dòng cũ thì Hibernate tự Update, dòng mới thì Persist)
                        if (isNew) {
                            session.persist(entity);
                        }
                    }

                    successCount++;

                    if (successCount % batchSize == 0) {
                        tx.commit();
                        session.clear();
                        tx = session.beginTransaction();
                    }
                } catch (Exception e) {
                    // System.out.println("Lỗi dòng CSV: " + e.getMessage());
                }
            }

            if (tx.isActive()) {
                tx.commit();
            }

            return "Import thành công " + successCount + " bản ghi Chứng chỉ!";

        } catch (Exception e) {
            return "Lỗi đọc file: " + e.getMessage();
        }
    }

    public DiemCongXetTuyenDTO getDiemCongXetTuyen(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<DiemCongXetTuyenDTO> getAllDiemCongXetTuyen(){
        Session session = factory.openSession();
        List<DiemCongXetTuyen> listDiemCongXetTuyen = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listDiemCongXetTuyen);
    }

    public String updateDiemCongXetTuyen(int id, DiemCongXetTuyenDTO newDiemCongXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, newDiemCongXetTuyenDTO.getTsCccd());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + newDiemCongXetTuyenDTO.getTsCccd();
            }

            DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);

            if(diemCongXetTuyen == null){
                return "Lỗi: Không tìm thấy bảng điểm cộng với ID " + id;
            }

            diemCongXetTuyen.setThiSinh(thiSinhGoc);
            diemCongXetTuyen.setMon(newDiemCongXetTuyenDTO.getMon());
            diemCongXetTuyen.setPhuongThuc(newDiemCongXetTuyenDTO.getPhuongThuc());
            diemCongXetTuyen.setDiemCongToHopXetTuyen(newDiemCongXetTuyenDTO.getDiemCongToHopXetTuyen());
            diemCongXetTuyen.setDiemCongKhongXetToHopXetTuyen(newDiemCongXetTuyenDTO.getDiemCongKhongXetToHopXetTuyen());
            diemCongXetTuyen.setGhiChu(newDiemCongXetTuyenDTO.getGhiChu());

            dao.updateWithSession(session, diemCongXetTuyen);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteDiemCongXetTuyen(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);

            if(diemCongXetTuyen == null){
                return "Lỗi: Không tìm thấy bảng điểm cộng với ID " + id;
            }

            dao.deleteWithSession(session, diemCongXetTuyen);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
}
