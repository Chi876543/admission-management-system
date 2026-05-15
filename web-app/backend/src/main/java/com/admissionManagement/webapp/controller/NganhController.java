package com.admissionManagement.webapp.controller;

import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.service.NganhBUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nganh")
@CrossOrigin(origins = "http://localhost:5173")
public class NganhController {

    @Autowired
    private NganhBUS nganhBUS;

    @GetMapping
    public ResponseEntity<List<NganhDTO>> getAll() {
        return ResponseEntity.ok(nganhBUS.getAllNganh(0, 0));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        NganhDTO dto = nganhBUS.getNganh(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody NganhDTO dto) {
        String result = nganhBUS.addBangQuyDoi(dto);
        if (result.startsWith("Lỗi")) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody NganhDTO dto) {
        String result = nganhBUS.updateNganh(id, dto);
        if (result.startsWith("Lỗi")) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        String result = nganhBUS.deleteNganh(id);
        if (result.startsWith("Lỗi")) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }
}