package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(
        name = "xt_diemcongxetuyen",
        indexes = {
                @Index(name = "idx_diemcong_cccd",      columnList = "ts_cccd"),
                @Index(name = "idx_diemcong_mon",       columnList = "mon"),
                @Index(name = "idx_diemcong_phuongthuc",columnList = "phuongthuc"),
                @Index(name = "idx_diemcong_ghichu",    columnList = "ghichu(255)")
        }
)

public class DiemCongXetTuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddiemcong", nullable = false)
    private int idDiemCong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ts_cccd",
            referencedColumnName = "cccd",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_diemcongxettuyen_thisinh")
    )
    private ThiSinh thiSinh;

    @Column(name = "mon", length = 10)
    private String mon;

    @Column(name = "phuongthuc", length = 45)
    private String phuongThuc;

    @Column(name = "diemUtxtToHop", precision = 6, scale = 2)
    private BigDecimal diemUtxtToHop;

    @Column(name = "diemUtxtKhongXetToHop", precision = 6, scale = 2)
    private BigDecimal diemUtxtKhongXetToHop;

    @Column(name = "diemCc", precision = 6, scale = 2)
    private BigDecimal diemCc;

    @Column(name = "diemTongThxt", precision = 6, scale = 2)
    private BigDecimal diemTongThxt;

    @Column(name = "diemTongKhongXetThxt", precision = 6, scale = 2)
    private BigDecimal diemTongKhongXetThxt;

    @Lob // Dùng cho kiểu TEXT trong SQL
    @Column(name = "ghichu")
    private String ghiChu;
}
