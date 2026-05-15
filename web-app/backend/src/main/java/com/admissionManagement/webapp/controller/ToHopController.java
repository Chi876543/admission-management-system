package com.admissionManagement.webapp.controller;

import com.admissionManagement.core.dto.ToHopMonThiDTO;
import com.admissionManagement.core.service.ToHopMonThiBUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/to-hop")
@CrossOrigin(origins = "http://localhost:5173")
public class ToHopController {

    @Autowired
    private ToHopMonThiBUS toHopBUS;

    @GetMapping
    public ResponseEntity<List<ToHopMonThiDTO>> getAll() {
        return ResponseEntity.ok(toHopBUS.getAllToHopMonThi(0, 0));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        ToHopMonThiDTO dto = toHopBUS.getToHopMonThi(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}