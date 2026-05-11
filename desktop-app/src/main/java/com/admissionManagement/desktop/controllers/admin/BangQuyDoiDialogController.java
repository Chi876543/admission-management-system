package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.service.BangQuyDoiBUS;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class BangQuyDoiDialogController extends BaseController {
    private final BangQuyDoiBUS bangQuyDoiBUS = new BangQuyDoiBUS();
    private Stage dialogStage;
    private BangQuyDoiDTO editingRow;
    private boolean isSaved = false;

    @FXML private Label lblDialogTitle, lblError;
    @FXML private TextField tfPhuongThuc, tfToHop, tfMon, tfDiemA, tfDiemB, tfDiemC, tfDiemD, tfMaQuyDoi, tfPhanVi;

    @FXML
    public void initialize() {
        // Có thể khởi tạo ComboBox cho Phương thức hoặc Tổ hợp nếu cần giống bên Thí sinh
    }

    public void setDialogData(Stage stage, BangQuyDoiDTO row) {
        this.dialogStage = stage;
        this.editingRow = row;

        if (row != null) {
            lblDialogTitle.setText("Sửa luật quy đổi ID: " + row.getIdqd());
            tfPhuongThuc.setText(row.getPhuongThuc());
            tfToHop.setText(row.getToHop());
            tfMon.setText(row.getMon());
            tfDiemA.setText(row.getDiemA() != null ? row.getDiemA().toString() : "");
            tfDiemB.setText(row.getDiemB() != null ? row.getDiemB().toString() : "");
            tfDiemC.setText(row.getDiemC() != null ? row.getDiemC().toString() : "");
            tfDiemD.setText(row.getDiemD() != null ? row.getDiemD().toString() : "");
            tfMaQuyDoi.setText(row.getMaQuyDoi());
            tfPhanVi.setText(row.getPhanVi());
        } else {
            lblDialogTitle.setText("Thêm luật quy đổi mới");
        }
    }

    @FXML
    private void onDialogSave() {
        // Validation cơ bản
        if (tfMaQuyDoi.getText().trim().isEmpty() || tfPhuongThuc.getText().trim().isEmpty()) {
            lblError.setText("Mã quy đổi và Phương thức không được để trống.");
            return;
        }

        try {
            // Chuẩn bị dữ liệu từ Form
            BigDecimal dA = new BigDecimal(tfDiemA.getText().trim());
            BigDecimal dB = new BigDecimal(tfDiemB.getText().trim());
            BigDecimal dC = new BigDecimal(tfDiemC.getText().trim());
            BigDecimal dD = new BigDecimal(tfDiemD.getText().trim());

            if (editingRow == null) {
                // Trường hợp THÊM MỚI
                BangQuyDoiDTO newDTO = new BangQuyDoiDTO(
                        0,
                        tfPhuongThuc.getText().trim(),
                        tfToHop.getText().trim(),
                        tfMon.getText().trim(),
                        dA, dB, dC, dD,
                        tfMaQuyDoi.getText().trim(),
                        tfPhanVi.getText().trim()
                );

                String result = bangQuyDoiBUS.addBangQuyDoi(newDTO);
                if (result.startsWith("Lỗi:")) {
                    lblError.setText(result);
                    return;
                } else {
                    showInfo("Thành công", result);
                }
            } else {
                // Trường hợp CẬP NHẬT
                editingRow.setPhuongThuc(tfPhuongThuc.getText().trim());
                editingRow.setToHop(tfToHop.getText().trim());
                editingRow.setMon(tfMon.getText().trim());
                editingRow.setDiemA(dA);
                editingRow.setDiemB(dB);
                editingRow.setDiemC(dC);
                editingRow.setDiemD(dD);
                editingRow.setMaQuyDoi(tfMaQuyDoi.getText().trim());
                editingRow.setPhanVi(tfPhanVi.getText().trim());

                String result = bangQuyDoiBUS.updateBangQuyDoi(editingRow.getIdqd(), editingRow);
                if (result.startsWith("Lỗi:")) {
                    lblError.setText(result);
                    return;
                } else {
                    showInfo("Thành công", result);
                }
            }

            isSaved = true;
            dialogStage.close();

        } catch (NumberFormatException e) {
            lblError.setText("Lỗi: Các ô điểm A, B, C, D phải là số (Ví dụ: 10.00)");
        } catch (Exception e) {
            lblError.setText("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @FXML
    private void onDialogCancel() {
        dialogStage.close();
    }

    public boolean getIsSaved() {
        return isSaved;
    }
}