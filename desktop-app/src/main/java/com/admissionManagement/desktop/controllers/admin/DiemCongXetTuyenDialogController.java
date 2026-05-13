package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.helper.DatabaseHelper;
import com.admissionManagement.core.service.DiemCongXetTuyenBUS;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class DiemCongXetTuyenDialogController extends BaseController {

    private DiemCongXetTuyenBUS bus;
    private Stage dialogStage;
    private DiemCongXetTuyenDTO editingRow;
    private boolean isSaved = false;

    private DiemCongXetTuyenDTO savedData;

    @FXML private Label lblDialogTitle;
    @FXML private Label lblError;

    @FXML private TextField tfCccd;

    @FXML private ComboBox<String>
            cbMon,
            cbPhuongThuc,
            cbLoaiGiai,
            cbCap;

    @FXML private TextField tfGhiChu;

    public void init(
            Stage stage,
            DiemCongXetTuyenDTO row,
            DiemCongXetTuyenBUS bus
    ) {

        this.dialogStage = stage;
        this.editingRow = row;
        this.bus = bus;

        cbMon.setItems(FXCollections.observableArrayList(
                "Toán",
                "Văn",
                "Lý",
                "Hóa",
                "Sinh",
                "Sử",
                "Địa",
                "Tin học"
        ));

        cbPhuongThuc.setItems(FXCollections.observableArrayList(
                "THPT",
                "DGNL",
                "VSAT"
        ));

        cbLoaiGiai.setItems(FXCollections.observableArrayList(
                "Nhất",
                "Nhì",
                "Ba",
                "Khuyến khích"
        ));

        cbCap.setItems(FXCollections.observableArrayList(
                "Quốc tế",
                "Quốc gia",
                "Tỉnh",
                "Thành phố"
        ));

        if (row != null) {

            lblDialogTitle.setText(
                    "Sửa điểm cộng ID: "
                            + row.getIdDiemCong()
            );

            tfCccd.setText(row.getTsCccd());

            cbMon.setValue(row.getMon());

            cbPhuongThuc.setValue(row.getPhuongThuc());

            tfGhiChu.setText(
                    row.getGhiChu() != null
                            ? row.getGhiChu()
                            : ""
            );

        } else {

            lblDialogTitle.setText(
                    "Thêm điểm cộng mới"
            );
        }
    }

    @FXML
    private void onDialogSave() {

        lblError.setText("");

        if (tfCccd.getText().trim().isEmpty()) {

            lblError.setText(
                    "CCCD không được để trống."
            );

            return;
        }

        if (cbMon.getValue() == null) {

            lblError.setText(
                    "Vui lòng chọn môn."
            );

            return;
        }

        if (cbPhuongThuc.getValue() == null) {

            lblError.setText(
                    "Vui lòng chọn phương thức."
            );

            return;
        }

        if (cbLoaiGiai.getValue() == null) {

            lblError.setText(
                    "Vui lòng chọn loại giải."
            );

            return;
        }

        if (cbCap.getValue() == null) {

            lblError.setText(
                    "Vui lòng chọn cấp."
            );

            return;
        }

        BigDecimal[] diem =
                DatabaseHelper.tinhDiemUtxt(
                        cbCap.getValue().toLowerCase(),
                        cbLoaiGiai.getValue().toLowerCase(),
                        cbPhuongThuc.getValue()
                );

        BigDecimal diemToHop = diem[0];
        BigDecimal diemKhongToHop = diem[1];

        DiemCongXetTuyenDTO dto =
                new DiemCongXetTuyenDTO(
                        editingRow != null
                                ? editingRow.getIdDiemCong()
                                : 0,

                        tfCccd.getText().trim(),

                        cbMon.getValue(),

                        cbPhuongThuc.getValue(),

                        diemToHop,

                        diemKhongToHop,

                        BigDecimal.ZERO,

                        diemToHop,

                        diemKhongToHop,

                        tfGhiChu.getText().trim()
                );

        String result =
                editingRow == null
                        ? bus.addDiemCongXetTuyen(dto)
                        : bus.updateDiemCongXetTuyen(
                        dto.getIdDiemCong(),
                        dto
                );

        if (result.startsWith("Lỗi")) {

            lblError.setText(result);
            return;
        }

        savedData = dto;

        if (editingRow != null) {

            editingRow.setMon(dto.getMon());
            editingRow.setPhuongThuc(dto.getPhuongThuc());
            editingRow.setDiemUtxtToHop(dto.getDiemUtxtToHop());
            editingRow.setDiemUtxtKhongXetToHop(dto.getDiemUtxtKhongXetToHop());
            editingRow.setDiemTongThxt(dto.getDiemTongThxt());
            editingRow.setDiemTongKhongXetThxt(dto.getDiemTongKhongXetThxt());
            editingRow.setGhiChu(dto.getGhiChu());
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

    public DiemCongXetTuyenDTO getSavedData() {

        return savedData;
    }
}