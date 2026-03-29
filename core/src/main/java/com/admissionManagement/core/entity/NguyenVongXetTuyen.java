package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_nguyenvongxettuyen")
@Data
public class NguyenVongXetTuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnv")
    private int idNv;

    @Column(name = "nn_cccd", length = 45, nullable = false)
    private String cccd;

    @Column(name = "nv_manganh", length = 45, nullable = false)
    private String maNganh;

    @Column(name = "nv_tt", nullable = false)
    private int thuTu;

    @Column(name = "diem_thxt", precision = 10, scale = 5)
    private BigDecimal diemThxt;

    @Column(name = "diem_utqd", precision = 10, scale = 5)
    private BigDecimal diemUtqd;

    @Column(name = "diem_cong", precision = 6, scale = 2)
    private BigDecimal diemCong;

    @Column(name = "diem_xettuyen", precision = 10, scale = 5)
    private BigDecimal diemXetTuyen;

    @Column(name = "nv_ketqua", length = 45)
    private String ketQua;

    @Column(name = "nv_keys", length = 45, unique = true)
    private String nvKeys;

    @Column(name = "tt_phuongthuc", length = 45)
    private String phuongThuc;

    @Column(name = "tt_thm", length = 45)
    private String thm;
}
