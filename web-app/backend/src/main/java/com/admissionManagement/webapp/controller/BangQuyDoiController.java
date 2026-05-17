package com.admissionManagement.webapp.controller;

import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.service.BangQuyDoiBUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bangquydoi")
@CrossOrigin(origins = "http://localhost:5173")
public class BangQuyDoiController {

    @Autowired
    private BangQuyDoiBUS bangQuyDoiBUS;

    @GetMapping("/tra-cuu")
    public ResponseEntity<?> getLuatQuyDoi(
        @RequestParam(value = "phuongThuc") String phuongThuc,
        @RequestParam(value = "diem") BigDecimal diem,
        @RequestParam(value = "mon", required = false) String mon,
        @RequestParam(value = "toHop", required = false) String toHop) {
    
        BangQuyDoiDTO result = bangQuyDoiBUS.getBangQuyDoiWithScore(phuongThuc, diem, mon, toHop);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}