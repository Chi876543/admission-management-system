package com.admissionManagement.webapp.controller;

import com.admissionManagement.core.dto.DiemThiXetTuyenDTO;
import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.service.DiemThiXetTuyenBUS;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
import com.admissionManagement.core.service.ThiSinhBUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller cho portal thí sinh (dashboard mới).
 * Login: CCCD + ngày sinh dạng DDMMYYYY (giống tra cứu cũ).
 * Route gốc /api/tra-cuu (dashboard cũ) vẫn giữ nguyên.
 */
@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    @Autowired private ThiSinhBUS thiSinhBUS;
    @Autowired private NguyenVongXetTuyenBUS nguyenVongBUS;
    @Autowired private DiemThiXetTuyenBUS diemThiBUS;

    /**
     * POST /api/student/login
     * Body: { "cccd": "...", "ngaySinh": "DDMMYYYY" }
     * Xác thực: tìm thí sinh theo CCCD, so ngày sinh (format DD/MM/YYYY trong DB).
     * Password mặc định của thí sinh trong DB = ngày sinh 8 số (theo spec file docx).
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String cccd = body.get("cccd");
        String ngaySinhRaw = body.get("ngaySinh"); // DDMMYYYY

        if (cccd == null || ngaySinhRaw == null) {
            return ResponseEntity.badRequest().body("Thiếu CCCD hoặc ngày sinh");
        }

        // Format ngày sinh từ DDMMYYYY → DD/MM/YYYY (format lưu trong DB)
        String ngaySinhFormatted = ngaySinhRaw.replaceAll("(\\d{2})(\\d{2})(\\d{4})", "$1/$2/$3");

        // Tìm thí sinh theo CCCD
        ThiSinhDTO thiSinh = thiSinhBUS.getByCccd(cccd);

        if (thiSinh == null) {
            return ResponseEntity.status(401).body("Không tìm thấy thí sinh với CCCD: " + cccd);
        }

        // Kiểm tra ngày sinh khớp không (đây là "mật khẩu" theo spec)
        if (!ngaySinhFormatted.equals(thiSinh.getNgaySinh())) {
            return ResponseEntity.status(401).body("Ngày sinh không đúng");
        }

        // Lấy danh sách nguyện vọng
        List<NguyenVongXetTuyenDTO> nguyenVong = nguyenVongBUS.getByThiSinhCccd(cccd);

        return ResponseEntity.ok(Map.of(
                "thiSinh", thiSinh,
                "nguyenVong", nguyenVong
        ));
    }

    /**
     * GET /api/student/diem-thi?cccd=...
     * Trả về bảng điểm thi chi tiết của thí sinh (THPT, VSAT, ĐGNL).
     * Dùng DiemThiXetTuyenBUS để tra cứu điểm theo từng phương thức.
     */
    @GetMapping("/diem-thi")
    public ResponseEntity<?> getDiemThi(@RequestParam String cccd) {
        if (cccd == null || cccd.isBlank()) {
            return ResponseEntity.badRequest().body("Thiếu CCCD");
        }

        // Dùng getByCccd thay vì filter từ getAll — hiệu quả hơn
        DiemThiXetTuyenDTO result = diemThiBUS.getByCccd(cccd);

        if (result == null) {
            return ResponseEntity.status(404).body("Không tìm thấy bảng điểm cho CCCD: " + cccd);
        }

        return ResponseEntity.ok(result);
    }
}