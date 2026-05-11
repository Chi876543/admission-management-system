package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class NguyenVongXetTuyenDialogController extends BaseController {

    private final NguyenVongXetTuyenBUS bus = new NguyenVongXetTuyenBUS();
    private Stage dialogStage;
    private NguyenVongXetTuyenDTO editingRow;
    private boolean isSaved = false;

    @FXML private Label lblDialogTitle, lblError;
    @FXML private TextField tfCccd, tfMaNganh, tfThuTu, tfDiemTHXT, tfDiemUTQD, tfDiemCong, tfDiemXT, tfNvKeys, tfToHop;
    @FXML private ComboBox<String> cbPhuongThuc, cbKetQua;

    @FXML
    public void initialize() {
        cbPhuongThuc.setItems(FXCollections.observableArrayList("DGNL", "THPT", "VSAT", "XetTuyenThang"));
        cbKetQua.setItems(FXCollections.observableArrayList("Đỗ", "Trượt", "Đang xét duyệt"));
    }

    public void setDialogData(Stage stage, NguyenVongXetTuyenDTO row) {
        this.dialogStage = stage;
        this.editingRow = row;

        if (row != null) {
            lblDialogTitle.setText("Sửa nguyện vọng ID: " + row.getIdNv());
            tfCccd.setText(row.getCccd());
            tfMaNganh.setText(row.getMaNganh());
            tfThuTu.setText(String.valueOf(row.getThuTu()));
            tfDiemTHXT.setText(row.getDiemThxt().toString());
            tfDiemUTQD.setText(row.getDiemUtqd().toString());
            tfDiemCong.setText(row.getDiemCong().toString());
            tfDiemXT.setText(row.getDiemXetTuyen().toString());
            tfNvKeys.setText(row.getNvKeys());
            tfToHop.setText(row.getThm());
            cbPhuongThuc.setValue(row.getPhuongThuc());
            cbKetQua.setValue(row.getKetQua());

            // Khóa CCCD không cho sửa nếu là chế độ Update để tránh nhầm lẫn dữ liệu thí sinh
            tfCccd.setEditable(false);
        } else {
            lblDialogTitle.setText("Thêm nguyện vọng mới");
        }
    }

    @FXML
    private void onDialogSave() {
        if (tfCccd.getText().trim().isEmpty() || tfMaNganh.getText().trim().isEmpty()) {
            lblError.setText("CCCD và Mã ngành là bắt buộc.");
            return;
        }

        try {
            NguyenVongXetTuyenDTO dto = new NguyenVongXetTuyenDTO();
            if (editingRow != null) dto.setIdNv(editingRow.getIdNv());

            dto.setCccd(tfCccd.getText().trim());
            dto.setMaNganh(tfMaNganh.getText().trim());
            dto.setThuTu(Integer.parseInt(tfThuTu.getText().trim()));
            dto.setDiemThxt(new BigDecimal(tfDiemTHXT.getText().trim()));
            dto.setDiemUtqd(new BigDecimal(tfDiemUTQD.getText().trim()));
            dto.setDiemCong(new BigDecimal(tfDiemCong.getText().trim()));
            dto.setDiemXetTuyen(new BigDecimal(tfDiemXT.getText().trim()));
            dto.setNvKeys(tfNvKeys.getText().trim());
            dto.setThm(tfToHop.getText().trim());
            dto.setPhuongThuc(cbPhuongThuc.getValue());
            dto.setKetQua(cbKetQua.getValue());

            String result = (editingRow == null) ? bus.addNguyenVongXetTuyen(dto) : bus.updateNguyenVongXetTuyen(dto.getIdNv(), dto);

            if (result.contains("successfully")) {
                showInfo("Thành công", result);
                isSaved = true;
                dialogStage.close();
            } else {
                lblError.setText(result);
            }
        } catch (NumberFormatException e) {
            lblError.setText("Lỗi: Thứ tự và Điểm phải là số.");
        }
    }

    @FXML private void onDialogCancel() { dialogStage.close(); }
    public boolean getIsSaved() { return isSaved; }
}