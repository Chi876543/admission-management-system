package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.Normalizer;
import java.util.List;
import java.util.ResourceBundle;

public class NguyenVongXetTuyenController
        extends BaseController
        implements Initializable {

    private final NguyenVongXetTuyenBUS bus = new NguyenVongXetTuyenBUS();

    // Toàn bộ dữ liệu
    private final ObservableList<NguyenVongXetTuyenDTO> allData =
            FXCollections.observableArrayList();

    // Dữ liệu sau khi lọc
    private FilteredList<NguyenVongXetTuyenDTO> filteredData;

    // Dữ liệu sau khi sort (dùng để slice trang)
    private SortedList<NguyenVongXetTuyenDTO> sortedData;

    // Dữ liệu hiển thị trên bảng (chỉ trang hiện tại)
    private final ObservableList<NguyenVongXetTuyenDTO> pageData =
            FXCollections.observableArrayList();

    // Phân trang
    private int currentPage = 0;
    private static final int PAGE_SIZE = 20;

    // ── FXML ──────────────────────────────────────────────
    @FXML private TextField tfSearch;
    @FXML private Label lblCount;
    @FXML private TableView<NguyenVongXetTuyenDTO> tblNguyenVong;

    @FXML private TableColumn<NguyenVongXetTuyenDTO, Integer>  colId, colThuTu;
    @FXML private TableColumn<NguyenVongXetTuyenDTO, String>   colCccd, colMaNganh,
            colPhuongThuc, colToHop, colKetQua, colNvKeys;
    @FXML private TableColumn<NguyenVongXetTuyenDTO, BigDecimal>
            colDiemTHXT, colDiemUTQD, colDiemCong, colDiemXT;

    // Phân trang controls — thêm vào FXML tương ứng
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Label  lblPage;

    // ─────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadData();

        filteredData = new FilteredList<>(allData, b -> true);

        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            String keyword = normalize(newVal);
            filteredData.setPredicate(item -> {
                if (keyword.isEmpty()) return true;
                return String.valueOf(item.getIdNv()).contains(keyword)
                        || contains(item.getCccd(), keyword)
                        || contains(item.getMaNganh(), keyword);
            });
            // Khi filter thay đổi → về trang 1
            currentPage = 0;
            updatePage();
        });

        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblNguyenVong.comparatorProperty());

        // Khi sort thay đổi → refresh trang hiện tại
        sortedData.comparatorProperty().addListener((obs, o, n) -> updatePage());

        // Bảng hiển thị pageData thay vì sortedData trực tiếp
        tblNguyenVong.setItems(pageData);

        updatePage();
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
        colNvKeys.setCellValueFactory(new PropertyValueFactory<>("nvKeys"));
    }

    private void loadData() {
        allData.setAll(bus.getAllNganhToHop());
        currentPage = 0;
        updatePage();
    }

    // ── Phân trang ────────────────────────────────────────

    private void updatePage() {
        int totalItems = sortedData == null ? 0 : sortedData.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / PAGE_SIZE));

        // Đảm bảo currentPage hợp lệ
        if (currentPage >= totalPages) currentPage = totalPages - 1;
        if (currentPage < 0) currentPage = 0;

        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, totalItems);

        pageData.setAll(
                totalItems == 0
                        ? List.of()
                        : sortedData.subList(from, to)
        );

        // Cập nhật label trang và nút
        lblPage.setText("Trang " + (currentPage + 1) + " / " + totalPages);
        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable(currentPage >= totalPages - 1);

        // Cập nhật count: hiện số bản ghi đang lọc + tổng
        lblCount.setText(totalItems + " bản ghi"
                + (totalItems != allData.size() ? " (tổng: " + allData.size() + ")" : ""));
    }

    @FXML
    private void onPrevPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePage();
        }
    }

    @FXML
    private void onNextPage() {
        int totalPages = (int) Math.ceil((double) sortedData.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) {
            currentPage++;
            updatePage();
        }
    }

    // ── CRUD ─────────────────────────────────────────────

    @FXML
    private void onAdd() {
        openDialog(null);
    }

    @FXML
    private void onEdit() {
        NguyenVongXetTuyenDTO selected =
                tblNguyenVong.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn dòng cần sửa.");
            return;
        }
        openDialog(selected);
    }

    @FXML
    private void onDeleteSelected() {
        NguyenVongXetTuyenDTO selected =
                tblNguyenVong.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn dòng cần xóa.");
            return;
        }

        if (confirmDelete("Nguyện vọng " + selected.getThuTu())) {
            String result = bus.deleteNguyenVongXetTuyen(selected.getIdNv());
            if (!result.startsWith("Lỗi")) {
                allData.remove(selected); // xóa khỏi list gốc → filteredData tự cập nhật
                updatePage();
                showInfo("Thành công", "Đã xóa nguyện vọng.");
            } else {
                showError(result);
            }
        }
    }

    @FXML
    private void onImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn file import");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
        );
        File file = chooser.showOpenDialog(tblNguyenVong.getScene().getWindow());
        if (file == null) return;
        showInfo("Import", "Đã chọn file:\n" + file.getAbsolutePath());
        // TODO: bus.importDataProcess(file.getAbsolutePath());
    }

    private void openDialog(NguyenVongXetTuyenDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/admissionManagement/desktop/views/admin/NguyenVongXetTuyenDialogUI.fxml"
                    )
            );
            Parent root = loader.load();
            NguyenVongXetTuyenDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm nguyện vọng" : "Sửa nguyện vọng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            dialogCtrl.init(stage, row, bus, allData);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) {
                // allData đã được dialog cập nhật trực tiếp
                // Chỉ cần refresh trang
                updatePage();
            }

        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────

    private boolean contains(String source, String keyword) {
        return normalize(source).contains(keyword);
    }

    private String normalize(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().trim();
    }
}