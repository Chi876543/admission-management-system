package com.admissionManagement.webapp.controller;

import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.service.NganhBUS;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
import com.admissionManagement.core.service.ThiSinhBUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(nganhBUS.getAllNganh());
    }
}