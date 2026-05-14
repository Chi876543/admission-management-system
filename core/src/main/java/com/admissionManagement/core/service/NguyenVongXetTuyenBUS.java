package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.*;
import com.admissionManagement.core.dto.*;
import com.admissionManagement.core.entity.*;
import com.admissionManagement.core.helper.DatabaseHelper;
import com.admissionManagement.core.util.HibernateUtil;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Service
public class NguyenVongXetTuyenBUS {

    private final NguyenVongXetTuyenDAO dao;
    private final ThiSinhDAO thisinhdao;
    private final NganhDAO nganhdao;
    private final ToHopMonThiDAO tohopdao;
    private final DiemThiXetTuyenDAO diemThiXetTuyenDAO;
    private final SessionFactory factory;

    public NguyenVongXetTuyenBUS() {
        this.dao = new NguyenVongXetTuyenDAO();
        this.thisinhdao = new ThiSinhDAO();
        this.nganhdao = new NganhDAO();
        this.tohopdao = new ToHopMonThiDAO();
        this.diemThiXetTuyenDAO = new DiemThiXetTuyenDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private NguyenVongXetTuyenDTO toDTO(NguyenVongXetTuyen entity){
        return new NguyenVongXetTuyenDTO(
                entity.getIdNv(),
                entity.getThiSinh().getCccd(),
                entity.getNganh().getMaNganh(),
                entity.getThuTu(),
                entity.getDiemThxt(),
                entity.getDiemUtqd(),
                entity.getDiemCong(),
                entity.getDiemXetTuyen(),
                entity.getKetQua(),
                entity.getNvKeys(),
                entity.getPhuongThuc(),
                entity.getToHopMonThi().getMaToHop()
        );
    }

    private List<NguyenVongXetTuyenDTO> mapListEntityToListDTO(List<NguyenVongXetTuyen> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addNguyenVongXetTuyen(NguyenVongXetTuyenDTO nguyenVongXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, nguyenVongXetTuyenDTO.getCccd());
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, nguyenVongXetTuyenDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopdao.getByMaToHopWithSession(session, nguyenVongXetTuyenDTO.getThm());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + nguyenVongXetTuyenDTO.getCccd();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + nguyenVongXetTuyenDTO.getThm();
            }
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + nguyenVongXetTuyenDTO.getMaNganh();
            }

            NguyenVongXetTuyen nguyenVongXetTuyen = new NguyenVongXetTuyen();
            nguyenVongXetTuyen.setThiSinh(thiSinhGoc);
            nguyenVongXetTuyen.setNganh(nganhGoc);
            nguyenVongXetTuyen.setThuTu(nguyenVongXetTuyenDTO.getThuTu());
            nguyenVongXetTuyen.setDiemThxt(nguyenVongXetTuyenDTO.getDiemThxt());
            nguyenVongXetTuyen.setDiemUtqd(nguyenVongXetTuyenDTO.getDiemUtqd());
            nguyenVongXetTuyen.setDiemCong(nguyenVongXetTuyenDTO.getDiemCong());
            nguyenVongXetTuyen.setDiemXetTuyen(nguyenVongXetTuyenDTO.getDiemXetTuyen());
            nguyenVongXetTuyen.setKetQua(nguyenVongXetTuyenDTO.getKetQua());
            nguyenVongXetTuyen.setNvKeys(nguyenVongXetTuyenDTO.getNvKeys());
            nguyenVongXetTuyen.setPhuongThuc(nguyenVongXetTuyenDTO.getPhuongThuc());
            nguyenVongXetTuyen.setToHopMonThi(toHopGoc);

            dao.addWithSession(session, nguyenVongXetTuyen);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public NguyenVongXetTuyenDTO getNguyenVongXetTuyen(int id){
        try(Session session = factory.openSession()){
            NguyenVongXetTuyen result = dao.getWithSession(session, id);
            if(result != null)
                return toDTO(result);
            return null;
        }
    }

    public NguyenVongXetTuyenDTO getByKey(String key) {
        try (Session session = factory.openSession()) {
            NguyenVongXetTuyen entity = dao.getByKeyWithSession(session, key);
            if (entity != null)
                return toDTO(entity);
            return null;
        }
    }

