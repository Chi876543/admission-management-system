package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Data
@Entity
@Table(
        name = "xt_nganh_tohop",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_nganhtohop_keys", columnNames = {"tb_keys"})
        }
)

public class NganhToHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manganh",
            referencedColumnName = "manganh",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_nganhtohop_nganh")
    )
    private Nganh nganh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "matohop",
            referencedColumnName = "matohop",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_nganhtohop_tohop")
    )
    private ToHopMonThi toHopMonThi;

    @Column(name = "th_mon1", length = 10) private String thMon1;
    @Column(name = "hsmon1") private Byte hsMon1; // tinyint dùng Byte

    @Column(name = "th_mon2", length = 10) private String thMon2;
    @Column(name = "hsmon2") private Byte hsMon2;

    @Column(name = "th_mon3", length = 10) private String thMon3;
    @Column(name = "hsmon3") private Byte hsMon3;

    @Column(name = "tb_keys", length = 45)
    private String tbKeys;

    // boolean
    @Column(name = "N1") private Boolean anh;
    @Column(name = "`TO`") private Boolean toan;
    @Column(name = "LI") private Boolean ly;
    @Column(name = "HO") private Boolean hoa;
    @Column(name = "SI") private Boolean sinh;
    @Column(name = "VA") private Boolean van;
    @Column(name = "SU") private Boolean su;
    @Column(name = "DI") private Boolean dia;
    @Column(name = "TI") private Boolean tin;
    @Column(name = "NK1") private Boolean nk1;
    @Column(name = "NK2") private Boolean nk2;
    @Column(name = "NK3") private Boolean nk3;
    @Column(name = "NK4") private Boolean nk4;
    @Column(name = "NK5") private Boolean nk5;
    @Column(name = "NK6") private Boolean nk6;
    @Column(name = "CNCN") private Boolean cncn;
    @Column(name = "CNNN") private Boolean cnnn;
    @Column(name = "KHAC") private Boolean khac;
    @Column(name = "KTPL") private Boolean ktpl;

    @ColumnDefault("0.00")
    @Column(name = "dolech", precision = 6, scale = 2)
    private BigDecimal doLech;
}
