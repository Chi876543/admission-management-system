package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.*;
import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.dto.DiemCongXetTuyenRequestDTO;
import com.admissionManagement.core.dto.DiemCongXetTuyenRequestDTO.GiaiThuongHsgDTO;
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
                entity.getDiemUtxtToHop(),
                entity.getDiemUtxtKhongXetToHop(),
                entity.getDiemCc(),
                entity.getDiemTongThxt(),
                entity.getDiemTongKhongXetThxt(),
                entity.getGhiChu()
        );
    }

    private List<DiemCongXetTuyenDTO> mapListEntityToListDTO(List<DiemCongXetTuyen> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addDiemCongXetTuyen(DiemCongXetTuyenRequestDTO request) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, request.getTsCccd());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có CCCD " + request.getTsCccd();
            }

            List<DiemCongXetTuyen> listDiemCu = dao.getListByCccdWithSession(session, thiSinhGoc.getCccd());
            Map<String, DiemCongXetTuyen> mapDiemCu = listDiemCu.stream()
                    .collect(Collectors.toMap(d -> d.getPhuongThuc() + "_" + d.getMon(), d -> d));

            String[] danhSachPhuongThuc = {"THPT", "DGNL", "VSAT"};

            if (request.getChungChi() != null) {
                String monMoi = "N1";

                BigDecimal diemQuyDoiThang10 = DatabaseHelper.tinhDiemQuyDoiTuDiemGoc(request.getChungChi());
                if (diemQuyDoiThang10.compareTo(BigDecimal.ZERO) > 0) {
                    String mucDiemStr = diemQuyDoiThang10.toString();
                    String ghiChuMoi = request.getChungChi().getTenChungChi();
                    if (request.getChungChi().getMucDiem() != null && !request.getChungChi().getMucDiem().trim().isEmpty()) {
                        ghiChuMoi += " " + request.getChungChi().getMucDiem().trim();
                    } else if (ghiChuMoi.toUpperCase().contains("TOEIC 4")) {
                        ghiChuMoi = "TOEIC 4 kỹ năng";
                    }

                    for (String pt : danhSachPhuongThuc) {
                        BigDecimal diemCC = DatabaseHelper.tinhDiemChungChiTiengAnh(mucDiemStr, pt);

                        String key = pt + "_" + monMoi;
                        DiemCongXetTuyen entity = mapDiemCu.get(key);
                        boolean isNew = false;

                        if (entity == null) {
                            entity = new DiemCongXetTuyen();
                            entity.setThiSinh(thiSinhGoc);
                            entity.setPhuongThuc(pt);
                            entity.setMon(monMoi);
                            entity.setDiemUtxtToHop(BigDecimal.ZERO);
                            entity.setDiemUtxtKhongXetToHop(BigDecimal.ZERO);
                            entity.setGhiChu(request.getChungChi().getTenChungChi());
                            isNew = true;
                            mapDiemCu.put(key, entity);
                        } else {
                            String ghiChuCu = entity.getGhiChu() != null ? entity.getGhiChu() : "";
                            if (ghiChuCu.isEmpty()) {
                                entity.setGhiChu(ghiChuMoi);
                            } else if (!ghiChuCu.contains(ghiChuMoi)) {
                                entity.setGhiChu(ghiChuCu + ", " + ghiChuMoi);
                            }
                        }
                        entity.setDiemCc(diemCC);

                        BigDecimal utxtToHop = entity.getDiemUtxtToHop() != null ? entity.getDiemUtxtToHop() : BigDecimal.ZERO;
                        BigDecimal utxtKhongToHop = entity.getDiemUtxtKhongXetToHop() != null ? entity.getDiemUtxtKhongXetToHop() : BigDecimal.ZERO;

                        entity.setDiemTongThxt(utxtToHop);
                        entity.setDiemTongKhongXetThxt(utxtKhongToHop.add(diemCC));

                        if (isNew) {
                            dao.addWithSession(session, entity);
                        }
                    }
                }
            }

            if (request.getDanhSachGiaiThuong() != null) {
                for (GiaiThuongHsgDTO giai : request.getDanhSachGiaiThuong()) {
                    String ghiChuMoi = "Giải " + giai.getLoaiGiai() + " - Học sinh giỏi cấp " + giai.getCap();
                    for (String pt : danhSachPhuongThuc) {
                        BigDecimal[] diemUTXT = DatabaseHelper.tinhDiemUtxt(giai.getCap(), giai.getLoaiGiai(), pt);

                        String key = pt + "_" + giai.getMon();
                        DiemCongXetTuyen entity = mapDiemCu.get(key);
                        boolean isNew = false;

                        if (entity == null) {
                            entity = new DiemCongXetTuyen();
                            entity.setThiSinh(thiSinhGoc);
                            entity.setPhuongThuc(pt);
                            entity.setMon(giai.getMon());
                            entity.setDiemCc(BigDecimal.ZERO);
                            entity.setDiemUtxtToHop(BigDecimal.ZERO);
                            entity.setDiemUtxtKhongXetToHop(BigDecimal.ZERO);
                            entity.setGhiChu(request.getChungChi().getTenChungChi());
                            isNew = true;
                            mapDiemCu.put(key, entity);
                        } else {
                            String ghiChuCu = entity.getGhiChu() != null ? entity.getGhiChu() : "";
                            if (ghiChuCu.isEmpty()) {
                                entity.setGhiChu(ghiChuMoi);
                            } else if (!ghiChuCu.contains(ghiChuMoi)) {
                                entity.setGhiChu(ghiChuCu + ", " + ghiChuMoi);
                            }
                        }

                        BigDecimal diemToHopHienTai = entity.getDiemUtxtToHop() != null ? entity.getDiemUtxtToHop() : BigDecimal.ZERO;
                        BigDecimal diemKhongToHopHienTai = entity.getDiemUtxtKhongXetToHop() != null ? entity.getDiemUtxtKhongXetToHop() : BigDecimal.ZERO;

                        entity.setDiemUtxtToHop(diemToHopHienTai.max(diemUTXT[0]));
                        entity.setDiemUtxtKhongXetToHop(diemKhongToHopHienTai.max(diemUTXT[1]));

                        BigDecimal cc = entity.getDiemCc() != null ? entity.getDiemCc() : BigDecimal.ZERO;

                        entity.setDiemTongThxt(entity.getDiemUtxtToHop());
                        entity.setDiemTongKhongXetThxt(entity.getDiemUtxtKhongXetToHop().add(cc));

                        if (isNew) {
                            dao.addWithSession(session, entity);
                        }
                    }
                }
            }

            tx.commit();
            return "Thêm điểm cộng thành công cho 3 phương thức!";

        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        } finally {
            session.close();
        }
    }

    public DiemCongXetTuyenDTO addAndReturn(DiemCongXetTuyenDTO dto) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, dto.getTsCccd());
            if (thiSinhGoc == null) return null;

            DiemCongXetTuyen entity = new DiemCongXetTuyen();
            entity.setThiSinh(thiSinhGoc);
            entity.setMon(dto.getMon());
            entity.setPhuongThuc(dto.getPhuongThuc());
            entity.setDiemUtxtToHop(dto.getDiemUtxtToHop());
            entity.setDiemUtxtKhongXetToHop(dto.getDiemUtxtKhongXetToHop());
            entity.setDiemCc(dto.getDiemCc());
            entity.setDiemTongThxt(dto.getDiemTongThxt());
            entity.setDiemTongKhongXetThxt(dto.getDiemTongKhongXetThxt());
            entity.setGhiChu(dto.getGhiChu());

            dao.addWithSession(session, entity);
            tx.commit();
            return toDTO(entity); // entity.idDiemCong đã có giá trị thật sau khi commit
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
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
                        entity.setDiemUtxtToHop(diemQuyDinh[0]);
                        entity.setDiemUtxtKhongXetToHop(diemQuyDinh[1]);
                        entity.setDiemCc(BigDecimal.ZERO);
                        entity.setDiemTongThxt(entity.getDiemUtxtToHop());
                        entity.setDiemTongKhongXetThxt(entity.getDiemUtxtKhongXetToHop().add(entity.getDiemCc()));
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
                    String monMoi = "N1";

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

                            entity.setDiemUtxtToHop(BigDecimal.ZERO);
                            entity.setDiemUtxtKhongXetToHop(BigDecimal.ZERO);
                            entity.setDiemCc(BigDecimal.ZERO);
                            entity.setDiemTongThxt(BigDecimal.ZERO);
                            entity.setDiemTongKhongXetThxt(BigDecimal.ZERO);
                            isNew = true;
                        }
                        else {
                            String ghiChuCu = entity.getGhiChu() == null ? "" : entity.getGhiChu() + ", ";
                            if (!ghiChuCu.contains(ghiChuMoi)) {
                                entity.setGhiChu(ghiChuCu + ghiChuMoi);
                            }
                        }

                        entity.setDiemCc(diemQuyDoi.max(entity.getDiemCc()));

                        BigDecimal utxtKhongToHop = entity.getDiemUtxtKhongXetToHop() != null ? entity.getDiemUtxtKhongXetToHop() : BigDecimal.ZERO;
                        BigDecimal diemCC = entity.getDiemCc() != null ? entity.getDiemCc() : BigDecimal.ZERO;

                        entity.setDiemTongKhongXetThxt(utxtKhongToHop.add(diemCC));

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
            DiemCongXetTuyen result = dao.getWithSession(session, id);
            if(result != null)
                return toDTO(result);
            return null;
        }
    }

    public List<DiemCongXetTuyenDTO> getAllDiemCongXetTuyen(){
        try(Session session = factory.openSession()){
            List<DiemCongXetTuyen> result = dao.getAll(session);
            if(!result.isEmpty())
                return mapListEntityToListDTO(result);
            return java.util.Collections.emptyList();
        }
    }

    public List<DiemCongXetTuyenDTO> getAllDiemCongXetTuyenWithCccd(String keyword){
        String cccd = null;

        if(keyword != null && !keyword.trim().isEmpty()) {
            if (keyword.matches("^\\d{9,12}$")) {
                cccd = keyword;
            }
        }

        try(Session session = factory.openSession()){
            List<DiemCongXetTuyen> listDiemCongXetTuyen = dao.getAllWithSession(session, cccd);
            return mapListEntityToListDTO(listDiemCongXetTuyen);
        }
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
            diemCongXetTuyen.setDiemUtxtToHop(newDiemCongXetTuyenDTO.getDiemUtxtToHop());
            diemCongXetTuyen.setDiemUtxtKhongXetToHop(newDiemCongXetTuyenDTO.getDiemUtxtKhongXetToHop());
            diemCongXetTuyen.setDiemCc(newDiemCongXetTuyenDTO.getDiemCc());
            diemCongXetTuyen.setDiemTongThxt(newDiemCongXetTuyenDTO.getDiemTongThxt());
            diemCongXetTuyen.setDiemTongKhongXetThxt(newDiemCongXetTuyenDTO.getDiemTongKhongXetThxt());
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

    public List<DiemCongXetTuyenDTO> getListByCccd(String cccd) {
        try (Session session = factory.openSession()) {
            List<DiemCongXetTuyen> entities = dao.getListByCccdWithSession(session, cccd);

            if (entities == null || entities.isEmpty()) {
                return java.util.Collections.emptyList();
            }

            return mapListEntityToListDTO(entities);
        } catch (Exception e) {
            System.err.println("Lỗi lấy điểm cộng cho CCCD " + cccd + ": " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }
}
