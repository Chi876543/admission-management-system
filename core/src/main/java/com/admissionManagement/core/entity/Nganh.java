package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "xt_nganh")

public class Nganh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnganh")
    private int idNganh;

    @Column(name = "manganh", length = 45, nullable = false)
    private String maNganh;

    @Column(name = "tennganh", length = 100, nullable = false)
    private String tenNganh;

    @Column(name = "n_tohopgoc", length = 3)
    private String toHopGoc;

    @Column(name = "n_chitieu", nullable = false)
    private int chiTieu;

    @Column(name = "n_diemsan", precision = 10, scale = 2)
    private BigDecimal diemSan;

    @Column(name = "n_diemtrungtuyen", precision = 10, scale = 2)
    private BigDecimal diemTrungTuyen;

    @Column(name = "n_tuyenthang", length = 1) private String tuyenThang;
    @Column(name = "n_dgnl", length = 1) private String dgnl;
    @Column(name = "n_thpt", length = 1) private String thpt;
    @Column(name = "n_vsat", length = 1) private String vsat;

    @Column(name = "sl_xtt") private Integer slXtt;
    @Column(name = "sl_dgnl") private Integer slDgnl;
    @Column(name = "sl_vsat") private Integer slVsat;
    @Column(name = "sl_thpt", length = 45) private String slThpt;
}
