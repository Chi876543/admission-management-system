package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(
        name = "xt_diemcongxetuyen",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_diemcongxettuyen_keys", columnNames = {"dc_keys"})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manganh",
            referencedColumnName = "manganh",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_diemcongxettuyen_nganh")
    )
    private Nganh nganh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "matohop",
            referencedColumnName = "matohop",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_diemcongxettuyen_tohopmonthi")
    )
    private ToHopMonThi toHopMonThi;

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

    @Column(name = "dc_keys", length = 45, nullable = false)
    private String dcKeys;
}
