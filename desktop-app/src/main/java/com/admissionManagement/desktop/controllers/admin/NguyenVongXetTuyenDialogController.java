package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class NguyenVongXetTuyenDialogController extends BaseController {

    private NguyenVongXetTuyenBUS bus;
    private Stage dialogStage;
    private NguyenVongXetTuyenDTO editingRow;
    private boolean isSaved = false;
    private ObservableList<NguyenVongXetTuyenDTO> allData;

    @FXML private Label lblDialogTitle, lblError;
    @FXML private TextField tfCccd, tfThuTu;
    @FXML private ComboBox<String> cbMaNganh;

    public void init(
            Stage stage,
            NguyenVongXetTuyenDTO row,
            NguyenVongXetTuyenBUS bus,
            ObservableList<NguyenVongXetTuyenDTO> allData
    ) {
        this.dialogStage = stage;
        this.editingRow = row;
        this.bus = bus;
        this.allData = allData;

        setupComboBox();

        if (row != null) {
            lblDialogTitle.setText("Sửa nguyện vọng ID: " + row.getIdNv());
            tfCccd.setText(row.getCccd());
            tfCccd.setDisable(true);
            cbMaNganh.setValue(row.getMaNganh());
            tfThuTu.setText(String.valueOf(row.getThuTu()));
        } else {
            lblDialogTitle.setText("Thêm nguyện vọng mới");
            tfCccd.setDisable(false);
        }
    }

    private void setupComboBox() {
        cbMaNganh.setItems(FXCollections.observableArrayList(
                "7480201", "7480101", "7480107", "7340101", "7420201"
        ));
    }

    @FXML
    private void onDialogSave() {
        lblError.setText("");

        String cccd = editingRow != null
                ? editingRow.getCccd()
                : tfCccd.getText().trim();

        String maNganh = cbMaNganh.getValue();

        if (cccd.isEmpty()) { lblError.setText("CCCD không được để trống."); return; }
        if (maNganh == null) { lblError.setText("Vui lòng chọn ngành."); return; }

        int thuTu;
        try {
            thuTu = Integer.parseInt(tfThuTu.getText().trim());
        } catch (NumberFormatException e) {
            lblError.setText("Thứ tự NV phải là số.");
            return;
        }

        final String cccdFinal = cccd;
        final int thuTuFinal   = thuTu;

        // Kiểm tra trùng thứ tự NV cùng CCCD
        boolean isDuplicate = allData.stream().anyMatch(item ->
                item.getCccd().equalsIgnoreCase(cccdFinal)
                        && item.getThuTu() == thuTuFinal
                        && (editingRow == null || item.getIdNv() != editingRow.getIdNv())
        );
        if (isDuplicate) {
            lblError.setText("Thứ tự nguyện vọng đã tồn tại cho CCCD này.");
            return;
        }

        String result;

        if (editingRow == null) {
            result = bus.addNguyenVong(cccd, maNganh, thuTu);
        } else {
            String oldMaNganh = editingRow.getMaNganh();
            int    oldThuTu   = editingRow.getThuTu();

            String deleteResult = bus.deleteNguyenVongXetTuyen(editingRow.getIdNv());
            if (deleteResult.startsWith("Lỗi")) {
                lblError.setText("Lỗi khi xóa bản ghi cũ: " + deleteResult);
                return;
            }

            // Xóa các bản ghi cũ cùng cccd+maNganh+thuTu khỏi allData
            allData.removeIf(item ->
                    item.getCccd().equalsIgnoreCase(cccdFinal)
                            && item.getMaNganh().equals(oldMaNganh)
                            && item.getThuTu() == oldThuTu
            );

            result = bus.addNguyenVong(cccd, maNganh, thuTu);
        }

        if (result.startsWith("Lỗi")) {
            lblError.setText(result);
            return;
        }

        // Chỉ query NV của đúng thí sinh này (nhẹ hơn getAll)
        List<NguyenVongXetTuyenDTO> nvCuaThiSinh = bus.getByThiSinhCccd(cccdFinal);

        // Thêm các bản ghi mới (đúng maNganh + thuTu) vào đầu allData
        nvCuaThiSinh.stream()
                .filter(nv -> nv.getMaNganh().equals(maNganh) && nv.getThuTu() == thuTuFinal)
                .forEach(nv -> allData.add(0, nv));

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
}