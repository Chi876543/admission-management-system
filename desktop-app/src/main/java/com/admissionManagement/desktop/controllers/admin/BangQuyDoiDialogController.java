package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.service.BangQuyDoiBUS;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class BangQuyDoiDialogController extends BaseController {

    private BangQuyDoiBUS bangQuyDoiBUS;
    private Stage dialogStage;
    private BangQuyDoiDTO editingRow;
    private boolean isSaved = false;

    @FXML private Label lblDialogTitle, lblError;
    @FXML private TextField tfPhuongThuc, tfToHop, tfMon,
            tfDiemA, tfDiemB, tfDiemC, tfDiemD,
            tfMaQuyDoi, tfPhanVi;

    public void init(Stage stage, BangQuyDoiDTO row, BangQuyDoiBUS bus) {
        this.dialogStage   = stage;
        this.editingRow    = row;
        this.bangQuyDoiBUS = bus;

        if (row != null) {
            lblDialogTitle.setText("Sửa luật quy đổi ID: " + row.getIdqd());
            tfPhuongThuc.setText(row.getPhuongThuc());
            tfToHop.setText(row.getToHop());
            tfMon.setText(row.getMon());
            tfDiemA.setText(row.getDiemA() != null ? row.getDiemA().toPlainString() : "");
            tfDiemB.setText(row.getDiemB() != null ? row.getDiemB().toPlainString() : "");
            tfDiemC.setText(row.getDiemC() != null ? row.getDiemC().toPlainString() : "");
            tfDiemD.setText(row.getDiemD() != null ? row.getDiemD().toPlainString() : "");
            tfMaQuyDoi.setText(row.getMaQuyDoi());
            tfPhanVi.setText(row.getPhanVi());
        } else {
            lblDialogTitle.setText("Thêm luật quy đổi mới");
        }
    }

    @FXML
    private void onDialogSave() {
        lblError.setText("");

        // --- Validate required ---
        if (tfMaQuyDoi.getText().trim().isEmpty()) {
            lblError.setText("Mã quy đổi không được để trống.");
            return;
        }
        if (tfPhuongThuc.getText().trim().isEmpty()) {
            lblError.setText("Phương thức không được để trống.");
            return;
        }

        // --- Parse từng ô điểm riêng ---
        BigDecimal dA = parseBigDecimal(tfDiemA, "Điểm A");
        if (dA == null) return;
        BigDecimal dB = parseBigDecimal(tfDiemB, "Điểm B");
        if (dB == null) return;
        BigDecimal dC = parseBigDecimal(tfDiemC, "Điểm C");
        if (dC == null) return;
        BigDecimal dD = parseBigDecimal(tfDiemD, "Điểm D");
        if (dD == null) return;

        // --- Validate logic ---
        if (dA.compareTo(dB) > 0) {
            lblError.setText("Điểm A phải nhỏ hơn hoặc bằng Điểm B.");
            return;
        }
        if (dC.compareTo(dD) > 0) {
            lblError.setText("Điểm C phải nhỏ hơn hoặc bằng Điểm D.");
            return;
        }

        // --- Gọi BUS ---
        String result;
        if (editingRow == null) {
            BangQuyDoiDTO newDTO = new BangQuyDoiDTO(
                    0,
                    tfPhuongThuc.getText().trim(),
                    tfToHop.getText().trim(),
                    tfMon.getText().trim(),
                    dA, dB, dC, dD,
                    tfMaQuyDoi.getText().trim(),
                    tfPhanVi.getText().trim()
            );
            result = bangQuyDoiBUS.addBangQuyDoi(newDTO);
        } else {
            editingRow.setPhuongThuc(tfPhuongThuc.getText().trim());
            editingRow.setToHop(tfToHop.getText().trim());
            editingRow.setMon(tfMon.getText().trim());
            editingRow.setDiemA(dA);
            editingRow.setDiemB(dB);
            editingRow.setDiemC(dC);
            editingRow.setDiemD(dD);
            editingRow.setMaQuyDoi(tfMaQuyDoi.getText().trim());
            editingRow.setPhanVi(tfPhanVi.getText().trim());
            result = bangQuyDoiBUS.updateBangQuyDoi(editingRow.getIdqd(), editingRow);
        }

        if (result.startsWith("Lỗi")) {
            lblError.setText(result);
            return;
        }

        showInfo("Thành công", result);
        isSaved = true;
        dialogStage.close();
    }

    @FXML
    private void onDialogCancel() {
        dialogStage.close();
    }

    public boolean getIsSaved() {
        return isSaved;
    }

    private BigDecimal parseBigDecimal(TextField tf, String fieldName) {
        try {
            return new BigDecimal(tf.getText().trim());
        } catch (NumberFormatException e) {
            lblError.setText(fieldName + " phải là số hợp lệ (VD: 10.00).");
            return null;
        }
    }
}