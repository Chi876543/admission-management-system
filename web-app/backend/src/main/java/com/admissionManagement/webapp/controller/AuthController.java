package com.admissionManagement.webapp.controller;

import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.service.ThiSinhBUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private ThiSinhBUS thiSinhBUS;

    // Login.jsx gửi lên: { email: "...", password: "..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        // Tìm thí sinh theo email hoặc CCCD
        ThiSinhDTO thiSinh = thiSinhBUS.findByEmailOrCccd(email, password);

        if (thiSinh == null) {
            return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu");
        }
        return ResponseEntity.ok(thiSinh);
    }

    // Register.jsx gửi lên: { ho, ten, cccd, dien_thoai, email, ngay_sinh, gioi_tinh, noi_sinh, khu_vuc, password }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {
        ThiSinhDTO dto = new ThiSinhDTO();
        dto.setHo(body.get("ho"));
        dto.setTen(body.get("ten"));
        dto.setCccd(body.get("cccd"));
        dto.setDienThoai(body.get("dien_thoai"));
        dto.setEmail(body.get("email"));
        dto.setNgaySinh(body.get("ngay_sinh"));
        dto.setGioiTinh(body.get("gioi_tinh"));
        dto.setNoiSinh(body.get("noi_sinh"));
        dto.setKhuVuc(body.get("khu_vuc"));
        dto.setPassword(body.get("password"));

        String result = thiSinhBUS.addThiSinh(dto);
        if (result.startsWith("Lỗi")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}