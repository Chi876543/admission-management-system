package com.admissionManagement.core.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class DiemThiXetTuyenDTO {

    @NonNull
    private int idDiemThi;

    @NonNull
    private String cccd;

    @NonNull
    private String soBaoDanh;

    @NonNull
    private String phuongThuc;


    @NonNull private BigDecimal diemToan;
    @NonNull private BigDecimal diemLy;
    @NonNull private BigDecimal diemHoa;
    @NonNull private BigDecimal diemSinh;
    @NonNull private BigDecimal diemSu;
    @NonNull private BigDecimal diemDia;
    @NonNull private BigDecimal diemVan;
    @NonNull private BigDecimal diemAnh;
    @NonNull private BigDecimal diemKtpl;

    @NonNull private BigDecimal n1Thi;
    @NonNull private BigDecimal n1Cc;
    @NonNull private BigDecimal cncn;
    @NonNull private BigDecimal cnnn;
    @NonNull private BigDecimal nl1;
    @NonNull private BigDecimal nk1;
    @NonNull private BigDecimal nk2;
}
