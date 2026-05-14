package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(
        name = "xt_bangquydoi",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_bangquydoi_keys", columnNames = {"d_maquydoi"})
        },
        indexes = {
                @Index(name = "idx_bangquydoi_phuongthuc", columnList = "d_phuongthuc"),
                @Index(name = "idx_bangquydoi_tohop",      columnList = "d_tohop"),
                @Index(name = "idx_bangquydoi_mon",        columnList = "d_mon")
        }
)

public class BangQuyDoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idqd", nullable = false)
    private int idqd;

    @Column(name = "d_phuongthuc", length = 45)
    private String phuongThuc;

    @Column(name = "d_tohop", length = 45)
    private String toHop;

    @Column(name = "d_mon", length = 45)
    private String mon;

    @Column(name = "d_diema", precision = 6, scale = 2)
    private BigDecimal diemA;

    @Column(name = "d_diemb", precision = 6, scale = 2)
    private BigDecimal diemB;

    @Column(name = "d_diemc", precision = 6, scale = 2)
    private BigDecimal diemC;

    @Column(name = "d_diemd", precision = 6, scale = 2)
    private BigDecimal diemD;

    @Column(name = "d_maquydoi", length = 45, unique = true, nullable = false)
    private String maQuyDoi;

    @Column(name = "d_phanvi", length = 45)
    private String phanVi;
}
