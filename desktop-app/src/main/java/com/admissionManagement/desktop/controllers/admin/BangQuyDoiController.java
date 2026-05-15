package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.service.BangQuyDoiBUS;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BangQuyDoiController extends BaseController implements Initializable {

    private final BangQuyDoiBUS bangQuyDoiBUS = new BangQuyDoiBUS();
    private final ObservableList<BangQuyDoiDTO> pageData = FXCollections.observableArrayList();
    private PauseTransition searchDebounce;
    private long totalRecords = 0;

    @FXML private TextField tfSearch;
    @FXML private TableView<BangQuyDoiDTO> tblBangQuyDoi;
    @FXML private TableColumn<BangQuyDoiDTO, Integer>    colId;
    @FXML private TableColumn<BangQuyDoiDTO, String>     colPhuongThuc, colToHop, colMon;
    @FXML private TableColumn<BangQuyDoiDTO, BigDecimal> colDiemA, colDiemB, colDiemC, colDiemD;
    @FXML private Label lblCount;
    @FXML private Pagination pagination;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();

        searchDebounce = new PauseTransition(Duration.millis(400));
        searchDebounce.setOnFinished(e -> loadData(0));
        tfSearch.textProperty().addListener((obs, old, val) -> searchDebounce.playFromStart());

        if (pagination != null) {
            pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) ->
                    loadData(newVal.intValue())
            );
        }

        loadData(0);
    }

    private void setupTable() {
        tblBangQuyDoi.setItems(pageData);
        colId.setCellValueFactory(new PropertyValueFactory<>("idqd"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("phuongThuc"));
        colToHop.setCellValueFactory(new PropertyValueFactory<>("toHop"));
        colMon.setCellValueFactory(new PropertyValueFactory<>("mon"));
        colDiemA.setCellValueFactory(new PropertyValueFactory<>("diemA"));
        colDiemB.setCellValueFactory(new PropertyValueFactory<>("diemB"));
        colDiemC.setCellValueFactory(new PropertyValueFactory<>("diemC"));
        colDiemD.setCellValueFactory(new PropertyValueFactory<>("diemD"));
    }

    private void loadData(int pageIndex) {
        String keyword = tfSearch != null ? tfSearch.getText().trim() : "";

        Task<List<BangQuyDoiDTO>> task = new Task<>() {
            @Override
            protected List<BangQuyDoiDTO> call() {
                if (pageIndex == 0) {
                    totalRecords = bangQuyDoiBUS.getTotal();
                }
                return bangQuyDoiBUS.getAllBangQuyDoi(keyword, pageIndex, PAGE_SIZE);
            }
        };

        task.setOnSucceeded(e -> {
            pageData.setAll(task.getValue());

            int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / PAGE_SIZE));
            if (pagination != null) {
                pagination.setPageCount(totalPages);
                if (pagination.getCurrentPageIndex() != pageIndex)
                    pagination.setCurrentPageIndex(pageIndex);
            }
            if (lblCount != null)
                lblCount.setText(totalRecords + " bản ghi (trang " + (pageIndex + 1) + "/" + totalPages + ")");
        });

        task.setOnFailed(e -> showError("Lỗi tải dữ liệu: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void onAdd() { openDialog(null); }

    @FXML
    private void onEdit() {
        BangQuyDoiDTO selected = tblBangQuyDoi.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn dòng cần sửa."); return; }
        openDialog(selected);
    }

    @FXML
    private void onDeleteSelected() {
        BangQuyDoiDTO selected = tblBangQuyDoi.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn dòng cần xóa."); return; }
        if (confirmDelete("Luật quy đổi ID: " + selected.getIdqd())) {
            String result = bangQuyDoiBUS.deleteBangQuyDoi(selected.getIdqd());
            if (!result.startsWith("Lỗi")) {
                showInfo("Thành công", "Đã xóa bản ghi.");
                int cur = pagination != null ? pagination.getCurrentPageIndex() : 0;
                loadData(cur);
            } else {
                showError(result);
            }
        }
    }

    @FXML
    private void onImport() {
        File file = chooseCSV((Stage) tblBangQuyDoi.getScene().getWindow());
        if (file != null) {
            Task<String> importTask = new Task<>() {
                @Override protected String call() { return bangQuyDoiBUS.importCsv(file); }
            };
            importTask.setOnSucceeded(e -> {
                showInfo("Kết quả Import", importTask.getValue());
                loadData(0);
            });
            importTask.setOnFailed(e -> showError(importTask.getException().getMessage()));
            new Thread(importTask).start();
        }
    }

    private void openDialog(BangQuyDoiDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/admissionManagement/desktop/views/admin/BangQuyDoiDialogUI.fxml"));
            Parent root = loader.load();
            BangQuyDoiDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm luật quy đổi" : "Sửa luật quy đổi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            dialogCtrl.init(stage, row, bangQuyDoiBUS);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) {
                int cur = pagination != null ? pagination.getCurrentPageIndex() : 0;
                loadData(cur);
            }
        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }
}
