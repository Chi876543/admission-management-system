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

/**
 * Dialog thêm / sửa điểm chứng chỉ tiếng Anh (DiemCC).
 * Mon luôn là "N1", diemCc được tính từ mucDiem quy đổi.
 */
public class DiemCcDialogController extends BaseController {

    private DiemCongXetTuyenBUS bus;
    private Stage dialogStage;
    private DiemCongXetTuyenDTO editingRow;
    private boolean isSaved = false;
    private DiemCongXetTuyenDTO savedData;

    @FXML private Label lblDialogTitle;
    @FXML private Label lblError;
    @FXML private Label lblPreview;

    @FXML private TextField tfCccd;
    @FXML private ComboBox<String> cbPhuongThuc;
    @FXML private ComboBox<String> cbMucDiem;
    @FXML private TextField tfGhiChu;

    // Mức điểm hiển thị trong combobox (key = label, value = key cho hàm tính)
    private static final String[] MUC_DIEM_LABELS = {
            "IELTS 6.0 / TOEIC 700 (mức 8)",
            "IELTS 6.5 / TOEIC 800 (mức 9)",
            "IELTS 7.0+ / TOEIC 900+ (mức 10)"
    };
    private static final String[] MUC_DIEM_KEYS = { "8", "9", "10" };

    public void init(Stage stage, DiemCongXetTuyenDTO row, DiemCongXetTuyenBUS bus) {
        this.dialogStage = stage;
        this.editingRow  = row;
        this.bus         = bus;

        cbPhuongThuc.setItems(FXCollections.observableArrayList("THPT", "DGNL", "VSAT"));
        cbMucDiem.setItems(FXCollections.observableArrayList(MUC_DIEM_LABELS));

        // Cập nhật preview khi chọn
        cbMucDiem.valueProperty().addListener((obs, o, n) -> updatePreview());
        cbPhuongThuc.valueProperty().addListener((obs, o, n) -> updatePreview());

        if (row != null) {
            lblDialogTitle.setText("Sửa điểm CC ID: " + row.getIdDiemCong());
            tfCccd.setText(row.getTsCccd());
            tfCccd.setDisable(true);
            cbPhuongThuc.setValue(row.getPhuongThuc());
            tfGhiChu.setText(row.getGhiChu() != null ? row.getGhiChu() : "");

            // Cố gắng khôi phục mức điểm từ diemCc
            if (row.getDiemCc() != null) {
                restoreMucDiem(row.getDiemCc(), row.getPhuongThuc());
            }
        } else {
            lblDialogTitle.setText("Thêm điểm chứng chỉ tiếng Anh");
        }
    }

    /** Khôi phục combobox mức điểm từ giá trị diemCc + phuongThuc */
    private void restoreMucDiem(BigDecimal diemCc, String phuongThuc) {
        if (phuongThuc == null) return;
        for (int i = 0; i < MUC_DIEM_KEYS.length; i++) {
            BigDecimal cal = DatabaseHelper.tinhDiemChungChiTiengAnh(MUC_DIEM_KEYS[i], phuongThuc);
            if (cal.compareTo(diemCc) == 0) {
                cbMucDiem.setValue(MUC_DIEM_LABELS[i]);
                break;
            }
        }
    }

    private void updatePreview() {
        String mucLabel = cbMucDiem.getValue();
        String pt       = cbPhuongThuc.getValue();
        if (mucLabel == null || pt == null) {
            lblPreview.setText("");
            return;
        }
        String mucKey = getMucDiemKey(mucLabel);
        BigDecimal diem = DatabaseHelper.tinhDiemChungChiTiengAnh(mucKey, pt);
        lblPreview.setText("→ Điểm CC quy đổi: " + diem.toPlainString());
    }

    private String getMucDiemKey(String label) {
        for (int i = 0; i < MUC_DIEM_LABELS.length; i++) {
            if (MUC_DIEM_LABELS[i].equals(label)) return MUC_DIEM_KEYS[i];
        }
        return "8";
    }

    @FXML
    private void onDialogSave() {
        lblError.setText("");
        lblError.setStyle("-fx-text-fill: red;");

        String cccd = tfCccd.getText().trim();
        if (cccd.isEmpty()) {
            lblError.setText("CCCD không được để trống.");
            return;
        }
        if (cbPhuongThuc.getValue() == null) {
            lblError.setText("Vui lòng chọn phương thức.");
            return;
        }
        if (cbMucDiem.getValue() == null) {
            lblError.setText("Vui lòng chọn mức điểm chứng chỉ.");
            return;
        }

        String mucKey  = getMucDiemKey(cbMucDiem.getValue());
        String pt      = cbPhuongThuc.getValue();
        BigDecimal diemCc = DatabaseHelper.tinhDiemChungChiTiengAnh(mucKey, pt);

        String ghiChu = tfGhiChu.getText().trim();
        if (ghiChu.isEmpty()) ghiChu = cbMucDiem.getValue();

        DiemCongXetTuyenDTO dto = new DiemCongXetTuyenDTO(
                editingRow != null ? editingRow.getIdDiemCong() : 0,
                cccd,
                "N1",         // mon cố định
                pt,
                BigDecimal.ZERO,  // diemUtxtToHop
                BigDecimal.ZERO,  // diemUtxtKhongXetToHop
                diemCc,
                BigDecimal.ZERO,  // diemTongThxt
                diemCc,           // diemTongKhongXetThxt = diemCc
                ghiChu
        );

        if (editingRow == null) {
            DiemCongXetTuyenDTO returned = bus.addAndReturn(dto);
            if (returned == null) {
                lblError.setText("Lỗi: Không thể thêm bản ghi (kiểm tra CCCD).");
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
        }

        showInfo("Thành công", editingRow == null ? "Đã thêm điểm CC." : "Đã cập nhật điểm CC.");
        isSaved = true;
        dialogStage.close();
    }

    @FXML
    private void onDialogCancel() {
        dialogStage.close();
    }

    public boolean getIsSaved()           { return isSaved; }
    public DiemCongXetTuyenDTO getSavedData() { return savedData; }
}
