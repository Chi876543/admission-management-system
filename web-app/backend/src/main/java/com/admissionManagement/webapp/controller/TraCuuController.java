package com.admissionManagement.webapp.controller;

import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.service.NganhBUS;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
import com.admissionManagement.core.service.ThiSinhBUS;
import com.admissionManagement.core.helper.DatabaseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tra-cuu")
@CrossOrigin(origins = "http://localhost:5173")
public class TraCuuController {

    @Autowired private ThiSinhBUS thiSinhBUS;
    @Autowired private NguyenVongXetTuyenBUS nguyenVongBUS;
    @Autowired private NganhBUS nganhBUS;

    /**
     * GET /api/tra-cuu?sbd=123456&ngaySinh=01012005
     * Trả về: { thiSinh, nguyenVong }
     */
    @GetMapping
    public ResponseEntity<?> traCuu(
            @RequestParam(name = "sbd") String sbd,
            @RequestParam(name = "ngaySinh") String ngaySinh) {
        String ngaySinhFormatted = ngaySinh.replaceAll("(\\d{2})(\\d{2})(\\d{4})", "$1/$2/$3");
        ThiSinhDTO thiSinh = thiSinhBUS.findBySbd(sbd, ngaySinhFormatted);
        System.out.println(thiSinh + " " + sbd + " " + ngaySinhFormatted);
        if (thiSinh != null) {
            System.out.println(ngaySinh);
        }
        if (thiSinh == null)
            return ResponseEntity.status(404).body("Không tìm thấy thí sinh với SBD: " + sbd);
        List<NguyenVongXetTuyenDTO> nguyenVong = nguyenVongBUS.getByThiSinhCccd(thiSinh.getCccd());
        return ResponseEntity.ok(Map.of("thiSinh", thiSinh, "nguyenVong", nguyenVong));
    }

    /**
     * GET /api/tra-cuu/nganh
     * Danh sách ngành cho dropdown tính điểm
     */
    @GetMapping("/nganh")
    public ResponseEntity<List<NganhDTO>> getAllNganh() {
        return ResponseEntity.ok(nganhBUS.getAllNganh(0, 0));
    }

    @GetMapping("/tinhdiemuutien")
    public ResponseEntity<BigDecimal> tinhDiemUuTien(
        @RequestParam(name = "doiTuong", required = false, defaultValue = "") String doiTuong,
        @RequestParam(name = "khuVuc", required = false, defaultValue = "") String khuVuc,
        @RequestParam(name = "diemCong", defaultValue = "0") BigDecimal diemCong,
        @RequestParam(name = "dthgxt", defaultValue = "0") BigDecimal dthgxt) {

        ThiSinhDTO thiSinh = new ThiSinhDTO();
        thiSinh.setDoiTuong(doiTuong);
        thiSinh.setKhuVuc(khuVuc);
        
        BigDecimal ketQua = DatabaseHelper.tinhDiemUuTien(thiSinh, diemCong, dthgxt);
        return ResponseEntity.ok(ketQua);
    }

    @GetMapping("/quydoidiemvsatvadgnl")
    public ResponseEntity<BigDecimal> quyDoiDiemVSATVaDGNL(
        @RequestParam(name = "diem") BigDecimal diem,
        @RequestParam(name = "diemA") BigDecimal diemA,
        @RequestParam(name = "diemB") BigDecimal diemB,
        @RequestParam(name = "diemC") BigDecimal diemC,
        @RequestParam(name = "diemD") BigDecimal diemD ) {
        
        BangQuyDoiDTO bangQuyDoi = new BangQuyDoiDTO();
        bangQuyDoi.setDiemA(diemA);
        bangQuyDoi.setDiemB(diemB);
        bangQuyDoi.setDiemC(diemC);
        bangQuyDoi.setDiemD(diemD);
        BigDecimal ketQua = DatabaseHelper.quyDoiDiemVSATVaDGNL(diem, bangQuyDoi);
        return ResponseEntity.ok(ketQua);
    }
}