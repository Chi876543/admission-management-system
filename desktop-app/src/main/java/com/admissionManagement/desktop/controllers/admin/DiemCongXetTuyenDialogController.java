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
                "Toán", "Văn", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Tin học"
        ));

        cbPhuongThuc.setItems(FXCollections.observableArrayList(
                "THPT", "DGNL", "VSAT"
        ));

        cbLoaiGiai.setItems(FXCollections.observableArrayList(
                "Nhất", "Nhì", "Ba", "Khuyến khích"
        ));

        cbCap.setItems(FXCollections.observableArrayList(
                "Quốc tế", "Quốc gia", "Tỉnh", "Thành phố"
        ));

        // Tự động tạo ghi chú khi cbLoaiGiai hoặc cbCap thay đổi
        cbLoaiGiai.valueProperty().addListener((obs, o, n) -> autoFillGhiChu());
        cbCap.valueProperty().addListener((obs, o, n) -> autoFillGhiChu());

        if (row != null) {
            lblDialogTitle.setText("Sửa điểm cộng ID: " + row.getIdDiemCong());
            tfCccd.setText(row.getTsCccd());
            cbMon.setValue(row.getMon());
            cbPhuongThuc.setValue(row.getPhuongThuc());

            // Parse ghi chú dạng "Giải Nhì - Cấp Tỉnh" để set combobox
            parseGhiChuToComboBox(row.getGhiChu());

            tfGhiChu.setText(row.getGhiChu() != null ? row.getGhiChu() : "");

        } else {
            lblDialogTitle.setText("Thêm điểm cộng mới");
        }
    }

    /**
     * Parse ghi chú dạng "Giải Nhì - Cấp Tỉnh" hoặc "Nhì - Tỉnh cấp ..."
     * Nếu không khớp thì bỏ qua (thoi).
     */
    private void parseGhiChuToComboBox(String ghiChu) {
        if (ghiChu == null || ghiChu.isBlank()) return;

        // Tìm loại giải: Nhất / Nhì / Ba / Khuyến khích
        String[] loaiGiaiOptions = {"Nhất", "Nhì", "Ba", "Khuyến khích"};
        for (String loai : loaiGiaiOptions) {
            if (ghiChu.contains(loai)) {
                cbLoaiGiai.setValue(loai);
                break;
            }
        }

        // Tìm cấp: Quốc tế / Quốc gia / Tỉnh / Thành phố
        String[] capOptions = {"Quốc tế", "Quốc gia", "Thành phố", "Tỉnh"};
        for (String cap : capOptions) {
            if (ghiChu.contains(cap)) {
                cbCap.setValue(cap);
                break;
            }
        }
    }

    /** Tự động điền ghi chú theo format "Giải Nhì - Cấp Tỉnh" */
    private void autoFillGhiChu() {
        String loai = cbLoaiGiai.getValue();
        String cap  = cbCap.getValue();
        if (loai != null && cap != null) {
            tfGhiChu.setText("Giải " + loai + " - Cấp " + cap);
        }
    }

    @FXML
    private void onDialogSave() {

        lblError.setText("");
        lblError.setStyle("-fx-text-fill: red;");

        if (tfCccd.getText().trim().isEmpty()) {
            lblError.setText("CCCD không được để trống.");
            return;
        }

        if (cbMon.getValue() == null) {
            lblError.setText("Vui lòng chọn môn.");
            return;
        }

        if (cbPhuongThuc.getValue() == null) {
            lblError.setText("Vui lòng chọn phương thức.");
            return;
        }

        if (cbLoaiGiai.getValue() == null) {
            lblError.setText("Vui lòng chọn loại giải.");
            return;
        }

        if (cbCap.getValue() == null) {
            lblError.setText("Vui lòng chọn cấp.");
            return;
        }

        BigDecimal[] diem = DatabaseHelper.tinhDiemUtxt(
                cbCap.getValue().toLowerCase(),
                cbLoaiGiai.getValue().toLowerCase(),
                cbPhuongThuc.getValue()
        );

        BigDecimal diemToHop      = diem[0];
        BigDecimal diemKhongToHop = diem[1];

        // Tự tạo ghi chú nếu trống
        String ghiChu = tfGhiChu.getText().trim();
        if (ghiChu.isEmpty()) {
            ghiChu = "Giải " + cbLoaiGiai.getValue() + " - Cấp " + cbCap.getValue();
        }

        DiemCongXetTuyenDTO dto = new DiemCongXetTuyenDTO(
                editingRow != null ? editingRow.getIdDiemCong() : 0,
                tfCccd.getText().trim(),
                cbMon.getValue(),
                cbPhuongThuc.getValue(),
                diemToHop,
                diemKhongToHop,
                BigDecimal.ZERO,
                diemToHop,
                diemKhongToHop,
                ghiChu
        );

        if (editingRow == null) {
            // Dùng addAndReturn để lấy ID thật từ DB
            DiemCongXetTuyenDTO returned = bus.addAndReturn(dto);
            if (returned == null) {
                lblError.setText("Lỗi: Không thể thêm bản ghi (kiểm tra CCCD và dữ liệu).");
                return;
            }
            savedData = returned;
        } else {
            String result = bus.updateDiemCongXetTuyen(dto.getIdDiemCong(), dto);
            if (result.startsWith("Lỗi")) {
                lblError.setText(result);
                return;
            }
            savedData = dto;
            editingRow.setMon(dto.getMon());
            editingRow.setPhuongThuc(dto.getPhuongThuc());
            editingRow.setDiemUtxtToHop(dto.getDiemUtxtToHop());
            editingRow.setDiemUtxtKhongXetToHop(dto.getDiemUtxtKhongXetToHop());
            editingRow.setDiemTongThxt(dto.getDiemTongThxt());
            editingRow.setDiemTongKhongXetThxt(dto.getDiemTongKhongXetThxt());
            editingRow.setGhiChu(dto.getGhiChu());
        }

        showInfo("Thành công", editingRow == null ? "Đã thêm bản ghi." : "Đã cập nhật bản ghi.");
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
