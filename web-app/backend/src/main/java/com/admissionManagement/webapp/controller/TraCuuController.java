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

    @Autowired
    private ThiSinhBUS thiSinhBUS;

    @Autowired
    private NguyenVongXetTuyenBUS nguyenVongXetTuyenBUS;

    @Autowired
    private NganhBUS nganhBUS;

    // Dashboard.jsx gọi: GET /api/tra-cuu?sbd=123456&ngaySinh=01012005
    @GetMapping
    public ResponseEntity<?> traCuu(
            @RequestParam String sbd,
            @RequestParam String ngaySinh
    ) {
        ThiSinhDTO thiSinh = thiSinhBUS.findBySbd(sbd, ngaySinh);
        if (thiSinh == null) {
            return ResponseEntity.status(404).body("Không tìm thấy thí sinh");
        }

        List<NguyenVongXetTuyenDTO> nguyenVong =
                nguyenVongXetTuyenBUS.getByThiSinhCccd(thiSinh.getCccd());

        return ResponseEntity.ok(Map.of(
                "thiSinh", thiSinh,
                "nguyenVong", nguyenVong
        ));
    }

    // Dashboard.jsx cần danh sách ngành để hiển thị trong dropdown công cụ tính điểm
    @GetMapping("/nganh")
    public ResponseEntity<List<NganhDTO>> getAllNganh() {
        return ResponseEntity.ok(nganhBUS.getAllNganh());
    }
}