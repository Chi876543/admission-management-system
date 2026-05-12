package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.service.DiemCongXetTuyenBUS;
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
import java.util.ResourceBundle;

public class DiemCongXetTuyenController extends BaseController implements Initializable {

    private final DiemCongXetTuyenBUS bus = new DiemCongXetTuyenBUS();
    private final ObservableList<DiemCongXetTuyenDTO> masterData = FXCollections.observableArrayList();

    @FXML private TextField tfSearch;
    @FXML private TableView<DiemCongXetTuyenDTO> tblDiemCong;

    @FXML private TableColumn<DiemCongXetTuyenDTO, Integer> colId;
    @FXML private TableColumn<DiemCongXetTuyenDTO, String> colCccd, colNganh, colToHop, colPhuongThuc, colGhiChu;
    @FXML private TableColumn<DiemCongXetTuyenDTO, BigDecimal> colDiemCC, colDiemUT, colTong;
    @FXML private TableColumn<DiemCongXetTuyenDTO, Void> colAction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idDiemCong"));
        colCccd.setCellValueFactory(new PropertyValueFactory<>("tsCccd"));
        colNganh.setCellValueFactory(new PropertyValueFactory<>("maNganh"));
        colToHop.setCellValueFactory(new PropertyValueFactory<>("maToHop"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("phuongThuc"));
        colDiemCC.setCellValueFactory(new PropertyValueFactory<>("diemCC"));
        colDiemUT.setCellValueFactory(new PropertyValueFactory<>("diemUtxt"));
        colTong.setCellValueFactory(new PropertyValueFactory<>("diemTong"));
        colGhiChu.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                    () -> {
                        DiemCongXetTuyenDTO item = getTableRow().getItem();
                        if (item != null) openDialog(item);
                    },
                    () -> {
                        DiemCongXetTuyenDTO item = getTableRow().getItem();
                        if (item != null) onDelete(item);
                    }
            );
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });
        tblDiemCong.setItems(masterData);
    }

    private void loadData() {
        // Vì BUS chưa có hàm Search cụ thể, tạm thời lấy tất cả
        // Bạn có thể lọc masterData trên RAM dựa trên tfSearch giống file Demo nếu muốn
        masterData.setAll(bus.getAllDiemCongXetTuyen());
    }

    @FXML private void onSearch() { loadData(); }
    @FXML private void onAdd() { openDialog(null); }

    private void onDelete(DiemCongXetTuyenDTO row) {
        if (confirmDelete("Điểm cộng của thí sinh " + row.getTsCccd())) {
            String result = bus.deleteDiemCongXetTuyen(row.getIdDiemCong());
            if (result.contains("successfully")) {
                loadData();
                showInfo("Thành công", "Đã xóa bản ghi.");
            } else {
                showError(result);
            }
        }
    }

    private void openDialog(DiemCongXetTuyenDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admissionManagement/desktop/views/admin/diemcong-dialog.fxml"));
            Parent root = loader.load();
            DiemCongXetTuyenDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm điểm cộng" : "Sửa điểm cộng");
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