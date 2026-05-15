package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.service.NganhBUS;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class NguyenVongXetTuyenDialogController extends BaseController {

    private NguyenVongXetTuyenBUS bus;
    private Stage dialogStage;
    private NguyenVongXetTuyenDTO editingRow;
    private boolean isSaved = false;
    private ObservableList<NguyenVongXetTuyenDTO> allData;

    private final NganhBUS nganhBUS = new NganhBUS();

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
        this.editingRow  = row;
        this.bus         = bus;
        this.allData     = allData;

        // Load danh sách ngành từ DB (luôn mới nhất)
        loadDanhSachNganh();

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

    /** Load danh sách mã ngành từ DB thay vì hardcode */
    private void loadDanhSachNganh() {
        try {
            List<NganhDTO> danhSachNganh = nganhBUS.getAllNganh(0, 0);
            List<String> maNganhList = danhSachNganh.stream()
                    .map(NganhDTO::getMaNganh)
                    .sorted()
                    .collect(Collectors.toList());
            cbMaNganh.setItems(FXCollections.observableArrayList(maNganhList));
        } catch (Exception e) {
            // Fallback danh sách cứng nếu DB lỗi
            cbMaNganh.setItems(FXCollections.observableArrayList(
                    "7480201", "7480101", "7480107", "7340101", "7420201"
            ));
            showError("Cảnh báo: Không tải được danh sách ngành từ DB, dùng danh sách mặc định.");
        }
    }

    @FXML
    private void onDialogSave() {
        lblError.setText("");
        lblError.setStyle("-fx-text-fill: red;");

        String cccd = editingRow != null
                ? editingRow.getCccd()
                : tfCccd.getText().trim();

        String maNganh = cbMaNganh.getValue();

        if (cccd.isEmpty()) {
            lblError.setText("CCCD không được để trống.");
            return;
        }
        if (maNganh == null) {
            lblError.setText("Vui lòng chọn ngành.");
            return;
        }

        int thuTu;
        try {
            thuTu = Integer.parseInt(tfThuTu.getText().trim());
            if (thuTu < 1) {
                lblError.setText("Thứ tự nguyện vọng phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            lblError.setText("Thứ tự NV phải là số nguyên dương.");
            return;
        }

        final String cccdFinal  = cccd;
        final int    thuTuFinal = thuTu;

        // Kiểm tra trùng thứ tự NV cùng CCCD
        boolean isDuplicate = allData.stream().anyMatch(item ->
                item.getCccd().equalsIgnoreCase(cccdFinal)
                        && item.getThuTu() == thuTuFinal
                        && (editingRow == null || item.getIdNv() != editingRow.getIdNv())
        );
        if (isDuplicate) {
            lblError.setText("Thứ tự nguyện vọng " + thuTu + " đã tồn tại cho CCCD này.");
            return;
        }

        // Kiểm tra trùng ngành cùng CCCD
        final String maNganhFinal = maNganh;
        boolean isDuplicateNganh = allData.stream().anyMatch(item ->
                item.getCccd().equalsIgnoreCase(cccdFinal)
                        && item.getMaNganh().equals(maNganhFinal)
                        && (editingRow == null || item.getIdNv() != editingRow.getIdNv())
        );
        if (isDuplicateNganh) {
            lblError.setText("Ngành " + maNganh + " đã tồn tại trong danh sách nguyện vọng của thí sinh này.");
            return;
        }

        String result;

        if (editingRow == null) {
            // THÊM MỚI
            result = bus.addNguyenVong(cccd, maNganh, thuTu);
            if (result.startsWith("Lỗi")) {
                lblError.setText(result);
                return;
            }
            // Tải bản ghi vừa thêm vào allData
            List<NguyenVongXetTuyenDTO> nvCuaThiSinh = bus.getByThiSinhCccd(cccdFinal);
            nvCuaThiSinh.stream()
                    .filter(nv -> nv.getMaNganh().equals(maNganh) && nv.getThuTu() == thuTuFinal)
                    .forEach(nv -> allData.add(0, nv));
            showInfo("Thành công", "Thêm nguyện vọng thành công.");
        } else {
            // SỬA: kiểm tra xem maNganh có thay đổi không
            String oldMaNganh = editingRow.getMaNganh();

            if (!oldMaNganh.equals(maNganh)) {
                // Ngành đổi → xóa các NV cũ của ngành cũ (theo idNv cụ thể),
                // sau đó gọi addNguyenVong để tạo NV với ngành mới.
                // Không xóa theo idNv duy nhất vì một lần addNguyenVong
                // tạo nhiều dòng (PT2/PT3/PT4) — xóa đúng nhóm cùng cccd+oldMaNganh.
                List<NguyenVongXetTuyenDTO> nvCuThiSinh = bus.getByThiSinhCccd(cccdFinal);
                for (NguyenVongXetTuyenDTO nv : nvCuThiSinh) {
                    if (nv.getMaNganh().equals(oldMaNganh)) {
                        bus.deleteNguyenVongXetTuyen(nv.getIdNv());
                        allData.removeIf(item -> item.getIdNv() == nv.getIdNv());
                    }
                }
            }
            // Nếu ngành không đổi: saveOrUpdateNV() trong addNguyenVong sẽ tự
            // UPDATE dòng cũ theo nvKeys (cccd_maNganh_phuongThuc) → idNv giữ nguyên.

            result = bus.addNguyenVong(cccd, maNganh, thuTu);
            if (result.startsWith("Lỗi")) {
                lblError.setText(result);
                return;
            }

            // Reload các dòng thuộc ngành mới vào allData
            List<NguyenVongXetTuyenDTO> nvCuaThiSinh = bus.getByThiSinhCccd(cccdFinal);
            nvCuaThiSinh.stream()
                    .filter(nv -> nv.getMaNganh().equals(maNganhFinal) && nv.getThuTu() == thuTuFinal)
                    .forEach(nv -> {
                        // Nếu đã tồn tại trong allData (update) thì replace, chưa có thì add
                        boolean exists = allData.stream().anyMatch(item -> item.getIdNv() == nv.getIdNv());
                        if (exists) {
                            for (int i = 0; i < allData.size(); i++) {
                                if (allData.get(i).getIdNv() == nv.getIdNv()) {
                                    allData.set(i, nv);
                                    break;
                                }
                            }
                        } else {
                            allData.add(0, nv);
                        }
                    });

            showInfo("Thành công", "Sửa nguyện vọng thành công.");
        }

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