package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class NguyenVongXetTuyenDialogController extends BaseController {

    private NguyenVongXetTuyenBUS bus;
    private Stage dialogStage;
    private NguyenVongXetTuyenDTO editingRow;
    private boolean isSaved = false;

    @FXML private Label lblDialogTitle, lblError;
    @FXML private TextField tfCccd, tfMaNganh, tfThuTu, tfDiemTHXT,
            tfDiemUTQD, tfDiemCong, tfDiemXT, tfNvKeys, tfToHop;
    @FXML private ComboBox<String> cbPhuongThuc, cbKetQua;

    public void init(Stage stage, NguyenVongXetTuyenDTO row, NguyenVongXetTuyenBUS bus) {
        this.dialogStage = stage;
        this.editingRow  = row;
        this.bus         = bus;

        cbPhuongThuc.setItems(FXCollections.observableArrayList("DGNL", "THPT", "VSAT", "XetTuyenThang"));
        cbKetQua.setItems(FXCollections.observableArrayList("Trúng tuyển", "Trượt", "Đang xét duyệt"));

        if (row != null) {
            lblDialogTitle.setText("Sửa nguyện vọng ID: " + row.getIdNv());
            tfCccd.setText(row.getCccd());
            tfCccd.setEditable(false);
            tfMaNganh.setText(row.getMaNganh());
            tfThuTu.setText(String.valueOf(row.getThuTu()));
            tfDiemTHXT.setText(row.getDiemThxt()     != null ? row.getDiemThxt().toPlainString()     : "");
            tfDiemUTQD.setText(row.getDiemUtqd()     != null ? row.getDiemUtqd().toPlainString()     : "");
            tfDiemCong.setText(row.getDiemCong()     != null ? row.getDiemCong().toPlainString()     : "");
            tfDiemXT.setText(row.getDiemXetTuyen()   != null ? row.getDiemXetTuyen().toPlainString() : "");
            tfNvKeys.setText(row.getNvKeys());
            tfToHop.setText(row.getThm());
            cbPhuongThuc.setValue(row.getPhuongThuc());
            cbKetQua.setValue(row.getKetQua());
        } else {
            lblDialogTitle.setText("Thêm nguyện vọng mới");
        }
    }

    @FXML
    private void onDialogSave() {
        lblError.setText("");

        // --- Validate required ---
        if (tfCccd.getText().trim().isEmpty()) {
            lblError.setText("CCCD không được để trống.");
            return;
        }
        if (tfMaNganh.getText().trim().isEmpty()) {
            lblError.setText("Mã ngành không được để trống.");
            return;
        }
        if (tfToHop.getText().trim().isEmpty()) {
            lblError.setText("Tổ hợp không được để trống.");
            return;
        }
        if (tfNvKeys.getText().trim().isEmpty()) {
            lblError.setText("NV Keys không được để trống.");
            return;
        }

        // --- Parse thứ tự (bắt buộc) ---
        Integer thuTu = parseInt(tfThuTu, "Thứ tự");
        if (thuTu == null) return;

        // --- Parse điểm (không bắt buộc, để trống = null) ---
        BigDecimal diemTHXT = parseBigDecimalNullable(tfDiemTHXT, "Điểm THXT");
        if (lblError.getText() != null && !lblError.getText().isEmpty()) return;

        BigDecimal diemUTQD = parseBigDecimalNullable(tfDiemUTQD, "Điểm UTQD");
        if (lblError.getText() != null && !lblError.getText().isEmpty()) return;

        BigDecimal diemCong = parseBigDecimalNullable(tfDiemCong, "Điểm cộng");
        if (lblError.getText() != null && !lblError.getText().isEmpty()) return;

        BigDecimal diemXT = parseBigDecimalNullable(tfDiemXT, "Điểm xét tuyển");
        if (lblError.getText() != null && !lblError.getText().isEmpty()) return;

        // --- Gọi BUS ---
        NguyenVongXetTuyenDTO dto = new NguyenVongXetTuyenDTO(
                editingRow != null ? editingRow.getIdNv() : 0,
                tfCccd.getText().trim(),
                tfMaNganh.getText().trim(),
                thuTu,
                diemTHXT,
                diemUTQD,
                diemCong,
                diemXT,
                cbKetQua.getValue(),
                tfNvKeys.getText().trim(),
                cbPhuongThuc.getValue(),
                tfToHop.getText().trim()
        );

        String result = (editingRow == null)
                ? bus.addNguyenVongXetTuyen(dto)
                : bus.updateNguyenVongXetTuyen(dto.getIdNv(), dto);

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

    // --- Helpers ---
    private Integer parseInt(TextField tf, String fieldName) {
        try {
            return Integer.parseInt(tf.getText().trim());
        } catch (NumberFormatException e) {
            lblError.setText(fieldName + " phải là số nguyên.");
            return null;
        }
    }

    // Bắt buộc nhập, không được để trống
    private BigDecimal parseBigDecimal(TextField tf, String fieldName) {
        try {
            if (tf.getText().trim().isEmpty()) {
                lblError.setText(fieldName + " không được để trống.");
                return null;
            }
            return new BigDecimal(tf.getText().trim());
        } catch (NumberFormatException e) {
            lblError.setText(fieldName + " phải là số hợp lệ (VD: 10.00).");
            return null;
        }
    }

    // Không bắt buộc, để trống thì trả về null
    private BigDecimal parseBigDecimalNullable(TextField tf, String fieldName) {
        String val = tf.getText().trim();
        if (val.isEmpty()) return null;
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            lblError.setText(fieldName + " phải là số hợp lệ (VD: 10.00).");
            return null;
        }
    }
}