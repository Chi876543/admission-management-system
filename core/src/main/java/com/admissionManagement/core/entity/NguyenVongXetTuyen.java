package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(
        name = "xt_nguyenvongxettuyen",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_nguyenvong_keys", columnNames = {"nv_keys"})
        },
        indexes = {
                @Index(name = "idx_nguyenvong_cccd",    columnList = "nn_cccd"),
                @Index(name = "idx_nguyenvong_manganh", columnList = "nv_manganh")
        }
)
@Data
public class NguyenVongXetTuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnv", nullable = false)
    private int idNv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "nn_cccd",
            referencedColumnName = "cccd",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_nguyenvong_thisinh")
    )
    private ThiSinh thiSinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "nv_manganh",
            referencedColumnName = "manganh",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_nguyenvong_nganh")
    )
    private Nganh nganh;

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

    @Column(name = "nv_keys", length = 45, nullable = false)
    private String nvKeys;

    @Column(name = "tt_phuongthuc", length = 45)
    private String phuongThuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tt_thm",
            referencedColumnName = "matohop",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_nguyenvong_tohop")
    )
    private ToHopMonThi toHopMonThi;
}
