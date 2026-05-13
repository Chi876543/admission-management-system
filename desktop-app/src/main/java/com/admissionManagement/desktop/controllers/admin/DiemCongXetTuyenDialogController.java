package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.service.DiemCongXetTuyenBUS;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class DiemCongXetTuyenDialogController extends BaseController {

    private DiemCongXetTuyenBUS bus;
    private Stage dialogStage;
    private DiemCongXetTuyenDTO editingRow;
    private boolean isSaved = false;

    @FXML private Label lblDialogTitle, lblError;
    @FXML private TextField tfCccd, tfMon, tfPhuongThuc,
            tfDiemUtxtToHop, tfDiemUtxtKhongToHop,
            tfDiemCc, tfTongThxt, tfTongKhongThxt, tfGhiChu;

    public void init(Stage stage, DiemCongXetTuyenDTO row, DiemCongXetTuyenBUS bus) {
        this.dialogStage = stage;
        this.editingRow  = row;
        this.bus         = bus;

        if (row != null) {
            lblDialogTitle.setText("Sửa điểm cộng ID: " + row.getIdDiemCong());
            tfCccd.setText(row.getTsCccd());
            tfCccd.setEditable(false);
            tfMon.setText(row.getMon() != null ? row.getMon() : "");
            tfPhuongThuc.setText(row.getPhuongThuc() != null ? row.getPhuongThuc() : "");
            tfDiemUtxtToHop.setText(val(row.getDiemUtxtToHop()));
            tfDiemUtxtKhongToHop.setText(val(row.getDiemUtxtKhongXetToHop()));
            tfDiemCc.setText(val(row.getDiemCc()));
            tfTongThxt.setText(val(row.getDiemTongThxt()));
            tfTongKhongThxt.setText(val(row.getDiemTongKhongXetThxt()));
            tfGhiChu.setText(row.getGhiChu() != null ? row.getGhiChu() : "");
        } else {
            lblDialogTitle.setText("Thêm điểm cộng mới");
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
        if (tfPhuongThuc.getText().trim().isEmpty()) {
            lblError.setText("Phương thức không được để trống.");
            return;
        }

        // --- Parse từng field điểm ---
        BigDecimal diemUtxtToHop = parseBigDecimal(tfDiemUtxtToHop, "Điểm UTXT (có tổ hợp)");
        if (lblError.getText() != null && !lblError.getText().isEmpty()) return;

        BigDecimal diemUtxtKhongToHop = parseBigDecimal(tfDiemUtxtKhongToHop, "Điểm UTXT (không tổ hợp)");
        if (lblError.getText() != null && !lblError.getText().isEmpty()) return;

        BigDecimal diemCc = parseBigDecimal(tfDiemCc, "Điểm chứng chỉ");
        if (lblError.getText() != null && !lblError.getText().isEmpty()) return;

        BigDecimal tongThxt = parseBigDecimal(tfTongThxt, "Tổng THXT");
        if (lblError.getText() != null && !lblError.getText().isEmpty()) return;

        BigDecimal tongKhongThxt = parseBigDecimal(tfTongKhongThxt, "Tổng không xét THXT");
        if (lblError.getText() != null && !lblError.getText().isEmpty()) return;

        // --- Gọi BUS ---
        DiemCongXetTuyenDTO dto = new DiemCongXetTuyenDTO(
                editingRow != null ? editingRow.getIdDiemCong() : 0,
                tfCccd.getText().trim(),
                tfMon.getText().trim(),
                tfPhuongThuc.getText().trim(),
                diemUtxtToHop,
                diemUtxtKhongToHop,
                diemCc,
                tongThxt,
                tongKhongThxt,
                tfGhiChu.getText().trim()
        );

        String result = (editingRow == null)
                ? bus.addDiemCongXetTuyen(dto)
                : bus.updateDiemCongXetTuyen(dto.getIdDiemCong(), dto);

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
    private String val(BigDecimal d) {
        return d == null ? "" : d.toPlainString();
    }

    private BigDecimal parseBigDecimal(TextField tf, String fieldName) {
        String v = tf.getText().trim();
        if (v.isEmpty()) return null;
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException e) {
            lblError.setText(fieldName + " phải là số hợp lệ (VD: 1.50).");
            return null;
        }
    }
}