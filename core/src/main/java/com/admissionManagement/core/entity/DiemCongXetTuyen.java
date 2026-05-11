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
    @Column(name = "iddiemcong")
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

    @Column(name = "diemCongToHopXetTuyen", precision = 6, scale = 2)
    private BigDecimal diemCongToHopXetTuyen;

    @Column(name = "diemCongKhongXetToHopXetTuyen", precision = 6, scale = 2)
    private BigDecimal diemCongKhongXetToHopXetTuyen;

    @Lob // Dùng cho kiểu TEXT trong SQL
    @Column(name = "ghichu")
    private String ghiChu;
}
