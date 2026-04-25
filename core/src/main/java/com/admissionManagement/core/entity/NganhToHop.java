package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "xt_nganh_tohop")

public class NganhToHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "manganh", length = 45, nullable = false)
    private String maNganh;

    @Column(name = "matohop", length = 45, nullable = false)
    private String maToHop;

    @Column(name = "th_mon1", length = 10) private String thMon1;
    @Column(name = "hsmon1") private Byte hsMon1; // tinyint dùng Byte

    @Column(name = "th_mon2", length = 10) private String thMon2;
    @Column(name = "hsmon2") private Byte hsMon2;

    @Column(name = "th_mon3", length = 10) private String thMon3;
    @Column(name = "hsmon3") private Byte hsMon3;

    @Column(name = "tb_keys", length = 45, unique = true)
    private String tbKeys;

    // boolean
    @Column(name = "N1") private Boolean n1;
    @Column(name = "`TO`") private Boolean toan;
    @Column(name = "LI") private Boolean ly;
    @Column(name = "HO") private Boolean hoa;
    @Column(name = "SI") private Boolean sinh;
    @Column(name = "VA") private Boolean van;
    @Column(name = "SU") private Boolean su;
    @Column(name = "DI") private Boolean dia;
    @Column(name = "TI") private Boolean anh;
    @Column(name = "KHAC") private Boolean khac;
    @Column(name = "KTPL") private Boolean ktpl;

    @Column(name = "dolech", precision = 6, scale = 2)
    private BigDecimal doLech;
}
