package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.service.DiemCongXetTuyenBUS;
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

public class DiemCongXetTuyenController extends BaseController implements Initializable {

    private static final int PAGE_SIZE = 20;

    private final DiemCongXetTuyenBUS bus = new DiemCongXetTuyenBUS();
    private final ObservableList<DiemCongXetTuyenDTO> pageData = FXCollections.observableArrayList();
    private PauseTransition searchDebounce;
    private long totalRecords = 0;

    @FXML private TextField tfSearch;
    @FXML private Label lblCount;
    @FXML private Pagination pagination;

    @FXML private TableView<DiemCongXetTuyenDTO> tblDiemCong;
    @FXML private TableColumn<DiemCongXetTuyenDTO, Integer> colId;
    @FXML private TableColumn<DiemCongXetTuyenDTO, String>  colCccd, colMon, colPhuongThuc, colGhiChu;
    @FXML private TableColumn<DiemCongXetTuyenDTO, BigDecimal>
            colDiemUtxtToHop, colDiemUtxtKhongToHop, colDiemCc, colTongThxt, colTongKhongThxt;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();

        searchDebounce = new PauseTransition(Duration.millis(400));
        searchDebounce.setOnFinished(e -> loadData(0));
        tfSearch.textProperty().addListener((obs, old, val) -> searchDebounce.playFromStart());

        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) ->
                loadData(newVal.intValue())
        );

        loadData(0);
    }

    private void setupTable() {
        tblDiemCong.setItems(pageData);
        colId.setCellValueFactory(new PropertyValueFactory<>("idDiemCong"));
        colCccd.setCellValueFactory(new PropertyValueFactory<>("tsCccd"));
        colMon.setCellValueFactory(new PropertyValueFactory<>("mon"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("phuongThuc"));
        colDiemUtxtToHop.setCellValueFactory(new PropertyValueFactory<>("diemUtxtToHop"));
        colDiemUtxtKhongToHop.setCellValueFactory(new PropertyValueFactory<>("diemUtxtKhongXetToHop"));
        colDiemCc.setCellValueFactory(new PropertyValueFactory<>("diemCc"));
        colTongThxt.setCellValueFactory(new PropertyValueFactory<>("diemTongThxt"));
        colTongKhongThxt.setCellValueFactory(new PropertyValueFactory<>("diemTongKhongXetThxt"));
        colGhiChu.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));
    }

    private void loadData(int pageIndex) {
        Task<List<DiemCongXetTuyenDTO>> task = new Task<>() {
            @Override
            protected List<DiemCongXetTuyenDTO> call() {
                if (pageIndex == 0) {
                    totalRecords = bus.getTotal();
                }
                // getAllDiemCongXetTuyen(pageSize, pageIndex) — lưu ý thứ tự tham số ngược
                return bus.getAllDiemCongXetTuyen(PAGE_SIZE, pageIndex);
            }
        };

        task.setOnSucceeded(e -> {
            pageData.setAll(task.getValue());

            int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / PAGE_SIZE));
            pagination.setPageCount(totalPages);
            if (pagination.getCurrentPageIndex() != pageIndex) {
                pagination.setCurrentPageIndex(pageIndex);
            }
            lblCount.setText(totalRecords + " bản ghi (trang " + (pageIndex + 1) + "/" + totalPages + ")");
        });

        task.setOnFailed(e -> showError("Lỗi tải dữ liệu: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void onAdd() { openUnifiedDialog(null, false); }

    @FXML
    private void onEdit() {
        DiemCongXetTuyenDTO selected = tblDiemCong.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn dòng cần sửa."); return; }
        openUnifiedDialog(selected, false);
    }

    @FXML
    private void onDelete() {
        DiemCongXetTuyenDTO selected = tblDiemCong.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn dòng cần xóa."); return; }
        if (confirmDelete("Điểm cộng của thí sinh: " + selected.getTsCccd())) {
            String result = bus.deleteDiemCongXetTuyen(selected.getIdDiemCong());
            if (result.contains("successfully")) {
                showInfo("Thành công", "Đã xóa bản ghi.");
                loadData(pagination.getCurrentPageIndex());
            } else {
                showError(result);
            }
        }
    }

    @FXML private void onImportUtxt() { handleImport(true); }
    @FXML private void onImportCc()   { handleImport(false); }
    @FXML private void onAddCc()      { openUnifiedDialog(null, true); }

    @FXML
    private void onEditCc() {
        DiemCongXetTuyenDTO selected = tblDiemCong.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn dòng cần sửa."); return; }
        openUnifiedDialog(selected, true);
    }

    private void openUnifiedDialog(DiemCongXetTuyenDTO row, boolean preferCc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/admissionManagement/desktop/views/admin/DiemCongUnifiedDialogUI.fxml"));
            Parent root = loader.load();
            DiemCongUnifiedDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm điểm cộng" : "Sửa điểm cộng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            dialogCtrl.init(stage, row, bus, preferCc);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) {
                loadData(pagination.getCurrentPageIndex());
            }
        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }

    private void handleImport(boolean isUtxt) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(isUtxt ? "Import UTXT" : "Import CC");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(tblDiemCong.getScene().getWindow());
        if (file == null) return;

        Task<String> importTask = new Task<>() {
            @Override protected String call() {
                return isUtxt ? bus.importUtxtCsvData(file) : bus.importCcCsvData(file);
            }
        };
        importTask.setOnSucceeded(e -> {
            showInfo("Kết quả Import", importTask.getValue());
            loadData(0);
        });
        importTask.setOnFailed(e -> showError(importTask.getException().getMessage()));
        new Thread(importTask).start();
    }
}
