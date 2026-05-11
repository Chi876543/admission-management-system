package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemThiXetTuyenDTO;
import com.admissionManagement.core.service.DiemThiXetTuyenBUS;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class DiemThiXetTuyenController extends BaseController implements Initializable {

    private final DiemThiXetTuyenBUS bus = new DiemThiXetTuyenBUS();
    private final ObservableList<DiemThiXetTuyenDTO> masterData = FXCollections.observableArrayList();

    @FXML private TextField tfSearch;
    @FXML private TableView<DiemThiXetTuyenDTO> tblDiemThi;

    // Các cột chính (Hiển thị tượng trưng các môn quan trọng trên bảng chính)
    @FXML private TableColumn<DiemThiXetTuyenDTO, String> colCccd, colSbd, colPhuongThuc;
    @FXML private TableColumn<DiemThiXetTuyenDTO, BigDecimal> colToan, colVan, colAnh, colNL1;
    @FXML private TableColumn<DiemThiXetTuyenDTO, Void> colAction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colSbd.setCellValueFactory(new PropertyValueFactory<>("soBaoDanh"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("phuongThuc"));
        colToan.setCellValueFactory(new PropertyValueFactory<>("diemToan"));
        colVan.setCellValueFactory(new PropertyValueFactory<>("diemVan"));
        colAnh.setCellValueFactory(new PropertyValueFactory<>("n1Thi"));
        colNL1.setCellValueFactory(new PropertyValueFactory<>("nl1"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                    () -> {
                        DiemThiXetTuyenDTO item = getTableRow().getItem();
                        if (item != null) openDialog(item);
                    },
                    () -> {
                        DiemThiXetTuyenDTO item = getTableRow().getItem();
                        if (item != null) onDelete(item);
                    }
            );
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });
        tblDiemThi.setItems(masterData);
    }

    private void loadData() {
        masterData.setAll(bus.getAllDiemThiXetTuyen());
    }

    @FXML private void onSearch() { loadData(); }
    @FXML private void onAdd() { openDialog(null); }

    // Tính năng Import CSV cho VSAT
    @FXML private void onImportVsat() {
        handleImport("VSAT");
    }

    // Tính năng Import CSV cho ĐGNL
    @FXML private void onImportDgnl() {
        handleImport("DGNL");
    }

    private void handleImport(String type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String msg = type.equals("VSAT") ? bus.importVsatCsvData(selectedFile) : bus.importDGNLCsvData(selectedFile);
            showInfo("Kết quả Import", msg);
            loadData();
        }
    }

    private void onDelete(DiemThiXetTuyenDTO row) {
        if (confirmDelete("Bảng điểm của thí sinh: " + row.getCccd())) {
            String result = bus.deleteDiemThiXetTuyen(row.getIdDiemThi());
            if (result.contains("successfully")) {
                loadData();
                showInfo("Thành công", "Đã xóa.");
            } else {
                showError(result);
            }
        }
    }

    private void openDialog(DiemThiXetTuyenDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admissionManagement/desktop/views/admin/diemthi-dialog.fxml"));
            Parent root = loader.load();
            DiemThiXetTuyenDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm điểm thi" : "Cập nhật điểm thi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            dialogCtrl.setDialogData(stage, row);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) loadData();
        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
            e.printStackTrace();
        }
    }
}