    public List<NguyenVongXetTuyenDTO> getAllNganhToHop(){
        try(Session session = factory.openSession()){
            List<NguyenVongXetTuyen> result = dao.getAllWithSession(session);
            if(!result.isEmpty())
                return mapListEntityToListDTO(result);
            return Collections.emptyList();
        }
    }

    public String updateNguyenVongXetTuyen(int id, NguyenVongXetTuyenDTO newNguyenVongXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, newNguyenVongXetTuyenDTO.getCccd());
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, newNguyenVongXetTuyenDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopdao.getByMaToHopWithSession(session, newNguyenVongXetTuyenDTO.getThm());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + newNguyenVongXetTuyenDTO.getCccd();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + newNguyenVongXetTuyenDTO.getThm();
            }
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + newNguyenVongXetTuyenDTO.getMaNganh();
            }

            NguyenVongXetTuyen nguyenVongXetTuyen = dao.getWithSession(session, id);

            if(nguyenVongXetTuyen == null){
                return "Lỗi: Không tìm thấy Nguyện vọng với ID " + id;
            }

            nguyenVongXetTuyen.setThiSinh(thiSinhGoc);
            nguyenVongXetTuyen.setNganh(nganhGoc);
            nguyenVongXetTuyen.setThuTu(newNguyenVongXetTuyenDTO.getThuTu());
            nguyenVongXetTuyen.setDiemThxt(newNguyenVongXetTuyenDTO.getDiemThxt());
            nguyenVongXetTuyen.setDiemUtqd(newNguyenVongXetTuyenDTO.getDiemUtqd());
            nguyenVongXetTuyen.setDiemCong(newNguyenVongXetTuyenDTO.getDiemCong());
            nguyenVongXetTuyen.setDiemXetTuyen(newNguyenVongXetTuyenDTO.getDiemXetTuyen());
            nguyenVongXetTuyen.setKetQua(newNguyenVongXetTuyenDTO.getKetQua());
            nguyenVongXetTuyen.setNvKeys(newNguyenVongXetTuyenDTO.getNvKeys());
            nguyenVongXetTuyen.setPhuongThuc(newNguyenVongXetTuyenDTO.getPhuongThuc());
            nguyenVongXetTuyen.setToHopMonThi(toHopGoc);

            dao.updateWithSession(session, nguyenVongXetTuyen);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteNguyenVongXetTuyen(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            NguyenVongXetTuyen nguyenVongXetTuyen = dao.getWithSession(session, id);

            if(nguyenVongXetTuyen == null){
                return "Lỗi: Không tìm thấy Nguyện vọng với ID " + id;
            }

            dao.deleteWithSession(session, nguyenVongXetTuyen);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    // Dùng cho Dashboard: lấy danh sách nguyện vọng của thí sinh theo CCCD
    public List<NguyenVongXetTuyenDTO> getByThiSinhCccd(String cccd) {
        Session session = factory.openSession();
        List<NguyenVongXetTuyen> list = dao.getByThiSinhCccdWithSession(session, cccd);
        session.close();
        return mapListEntityToListDTO(list);
    }

    public List<NguyenVongXetTuyenDTO> getNguyenVongBelongToThiSinh(String sbd) {
        try (Session session = factory.openSession()) {
            ThiSinh thiSinh = thisinhdao.getBySbdWithSession(session, sbd);

            if(thiSinh == null) {
                return Collections.emptyList();
            }

            return mapListEntityToListDTO(thiSinh.getDanhSachNguyenVongCuaThiSinh());
        }
    }

    // Import
    public String importFromCsv(File file) {
        int successCount = 0;
        int errorCount = 0;

        // Mở session duy nhất để dùng chung
        try (Session session = factory.openSession()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                br.readLine(); // Bỏ qua header
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] columns = line.split("\",\"");
                    // Làm sạch dữ liệu và ép kiểu
                    String cccd = columns[1].replace("\"", "").trim();
                    int thuTu = Integer.parseInt(columns[2].replace("\"", "").trim());
                    String maNganh = columns[5].replace("\"", "").trim();

                    // GỌI HÀM: addNguyenVong sẽ tự lo Transaction bên trong
                    String result = addNguyenVong(session, cccd, maNganh, thuTu);

                    if (result.startsWith("Thêm nguyện vọng thành công")) {
                        successCount++;
                    } else {
                        errorCount++;
                        System.err.println(result); // Log lỗi để kiểm tra
                    }

                    // Giải phóng bộ nhớ đệm Hibernate
                    if (successCount % 50 == 0) {
                        session.clear();
                    }
                }
            }
        } catch (Exception e) {
            return "Lỗi hệ thống khi đọc file: " + e.getMessage();
        }
        return String.format("Hoàn thành! Thành công: %d, Thất bại: %d", successCount, errorCount);
    }


    /** Wrapper công khai không cần truyền Session — dùng cho Dialog thêm/sửa NV từ UI */
    public String addNguyenVong(String cccd, String maNganh, int thuTu) {
        try (Session session = factory.openSession()) {
            return addNguyenVong(session, cccd, maNganh, thuTu);
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }

    public String addNguyenVong(Session session, String cccd, String maNganh, int thuTu) {
        Transaction tx = null;
        try{
            tx = session.beginTransaction();

            // Kiểm tra thí sinh
            ThiSinhBUS thiSinhBUS = new ThiSinhBUS();
            ThiSinhDTO thiSinh = thiSinhBUS.getByCccd(cccd);
            if (thiSinh == null) {
                tx.rollback(); // Phải rollback trước khi return
                return "Lỗi: Thí sinh " + cccd + " không tồn tại.";
            }

            // Kiểm tra ngành
            Nganh nganh = nganhdao.getByMaNganhWithSession(session, maNganh);
            if (nganh == null) {
                tx.rollback();
                return "Lỗi: Ngành " + maNganh + " không tồn tại.";
            }

            // Lấy tổ hợp của ngành
            NganhToHopBUS nganhToHopBUS = new NganhToHopBUS();
            List<NganhToHopDTO> dsToHopDTO = nganhToHopBUS.getAllByMaNganh(maNganh);
            if (dsToHopDTO == null || dsToHopDTO.isEmpty()) {
                return "Lỗi: Ngành " + maNganh + " chưa có tổ hợp xét tuyển.";
            }

            // Lấy điểm thi
            DiemThiXetTuyen diem = diemThiXetTuyenDAO.getByCccdWithSession(session, cccd);

            // FIX: chưa có điểm → vẫn lưu NV với điểm 0, tổ hợp đầu tiên làm placeholder
            if (diem == null) {
                saveOrUpdateNV(
                        thiSinh, nganh, thuTu,
                        BigDecimal.ZERO,  // diemThxt
                        BigDecimal.ZERO,  // diemCong
                        BigDecimal.ZERO,  // diemUuTien
                        BigDecimal.ZERO,  // diemXetTuyen
                        "PT0",            // phuongThuc placeholder
                        dsToHopDTO.get(0).getMaToHop() // toHop đầu tiên làm placeholder
                );
                tx.commit();
                return "Thêm nguyện vọng thành công (chưa có điểm thi, sẽ tính lại khi import điểm).";
            }

            BangQuyDoiBUS bangQuyDoiBUS = new BangQuyDoiBUS();
            DiemCongXetTuyenBUS diemCongXetTuyenBUS = new DiemCongXetTuyenBUS();
            List<DiemCongXetTuyenDTO> dsDiemCong = diemCongXetTuyenBUS.getListByCccd(cccd);

            // Xử lý DGNL (PT2)
            if (diem.getNl1() != null) {
                BigDecimal diemCongDGNL = getDiemCongQuyChuan(dsDiemCong, "DGNL", null);
                if (diemCongDGNL.compareTo(BigDecimal.valueOf(3)) > 0) {
                    diemCongDGNL = BigDecimal.valueOf(3);
                }
                BangQuyDoiDTO quyDoi = bangQuyDoiBUS.getBangQuyDoiWithScore("DGNL", diem.getNl1(), null, nganh.getToHopGoc());
                BigDecimal diemHe30 = DatabaseHelper.quyDoiDiemVSATVaDGNL(diem.getNl1(), quyDoi);
                BigDecimal diemUuTien = DatabaseHelper.tinhDiemUuTien(thiSinh, diemCongDGNL, diemHe30);
                BigDecimal finalDiem = diemHe30.add(diemCongDGNL).add(diemUuTien);
                if (finalDiem.compareTo(BigDecimal.valueOf(30)) > 0) {
                    finalDiem = BigDecimal.valueOf(30);
                }
                saveOrUpdateNV(thiSinh, nganh, thuTu, diemHe30, diemCongDGNL, diemUuTien, finalDiem, "PT2", nganh.getToHopGoc());
            }

            // Xử lý VSAT (PT3)
            processAndSaveBestToHop(session, thiSinh, nganh, dsToHopDTO, diem, thuTu, "VSAT", "PT3", dsDiemCong, bangQuyDoiBUS);

            // Xử lý THPT (PT4)
            processAndSaveBestToHop(session, thiSinh, nganh, dsToHopDTO, diem, thuTu, "THPT", "PT4", dsDiemCong, bangQuyDoiBUS);


            tx.commit();
            return "Thêm nguyện vọng thành công cho thí sinh " + cccd;

        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    private BigDecimal getDiemCongQuyChuan(List<DiemCongXetTuyenDTO> dsDiemCong, String phuongThuc, String maMon) {
        if (dsDiemCong == null || dsDiemCong.isEmpty()) return BigDecimal.ZERO;

        BigDecimal tongCongRaw = BigDecimal.ZERO;
        for (DiemCongXetTuyenDTO dc : dsDiemCong) {

            if (phuongThuc.equalsIgnoreCase(dc.getPhuongThuc())) {
                // DGNL
                if ("DGNL".equalsIgnoreCase(phuongThuc)) {
                    BigDecimal giaTri = dc.getDiemTongKhongXetThxt();
                    tongCongRaw = giaTri != null ? giaTri : BigDecimal.ZERO;
                    return tongCongRaw.divide(new BigDecimal("40"), 5, RoundingMode.HALF_UP);
                }
                //VSAT
                if ("VSAT".equalsIgnoreCase(phuongThuc)) {

                    if (maMon != null && maMon.equalsIgnoreCase(dc.getMon())) {
                        BigDecimal giaTri = dc.getDiemTongThxt();
                        tongCongRaw = giaTri != null ? giaTri : BigDecimal.ZERO;
                    }
                    else if (dc.getMon() == null) {
                        BigDecimal giaTri = dc.getDiemTongKhongXetThxt();
                        tongCongRaw = giaTri != null ? giaTri : BigDecimal.ZERO;
                    }
                    return tongCongRaw.divide(new BigDecimal("15"), 5, RoundingMode.HALF_UP);

                }
                //THPT
                if ("THPT".equalsIgnoreCase(phuongThuc)) {

                    if (maMon != null && maMon.equalsIgnoreCase(dc.getMon())) {
                        BigDecimal giaTri = dc.getDiemTongThxt();
                        tongCongRaw = giaTri != null ? giaTri : BigDecimal.ZERO;
                    }
                    else if (dc.getMon() == null) {
                        BigDecimal giaTri = dc.getDiemTongKhongXetThxt();
                        tongCongRaw = giaTri != null ? giaTri : BigDecimal.ZERO;
                    }
                    return tongCongRaw;
                }
            }
        }

        return BigDecimal.ZERO;
    }


    private void saveOrUpdateNV(ThiSinhDTO ts, Nganh nganh,
                                int thuTu, BigDecimal diemThxt, BigDecimal diemCong,
                                BigDecimal diemUt, BigDecimal diemXetTuyen, String phuongThuc, String thm) {

        String key = ts.getCccd() + "_" + nganh.getMaNganh() + "_" + phuongThuc;

        BigDecimal diemSan = nganh.getDiemSan();
        String ketqua = "duoisan";
        if (diemSan.compareTo(diemXetTuyen) < 0) {
            ketqua = "yes";
        }



        // Kiểm tra xem đã tồn tại chưa để tránh trùng Unique Constraint
        NguyenVongXetTuyenDTO nv = this.getByKey(key);
        if (nv == null) nv = new NguyenVongXetTuyenDTO();

        nv.setCccd(ts.getCccd());
        nv.setMaNganh(nganh.getMaNganh());
        nv.setThuTu(thuTu);
        nv.setDiemThxt(diemThxt);
        nv.setDiemUtqd(diemUt);
        nv.setDiemCong(diemCong);
        nv.setDiemXetTuyen(diemXetTuyen); // Điểm đã cộng
        nv.setKetQua(ketqua);
        nv.setPhuongThuc(phuongThuc);
        nv.setThm(thm);
        nv.setNvKeys(key);

        if (nv.getIdNv() == 0) {
            this.addNguyenVongXetTuyen(nv);
        } else {
            this.updateNguyenVongXetTuyen(nv.getIdNv(), nv);
        }
    }

    private void processAndSaveBestToHop(Session session, ThiSinhDTO ts, Nganh nganh, List<NganhToHopDTO> dsToHopDTO,
                                         DiemThiXetTuyen diem, int thuTu, String phuongThuc, String maPT,
                                         List<DiemCongXetTuyenDTO> dsDiemCong, BangQuyDoiBUS bangQuyDoiBUS) {
        try {
            // Phương thức VSAT nếu có điểm thì tiếp tục
            if ("VSAT".equals(phuongThuc) && !checkHasDiemVSAT(diem)) {
                System.out.println("Khong tinh VSAT");
                return;
            }
            // Phương thức THPT nếu có điểm thì tiếp tục
            if ("THPT".equals(phuongThuc) && !checkHasDiemTHPT(diem)) {
                System.out.println("Khong tinh THPT");
                return;
            }

            BigDecimal maxDiem = BigDecimal.valueOf(-1);
            NganhToHopDTO bestToHop = null;
            BigDecimal diemThxtfinal = BigDecimal.ZERO;
            BigDecimal tongDiemCongfinal = BigDecimal.ZERO;
            BigDecimal diemUuTienfinal = BigDecimal.ZERO;

            for (NganhToHopDTO th : dsToHopDTO) {
                // Tính điểm thxt
                System.out.println(th.getMaToHop());
                BigDecimal diemThxt = tinhTHXT(diem, th, phuongThuc, dsDiemCong, bangQuyDoiBUS);
                System.out.println("Điểm THXT " + diemThxt);
                if (diemThxt == null) {
                    continue;
                }

                // Lấy điểm cộng 3 môn nếu có
                BigDecimal dc_m1 = getDiemCongQuyChuan(dsDiemCong, phuongThuc, th.getThMon1());
                BigDecimal dc_m2 = getDiemCongQuyChuan(dsDiemCong, phuongThuc, th.getThMon2());
                BigDecimal dc_m3 = getDiemCongQuyChuan(dsDiemCong, phuongThuc, th.getThMon3());
                // Tổng điểm cộng 3 môn
                BigDecimal tongDiemCong = dc_m1.add(dc_m2).add(dc_m3);

                // Điểm cộng không được vượt quá 3 điểm
                if (tongDiemCong.compareTo(BigDecimal.valueOf(3)) > 0) {
                    tongDiemCong = BigDecimal.valueOf(3);
                }
                System.out.println("Điểm Cộng " + tongDiemCong);
                // Điểm ưu tiên
                BigDecimal diemUuTien = DatabaseHelper.tinhDiemUuTien(ts, tongDiemCong, diemThxt);
                System.out.println("Điểm Ưu Tiên" + diemUuTien);

                // Tổng điểm cuối cùng
                BigDecimal finalDiem = diemThxt.add(tongDiemCong).add(diemUuTien);
                if (finalDiem != null) {

                    // Điểm tối đa 30
                    if (finalDiem.compareTo(BigDecimal.valueOf(30)) > 0) {
                        finalDiem = BigDecimal.valueOf(30);
                    }

                    // Nếu finalDiem > maxDiem Lấy tổ hợp có điểm cao nhất của ngành xét tuyển
                    if (finalDiem.compareTo(maxDiem) > 0) {
                        maxDiem = finalDiem;
                        diemThxtfinal = diemThxt;
                        tongDiemCongfinal = tongDiemCong;
                        diemUuTienfinal = diemUuTien;
                        bestToHop = th;
                    }
                }
            }

            if (bestToHop != null) {
                saveOrUpdateNV(ts, nganh, thuTu, diemThxtfinal, tongDiemCongfinal, diemUuTienfinal, maxDiem, maPT, bestToHop.getMaToHop());
            } else {
                System.err.println("Lỗi: Không tìm thấy tổ hợp hợp lệ cho thí sinh: " + ts.getCccd());
            }
        } catch (Exception e) {
            System.err.println("Lỗi dòng " + ts.getCccd() + ": " + e.getMessage());
        }
    }

    private BigDecimal tinhTHXT(DiemThiXetTuyen diem, NganhToHopDTO th, String phuongThuc,
                                List<DiemCongXetTuyenDTO> dsDiemCong, BangQuyDoiBUS bangQuyDoiBUS) {
        // Lấy điểm gốc từng môn theo phương thức (VSAT/THPT)
        BigDecimal m1 = layDiemTheoMon(diem, th.getThMon1(), phuongThuc, bangQuyDoiBUS);
        BigDecimal m2 = layDiemTheoMon(diem, th.getThMon2(), phuongThuc, bangQuyDoiBUS);
        BigDecimal m3 = layDiemTheoMon(diem, th.getThMon3(), phuongThuc, bangQuyDoiBUS);
        if (m1 == null || m2 == null || m3 == null) {
            return null;
        }
        // Tinh diem
        BigDecimal diemTHXT = DatabaseHelper.tinhDiemVSATVaTHPT(th, m1,m2,m3);

        return diemTHXT;
    }

    private BigDecimal layDiemTheoMon(DiemThiXetTuyen d, String maMon, String phuongThuc, BangQuyDoiBUS bangQuyDoiBUS) {
        if (d == null) return BigDecimal.ZERO;
        if (maMon == null || maMon.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String maMonUpper = maMon.trim().toUpperCase();

        // 1. Xử lý cho phương thức VSAT
        if ("VSAT".equalsIgnoreCase(phuongThuc)) {
            return switch (maMonUpper) {
                case "TO" -> DatabaseHelper.quyDoiDiemVSATVaDGNL(d.getDiemToanVSAT(),
                        bangQuyDoiBUS.getBangQuyDoiWithScore("VSAT", d.getDiemToanVSAT(), "TO", null));
                case "VA" -> DatabaseHelper.quyDoiDiemVSATVaDGNL(d.getDiemToanVSAT(),
                        bangQuyDoiBUS.getBangQuyDoiWithScore("VSAT", d.getDiemVanVSAT(), "VA", null));
                case "LI" -> DatabaseHelper.quyDoiDiemVSATVaDGNL(d.getDiemToanVSAT(),
                        bangQuyDoiBUS.getBangQuyDoiWithScore("VSAT", d.getDiemLyVSAT(), "LI", null));
                case "HO" -> DatabaseHelper.quyDoiDiemVSATVaDGNL(d.getDiemToanVSAT(),
                        bangQuyDoiBUS.getBangQuyDoiWithScore("VSAT", d.getDiemHoaVSAT(), "HO", null));
                case "SI" -> DatabaseHelper.quyDoiDiemVSATVaDGNL(d.getDiemToanVSAT(),
                        bangQuyDoiBUS.getBangQuyDoiWithScore("VSAT", d.getDiemSinhVSAT(), "SI", null));
                case "SU" -> DatabaseHelper.quyDoiDiemVSATVaDGNL(d.getDiemToanVSAT(),
                        bangQuyDoiBUS.getBangQuyDoiWithScore("VSAT", d.getDiemSuVSAT(), "SU", null));
                case "DI" -> DatabaseHelper.quyDoiDiemVSATVaDGNL(d.getDiemToanVSAT(),
                        bangQuyDoiBUS.getBangQuyDoiWithScore("VSAT", d.getDiemDiaVSAT(), "DI", null));
                case "N1" -> DatabaseHelper.quyDoiDiemVSATVaDGNL(d.getDiemToanVSAT(),
                        bangQuyDoiBUS.getBangQuyDoiWithScore("VSAT", d.getN1Thi(), "N1", null));
                default -> null;
            };
        }

        // 2. Xử lý cho phương thức THPT (và mặc định các môn khác)
        return switch (maMonUpper) {
            // Môn cơ bản THPT
            case "TO" -> d.getDiemToan();
            case "VA" -> d.getDiemVan();
            case "LI" -> d.getDiemLy();
            case "HO" -> d.getDiemHoa();
            case "SI" -> d.getDiemSinh();
            case "SU" -> d.getDiemSu();
            case "DI" -> d.getDiemDia();
            case "TI" -> d.getDiemTin();
            case "KTPL" -> d.getDiemKtpl();

            // Ngoại ngữ: Ưu tiên điểm quy đổi chứng chỉ (N1_CC) trước, nếu không có mới lấy điểm thi (N1_THI)
            case "N1" -> (d.getN1Cc() != null && d.getN1Cc().compareTo(BigDecimal.ZERO) > 0)
                    ? d.getN1Cc() : d.getN1Thi();

            // Các môn năng khiếu (Nếu mã tổ hợp có yêu cầu NK1, NK2...)
            case "NK1" -> d.getNk1();
            case "NK2" -> d.getNk2();
            case "NK3" -> d.getNk3();
            case "NK4" -> d.getNk4();
            case "NK5" -> d.getNk5();
            case "NK6" -> d.getNk6();

            // Các chứng chỉ/môn khác
            case "CNCN" -> d.getCncn();
            case "CNNN" -> d.getCnnn();

            default -> null;
        };
    }



    private boolean checkHasDiemVSAT(DiemThiXetTuyen d) {
        if (d == null) return false;

        // Kiểm tra xem có bất kỳ cột điểm VSAT nào khác null không
        return d.getDiemToanVSAT() != null ||
                d.getDiemVanVSAT()  != null ||
                d.getDiemLyVSAT()   != null ||
                d.getDiemHoaVSAT()  != null ||
                d.getDiemSinhVSAT() != null ||
                d.getDiemSuVSAT()   != null ||
                d.getDiemDiaVSAT()  != null ||
                d.getN1VSAT()       != null;
    }

    private boolean checkHasDiemTHPT(DiemThiXetTuyen d) {
        if (d == null) return false;

        // Kiểm tra xem có bất kỳ cột điểm VSAT nào khác null không
        return d.getDiemToan() != null ||
                d.getDiemVan()  != null ||
                d.getDiemLy()   != null ||
                d.getDiemHoa()  != null ||
                d.getDiemSinh() != null ||
                d.getDiemSu()   != null ||
                d.getDiemDia()  != null ||
                d.getCnnn()     != null ||
                d.getCncn()     != null ||
                d.getN1Cc()     != null ||
                d.getN1Thi()    != null ||
                d.getDiemTin()  != null ||
                d.getNk1()      != null ||
                d.getNk2()      != null ||
                d.getNk3()      != null ||
                d.getNk4()      != null ||
                d.getNk5()      != null ||
                d.getNk6()      != null ||
                d.getDiemKtpl()  != null;
    }


//    public static void main(String[] args) {
//        // 1. Khởi tạo BUS (Constructor này đã khởi tạo Factory và DAO bên trong)
//        NguyenVongXetTuyenBUS bus = new NguyenVongXetTuyenBUS();
//
//        Transaction tx = null;
//        // 2. Sử dụng try-with-resources để đảm bảo session luôn được đóng
//        try (Session session = bus.factory.openSession()) {
//            tx = session.beginTransaction();
//
//            // 3. Gọi DAO thông qua đối tượng đã khởi tạo trong BUS
//            // Giả sử bạn đã khai báo nganhToHopDao là một field trong BUS
//            String maNganhTest = "7480201";
//            List<NganhToHop> dsTohop = bus.nganhToHopDAO.getAllByMaNganhWithSession(session, maNganhTest);
//
//            // 4. Kiểm tra và in kết quả
//            if (dsTohop == null || dsTohop.isEmpty()) {
//                System.out.println(">>> Không tìm thấy tổ hợp nào cho ngành: " + maNganhTest);
//            } else {
//                System.out.println(">>> Tìm thấy " + dsTohop.size() + " tổ hợp:");
//                for (NganhToHop nth : dsTohop) {
//                    System.out.println("  + Mã tổ hợp: " + nth.getToHopMonThi().getMaToHop());
//                    System.out.println("    Môn: " + nth.getThMon1() + ", " + nth.getThMon2() + ", " + nth.getThMon3());
//                    System.out.println("    Môn: " + nth.getHsMon1() + ", " + nth.getHsMon2() + ", " + nth.getHsMon3());
//                }
//            }
//
//            tx.commit();
//        } catch (Exception e) {
//            if (tx != null) tx.rollback();
//            e.printStackTrace();
//        } finally {
//            // Đóng factory để kết thúc chương trình (chỉ dùng khi chạy main test)
//            if (bus.factory != null) {
//                bus.factory.close();
//            }
//        }
//    }



}