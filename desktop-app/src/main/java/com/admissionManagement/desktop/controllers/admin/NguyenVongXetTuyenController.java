package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
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

public class NguyenVongXetTuyenController extends BaseController implements Initializable {

    private final NguyenVongXetTuyenBUS bus = new NguyenVongXetTuyenBUS();
    private final ObservableList<NguyenVongXetTuyenDTO> pageData = FXCollections.observableArrayList();
    private PauseTransition searchDebounce;
    private long totalRecords = 0;

    @FXML private TextField tfSearch;
    @FXML private Label lblCount;
    @FXML private Pagination pagination;

    @FXML private TableView<NguyenVongXetTuyenDTO> tblNguyenVong;
    @FXML private TableColumn<NguyenVongXetTuyenDTO, Integer>    colId, colThuTu;
    @FXML private TableColumn<NguyenVongXetTuyenDTO, String>     colCccd, colMaNganh,
            colPhuongThuc, colToHop, colKetQua, colNvKeys;
    @FXML private TableColumn<NguyenVongXetTuyenDTO, BigDecimal>
            colDiemTHXT, colDiemUTQD, colDiemCong, colDiemXT;

    // btnPrev/btnNext đã được thay bằng Pagination control trong FXML

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();

        searchDebounce = new PauseTransition(Duration.millis(400));
        searchDebounce.setOnFinished(e -> loadData(0));
        tfSearch.textProperty().addListener((obs, old, val) -> searchDebounce.playFromStart());

        // Dùng Pagination nếu có trong FXML
        if (pagination != null) {
            pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) ->
                    loadData(newVal.intValue())
            );
        }

        loadData(0);
    }

    private void setupTable() {
        tblNguyenVong.setItems(pageData);
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
        colNvKeys.setCellValueFactory(new PropertyValueFactory<>("nvKeys"));
    }

    private void loadData(int pageIndex) {
        String keyword = tfSearch != null ? tfSearch.getText().trim() : "";
        Task<List<NguyenVongXetTuyenDTO>> task = new Task<>() {
            @Override
            protected List<NguyenVongXetTuyenDTO> call() {
                // BUG FIX: luôn cập nhật totalRecords khi pageIndex == 0 hoặc khi có keyword
                // Trước đây chỉ update khi pageIndex == 0, dẫn đến total sai khi search từ trang > 0
                if (pageIndex == 0 || !keyword.isEmpty()) {
                    totalRecords = bus.getTotal(keyword.isEmpty() ? null : keyword);
                }
                return bus.getAllNganhToHop(keyword.isEmpty() ? null : keyword, pageIndex, PAGE_SIZE);
            }
        };

        task.setOnSucceeded(e -> {
            pageData.setAll(task.getValue());

            int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / PAGE_SIZE));

            // Cập nhật Pagination (nếu có)
            if (pagination != null) {
                pagination.setPageCount(totalPages);
                if (pagination.getCurrentPageIndex() != pageIndex) {
                    pagination.setCurrentPageIndex(pageIndex);
                }
            }

            if (lblCount != null)
                lblCount.setText(totalRecords + " bản ghi (trang " + (pageIndex + 1) + "/" + totalPages + ")");
        });

        task.setOnFailed(e -> showError("Lỗi tải dữ liệu: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    // Lấy pageIndex hiện tại (hỗ trợ cả 2 kiểu pagination)
    private int getCurrentPageIndex() {
        if (pagination != null) return pagination.getCurrentPageIndex();
        return 0;
    }

    @FXML
    private void onAdd() { openDialog(null); }

    @FXML
    private void onEdit() {
        NguyenVongXetTuyenDTO selected = tblNguyenVong.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn dòng cần sửa."); return; }
        openDialog(selected);
    }

    @FXML
    private void onDeleteSelected() {
        NguyenVongXetTuyenDTO selected = tblNguyenVong.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn dòng cần xóa."); return; }
        if (confirmDelete("Nguyện vọng " + selected.getThuTu())) {
            String result = bus.deleteNguyenVongXetTuyen(selected.getIdNv());
            if (!result.startsWith("Lỗi")) {
                showInfo("Thành công", "Đã xóa nguyện vọng.");
                loadData(getCurrentPageIndex());
            } else {
                showError(result);
            }
        }
    }

    @FXML
    private void onImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file CSV nguyện vọng");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(tblNguyenVong.getScene().getWindow());
        if (selectedFile != null) {
            Task<String> importTask = new Task<>() {
                @Override protected String call() { return bus.importFromCsv(selectedFile); }
            };
            importTask.setOnSucceeded(e -> {
                showInfo("Kết quả", importTask.getValue());
                loadData(0);
            });
            importTask.setOnFailed(e -> showError("Lỗi Import: " + importTask.getException().getMessage()));
            new Thread(importTask).start();
            showInfo("Đang xử lý", "Hệ thống đang import ngầm, vui lòng đợi...");
        }
    }

    private void openDialog(NguyenVongXetTuyenDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/admissionManagement/desktop/views/admin/NguyenVongXetTuyenDialogUI.fxml"));
            Parent root = loader.load();
            NguyenVongXetTuyenDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm nguyện vọng" : "Sửa nguyện vọng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Dialog cần ObservableList để cập nhật trực tiếp — truyền pageData
            dialogCtrl.init(stage, row, bus, pageData);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) {
                loadData(getCurrentPageIndex());
            }
        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }
}
