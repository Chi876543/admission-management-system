package com.admissionManagement.core.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiemThiXetTuyenDTO {

    @NonNull
    private int idDiemThi;

    @NonNull
    private String cccd;

    private String soBaoDanh;

    private String phuongThuc;


    private BigDecimal diemToan;
    private BigDecimal diemLy;
    private BigDecimal diemHoa;
    private BigDecimal diemSinh;
    private BigDecimal diemSu;
    private BigDecimal diemDia;
    private BigDecimal diemVan;
    private BigDecimal diemTin;
    private BigDecimal diemKtpl;

    private BigDecimal n1Thi;
    private BigDecimal n1Cc;
    private BigDecimal cncn;
    private BigDecimal cnnn;
    private BigDecimal nl1;
    private BigDecimal nk1;
    private BigDecimal nk2;
    private BigDecimal nk3;
    private BigDecimal nk4;
    private BigDecimal nk5;
    private BigDecimal nk6;

    private BigDecimal diemToanVSAT;
    private BigDecimal diemLyVSAT;
    private BigDecimal diemHoaVSAT;
    private BigDecimal diemSinhVSAT;
    private BigDecimal diemSuVSAT;
    private BigDecimal diemDiaVSAT;
    private BigDecimal diemVanVSAT;
    private BigDecimal n1VSAT;
}
