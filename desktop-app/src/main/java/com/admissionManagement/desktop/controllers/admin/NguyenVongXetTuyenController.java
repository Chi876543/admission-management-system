package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NguyenVongXetTuyenController extends BaseController implements Initializable {

    private final NguyenVongXetTuyenBUS bus = new NguyenVongXetTuyenBUS();
    private final ObservableList<NguyenVongXetTuyenDTO> masterData = FXCollections.observableArrayList();

    @FXML private TextField tfSearch; // Tìm theo CCCD hoặc Mã nguyện vọng
    @FXML private TableView<NguyenVongXetTuyenDTO> tblNguyenVong;

    @FXML private TableColumn<NguyenVongXetTuyenDTO, Integer> colId, colThuTu;
    @FXML private TableColumn<NguyenVongXetTuyenDTO, String> colCccd, colMaNganh, colPhuongThuc, colToHop, colKetQua;
    @FXML private TableColumn<NguyenVongXetTuyenDTO, BigDecimal> colDiemTHXT, colDiemUTQD, colDiemCong, colDiemXT;
    @FXML private TableColumn<NguyenVongXetTuyenDTO, Void> colAction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idNv"));
        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colMaNganh.setCellValueFactory(new PropertyValueFactory<>("maNganh"));
        colThuTu.setCellValueFactory(new PropertyValueFactory<>("thuTu"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("phuongThuc"));
        colToHop.setCellValueFactory(new PropertyValueFactory<>("thm"));
        colDiemTHXT.setCellValueFactory(new PropertyValueFactory<>("diemThxt"));
        colDiemUTQD.setCellValueFactory(new PropertyValueFactory<>("diemUtqd"));
        colDiemCong.setCellValueFactory(new PropertyValueFactory<>("diemCong"));
        colDiemXT.setCellValueFactory(new PropertyValueFactory<>("diemXetTuyen"));
        colKetQua.setCellValueFactory(new PropertyValueFactory<>("ketQua"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                    () -> {
                        NguyenVongXetTuyenDTO item = getTableRow().getItem();
                        if (item != null) openDialog(item);
                    },
                    () -> {
                        NguyenVongXetTuyenDTO item = getTableRow().getItem();
                        if (item != null) onDelete(item);
                    }
            );
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });
        tblNguyenVong.setItems(masterData);
    }

    private void loadData() {
        String key = tfSearch.getText().trim();
        if (key.isEmpty()) {
            masterData.setAll(bus.getAllNganhToHop());
        } else {
            // Tận dụng hàm tìm kiếm theo CCCD có sẵn trong BUS của bạn
            masterData.setAll(bus.getByThiSinhCccd(key));
        }
    }

    @FXML private void onSearch() { loadData(); }
    @FXML private void onAdd() { openDialog(null); }

    private void onDelete(NguyenVongXetTuyenDTO row) {
        if (confirmDelete("Nguyện vọng ngành " + row.getMaNganh() + " của thí sinh " + row.getCccd())) {
            String result = bus.deleteNguyenVongXetTuyen(row.getIdNv());
            if (result.contains("successfully")) {
                loadData();
                showInfo("Thành công", "Đã xóa nguyện vọng.");
            } else {
                showError(result);
            }
        }
    }

    private void openDialog(NguyenVongXetTuyenDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admissionManagement/desktop/views/admin/nguyenvong-dialog.fxml"));
            Parent root = loader.load();
            NguyenVongXetTuyenDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm nguyện vọng" : "Sửa nguyện vọng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            dialogCtrl.setDialogData(stage, row);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) loadData();
        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }
}