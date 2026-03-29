package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "xt_diemcongxetuyen")

public class DiemCongXetTuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddiemcong")
    private int idDiemCong;

    @Column(name = "ts_cccd", length = 45, nullable = false)
    private String tsCccd;

    @Column(name = "manganh", length = 20)
    private String maNganh;

    @Column(name = "matohop", length = 10)
    private String maToHop;

    @Column(name = "phuongthuc", length = 45)
    private String phuongThuc;

    @Column(name = "diemCC", precision = 6, scale = 2)
    private BigDecimal diemCC;

    @Column(name = "diemUtxt", precision = 6, scale = 2)
    private BigDecimal diemUtxt;

    @Column(name = "diemTong", precision = 6, scale = 2)
    private BigDecimal diemTong;

    @Lob // Dùng cho kiểu TEXT trong SQL
    @Column(name = "ghichu")
    private String ghiChu;

    @Column(name = "dc_keys", length = 45, nullable = false, unique = true)
    private String dcKeys;
}
