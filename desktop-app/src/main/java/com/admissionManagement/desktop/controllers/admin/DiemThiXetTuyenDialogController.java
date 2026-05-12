package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemThiXetTuyenDTO;
import com.admissionManagement.core.service.DiemThiXetTuyenBUS;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class DiemThiXetTuyenDialogController extends BaseController {

    private DiemThiXetTuyenBUS bus;
    private Stage dialogStage;
    private DiemThiXetTuyenDTO editingRow;
    private boolean isSaved = false;

    @FXML private Label lblDialogTitle, lblError;
    @FXML private TextField tfCccd, tfSbd, tfPhuongThuc;

    // THPT
    @FXML private TextField tfToan, tfLy, tfHoa, tfSinh, tfSu, tfDia,
            tfVan, tfTin, tfKtpl, tfN1Thi, tfN1Cc, tfCncn, tfCnnn;
    // VSAT
    @FXML private TextField tfToanVsat, tfLyVsat, tfHoaVsat, tfSinhVsat,
            tfSuVsat, tfDiaVsat, tfVanVsat, tfN1Vsat;
    // ĐGNL & Năng khiếu
    @FXML private TextField tfNl1, tfNk1, tfNk2, tfNk3, tfNk4, tfNk5, tfNk6;

    public void init(Stage stage, DiemThiXetTuyenDTO row, DiemThiXetTuyenBUS bus) {
        this.dialogStage = stage;
        this.editingRow  = row;
        this.bus         = bus;

        if (row != null) {
            lblDialogTitle.setText("Sửa điểm thi thí sinh: " + row.getCccd());
            tfCccd.setText(row.getCccd());
            tfCccd.setEditable(false);
            tfSbd.setText(row.getSoBaoDanh() != null ? row.getSoBaoDanh() : "");
            tfPhuongThuc.setText(row.getPhuongThuc() != null ? row.getPhuongThuc() : "");

            // THPT
            tfToan.setText(val(row.getDiemToan()));
            tfLy.setText(val(row.getDiemLy()));
            tfHoa.setText(val(row.getDiemHoa()));
            tfSinh.setText(val(row.getDiemSinh()));
            tfSu.setText(val(row.getDiemSu()));
            tfDia.setText(val(row.getDiemDia()));
            tfVan.setText(val(row.getDiemVan()));
            tfTin.setText(val(row.getDiemTin()));
            tfKtpl.setText(val(row.getDiemKtpl()));
            tfN1Thi.setText(val(row.getN1Thi()));
            tfN1Cc.setText(val(row.getN1Cc()));
            tfCncn.setText(val(row.getCncn()));
            tfCnnn.setText(val(row.getCnnn()));

            // VSAT
            tfToanVsat.setText(val(row.getDiemToanVSAT()));
            tfLyVsat.setText(val(row.getDiemLyVSAT()));
            tfHoaVsat.setText(val(row.getDiemHoaVSAT()));
            tfSinhVsat.setText(val(row.getDiemSinhVSAT()));
            tfSuVsat.setText(val(row.getDiemSuVSAT()));
            tfDiaVsat.setText(val(row.getDiemDiaVSAT()));
            tfVanVsat.setText(val(row.getDiemVanVSAT()));
            tfN1Vsat.setText(val(row.getN1VSAT()));

            // ĐGNL & Năng khiếu
            tfNl1.setText(val(row.getNl1()));
            tfNk1.setText(val(row.getNk1()));
            tfNk2.setText(val(row.getNk2()));
            tfNk3.setText(val(row.getNk3()));
            tfNk4.setText(val(row.getNk4()));
            tfNk5.setText(val(row.getNk5()));
            tfNk6.setText(val(row.getNk6()));
        } else {
            lblDialogTitle.setText("Thêm bảng điểm mới");
        }
    }

    @FXML
    private void onDialogSave() {
        lblError.setText("");

        if (tfCccd.getText().trim().isEmpty()) {
            lblError.setText("CCCD không được để trống.");
            return;
        }

        try {
            DiemThiXetTuyenDTO dto = new DiemThiXetTuyenDTO();
            if (editingRow != null) dto.setIdDiemThi(editingRow.getIdDiemThi());

            dto.setCccd(tfCccd.getText().trim());
            dto.setSoBaoDanh(tfSbd.getText().trim());
            dto.setPhuongThuc(tfPhuongThuc.getText().trim());

            // THPT
            dto.setDiemToan(parse(tfToan.getText()));
            dto.setDiemLy(parse(tfLy.getText()));
            dto.setDiemHoa(parse(tfHoa.getText()));
            dto.setDiemSinh(parse(tfSinh.getText()));
            dto.setDiemSu(parse(tfSu.getText()));
            dto.setDiemDia(parse(tfDia.getText()));
            dto.setDiemVan(parse(tfVan.getText()));
            dto.setDiemTin(parse(tfTin.getText()));
            dto.setDiemKtpl(parse(tfKtpl.getText()));
            dto.setN1Thi(parse(tfN1Thi.getText()));
            dto.setN1Cc(parse(tfN1Cc.getText()));
            dto.setCncn(parse(tfCncn.getText()));
            dto.setCnnn(parse(tfCnnn.getText()));

            // VSAT
            dto.setDiemToanVSAT(parse(tfToanVsat.getText()));
            dto.setDiemLyVSAT(parse(tfLyVsat.getText()));
            dto.setDiemHoaVSAT(parse(tfHoaVsat.getText()));
            dto.setDiemSinhVSAT(parse(tfSinhVsat.getText()));
            dto.setDiemSuVSAT(parse(tfSuVsat.getText()));
            dto.setDiemDiaVSAT(parse(tfDiaVsat.getText()));
            dto.setDiemVanVSAT(parse(tfVanVsat.getText()));
            dto.setN1VSAT(parse(tfN1Vsat.getText()));

            // ĐGNL & Năng khiếu
            dto.setNl1(parse(tfNl1.getText()));
            dto.setNk1(parse(tfNk1.getText()));
            dto.setNk2(parse(tfNk2.getText()));
            dto.setNk3(parse(tfNk3.getText()));
            dto.setNk4(parse(tfNk4.getText()));
            dto.setNk5(parse(tfNk5.getText()));
            dto.setNk6(parse(tfNk6.getText()));

            String result = (editingRow == null)
                    ? bus.addDiemThiXetTuyen(dto)
                    : bus.updateDiemThiXetTuyen(dto.getIdDiemThi(), dto);

            if (result.startsWith("Lỗi")) {
                lblError.setText(result);
                return;
            }

            showInfo("Thành công", result);
            isSaved = true;
            dialogStage.close();

        } catch (NumberFormatException e) {
            lblError.setText("Lỗi: Các ô điểm phải nhập số hợp lệ (VD: 8.50).");
        }
    }

    @FXML
    private void onDialogCancel() {
        dialogStage.close();
    }

    public boolean getIsSaved() {
        return isSaved;
    }

    // --- Helpers ---
    private String val(BigDecimal d) {
        return d == null ? "" : d.toPlainString();
    }

    // Để trống = null (không phải ZERO như code cũ)
    private BigDecimal parse(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return new BigDecimal(s.trim());
    }
}