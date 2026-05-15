package com.admissionManagement.webapp.controller;

import com.admissionManagement.core.dto.NganhToHopDTO;
import com.admissionManagement.core.service.NganhToHopBUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nganh-to-hop")
@CrossOrigin(origins = "http://localhost:5173")
public class NganhToHopController {

    @Autowired
    private NganhToHopBUS nganhToHopBUS;

    // Lấy tất cả hoặc lọc theo mã ngành
    @GetMapping
    public ResponseEntity<List<NganhToHopDTO>> getAll(
            @RequestParam(name = "maNganh", required = false) String maNganh) {
        List<NganhToHopDTO> list = nganhToHopBUS.getAllNganhToHop();
        if (maNganh != null && !maNganh.isEmpty()) {
            list = list.stream()
                    .filter(dto -> dto.getMaNganh().equals(maNganh))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(list);
    }
}