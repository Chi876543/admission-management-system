package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "xt_tohop_monthi",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tohopmonthi_matohop", columnNames = {"matohop"})
        }
)
@Data
public class ToHopMonThi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtohop")
    private int idToHop;

    @Column(name = "matohop", length = 45, nullable = false)
    private String maToHop;

    @OneToMany(mappedBy = "toHopMonThi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NganhToHop> danhSachNganhSuDungToHopNay = new ArrayList<>();

    @Column(name = "mon1", length = 10, nullable = false)
    private String mon1;

    @Column(name = "mon2", length = 10, nullable = false)
    private String mon2;

    @Column(name = "mon3", length = 10, nullable = false)
    private String mon3;

    @Column(name = "tentohop", length = 100)
    private String tenToHop;

    @OneToMany(mappedBy = "toHopMonThi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiemCongXetTuyen> danhSachDiemCongCuaToHop = new ArrayList<>();

    @OneToMany(mappedBy = "toHopMonThi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NguyenVongXetTuyen> danhSachNguyenVongCuaToHop = new ArrayList<>();
}
