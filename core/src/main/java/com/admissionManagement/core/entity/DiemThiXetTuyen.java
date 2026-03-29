package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "xt_diemthixettuyen")

public class DiemThiXetTuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddiemthi")
    private int idDiemThi;

    @Column(name = "cccd", length = 20, nullable = false, unique = true)
    private String cccd;

    @Column(name = "sobaodanh", length = 45)
    private String soBaoDanh;

    @Column(name = "d_phuongthuc", length = 10)
    private String phuongThuc;

    // Map các môn thi
    @Column(name = "TO", precision = 8, scale = 2) private BigDecimal diemToan;
    @Column(name = "LI", precision = 8, scale = 2) private BigDecimal diemLy;
    @Column(name = "HO", precision = 8, scale = 2) private BigDecimal diemHoa;
    @Column(name = "SI", precision = 8, scale = 2) private BigDecimal diemSinh;
    @Column(name = "SU", precision = 8, scale = 2) private BigDecimal diemSu;
    @Column(name = "DI", precision = 8, scale = 2) private BigDecimal diemDia;
    @Column(name = "VA", precision = 8, scale = 2) private BigDecimal diemVan;
    @Column(name = "TI", precision = 8, scale = 2) private BigDecimal diemAnh;
    @Column(name = "KTPL", precision = 8, scale = 2) private BigDecimal diemKtpl;

    @Column(name = "N1_THI", precision = 8, scale = 2) private BigDecimal n1Thi;
    @Column(name = "N1_CC", precision = 8, scale = 2) private BigDecimal n1Cc;
    @Column(name = "CNCN", precision = 8, scale = 2) private BigDecimal cncn;
    @Column(name = "CNNN", precision = 8, scale = 2) private BigDecimal cnnn;
    @Column(name = "NL1", precision = 8, scale = 2) private BigDecimal nl1;
    @Column(name = "NK1", precision = 8, scale = 2) private BigDecimal nk1;
    @Column(name = "NK2", precision = 8, scale = 2) private BigDecimal nk2;
}
