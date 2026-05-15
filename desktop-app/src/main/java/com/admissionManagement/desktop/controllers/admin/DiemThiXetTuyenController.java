package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemThiXetTuyenDTO;
import com.admissionManagement.core.dto.ThongKeDiemDTO;
import com.admissionManagement.core.service.DiemThiXetTuyenBUS;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
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

public class DiemThiXetTuyenController extends BaseController implements Initializable {

    private final DiemThiXetTuyenBUS bus = new DiemThiXetTuyenBUS();
    private final ObservableList<DiemThiXetTuyenDTO> pageData = FXCollections.observableArrayList();
    private List<ThongKeDiemDTO> thongKeData;
    private PauseTransition searchDebounce;
    private long totalRecords = 0;

    // PAGE_SIZE nhỏ hơn để load nhanh hơn
    private static final int PAGE_SIZE = 20;

    // ── Stat cards ───────────────────────────────────
    @FXML private Label lblTbThpt, lblTbVsat, lblTbDgnl, lblDaNhap, lblDaNhapSub;

    // ── Chart ────────────────────────────────────────
    @FXML private BarChart<String, Number> barChart;
    @FXML private ComboBox<String> cbLoaiDiem, cbToHopChart;

    // ── Toolbar ──────────────────────────────────────
    @FXML private TextField tfSearch;

    // ── Table ────────────────────────────────────────
    @FXML private TableView<DiemThiXetTuyenDTO> tblDiem;

    @FXML private TableColumn<DiemThiXetTuyenDTO, String>     colCccd, colSbd;
    @FXML private TableColumn<DiemThiXetTuyenDTO, BigDecimal> colToan, colVan, colLy, colHoa,
            colSinh, colSu, colDia, colTin,
            colKtpl, colN1Thi, colN1Cc;
    @FXML private TableColumn<DiemThiXetTuyenDTO, BigDecimal> colToanVsat, colVanVsat, colLyVsat,
            colHoaVsat, colSinhVsat, colSuVsat,
            colDiaVsat, colN1Vsat;
    @FXML private TableColumn<DiemThiXetTuyenDTO, BigDecimal> colNl1, colCncn, colCnnn,
            colNk1, colNk2, colNk3,
            colNk4, colNk5, colNk6;

    // ── Pagination ───────────────────────────────────
    @FXML private Label      lblCount;
    @FXML private Pagination pagination;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchDebounce = new PauseTransition(Duration.millis(400));
        searchDebounce.setOnFinished(e -> loadData(0));
        tfSearch.textProperty().addListener((obs, old, val) -> searchDebounce.playFromStart());

        cbLoaiDiem.setItems(FXCollections.observableArrayList("THPT", "VSAT", "ĐGNL", "Năng khiếu"));
        cbLoaiDiem.setValue("THPT");
        cbLoaiDiem.setOnAction(e -> updateChart());

        setupTable();

        // Listener phân trang — chỉ gọi DB khi người dùng bấm nút trang
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) ->
                loadData(newVal.intValue())
        );

        loadData(0);
        loadStatAndChart();
    }

    private void setupTable() {
        tblDiem.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tblDiem.setItems(pageData);

        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colSbd.setCellValueFactory(new PropertyValueFactory<>("soBaoDanh"));
        colToan.setCellValueFactory(new PropertyValueFactory<>("diemToan"));
        colVan.setCellValueFactory(new PropertyValueFactory<>("diemVan"));
        colLy.setCellValueFactory(new PropertyValueFactory<>("diemLy"));
        colHoa.setCellValueFactory(new PropertyValueFactory<>("diemHoa"));
        colSinh.setCellValueFactory(new PropertyValueFactory<>("diemSinh"));
        colSu.setCellValueFactory(new PropertyValueFactory<>("diemSu"));
        colDia.setCellValueFactory(new PropertyValueFactory<>("diemDia"));
        colTin.setCellValueFactory(new PropertyValueFactory<>("diemTin"));
        colKtpl.setCellValueFactory(new PropertyValueFactory<>("diemKtpl"));
        colN1Thi.setCellValueFactory(new PropertyValueFactory<>("n1Thi"));
        colN1Cc.setCellValueFactory(new PropertyValueFactory<>("n1Cc"));
        colToanVsat.setCellValueFactory(new PropertyValueFactory<>("diemToanVSAT"));
        colVanVsat.setCellValueFactory(new PropertyValueFactory<>("diemVanVSAT"));
        colLyVsat.setCellValueFactory(new PropertyValueFactory<>("diemLyVSAT"));
        colHoaVsat.setCellValueFactory(new PropertyValueFactory<>("diemHoaVSAT"));
        colSinhVsat.setCellValueFactory(new PropertyValueFactory<>("diemSinhVSAT"));
        colSuVsat.setCellValueFactory(new PropertyValueFactory<>("diemSuVSAT"));
        colDiaVsat.setCellValueFactory(new PropertyValueFactory<>("diemDiaVSAT"));
        colN1Vsat.setCellValueFactory(new PropertyValueFactory<>("n1VSAT"));
        colNl1.setCellValueFactory(new PropertyValueFactory<>("nl1"));
        colCncn.setCellValueFactory(new PropertyValueFactory<>("cncn"));
        colCnnn.setCellValueFactory(new PropertyValueFactory<>("cnnn"));
        colNk1.setCellValueFactory(new PropertyValueFactory<>("nk1"));
        colNk2.setCellValueFactory(new PropertyValueFactory<>("nk2"));
        colNk3.setCellValueFactory(new PropertyValueFactory<>("nk3"));
        colNk4.setCellValueFactory(new PropertyValueFactory<>("nk4"));
        colNk5.setCellValueFactory(new PropertyValueFactory<>("nk5"));
        colNk6.setCellValueFactory(new PropertyValueFactory<>("nk6"));
    }

    /** Load dữ liệu trang từ DB — server-side pagination */
    private void loadData(int pageIndex) {
        String keyword = tfSearch.getText().trim();

        Task<List<DiemThiXetTuyenDTO>> task = new Task<>() {
            @Override
            protected List<DiemThiXetTuyenDTO> call() {
                // Gọi getTotal mỗi lần search thay đổi (pageIndex == 0)
                // Khi chỉ chuyển trang, dùng lại totalRecords
                if (pageIndex == 0) {
                    totalRecords = bus.getTotal();
                }
                return bus.getAllDiemThiXetTuyen(pageIndex, PAGE_SIZE);
            }
        };

        task.setOnSucceeded(e -> {
            pageData.setAll(task.getValue());

            int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
            totalPages = Math.max(1, totalPages);

            // Tạm gỡ listener để tránh vòng lặp khi setPageCount
            pagination.setPageCount(totalPages);
            if (pagination.getCurrentPageIndex() != pageIndex) {
                pagination.setCurrentPageIndex(pageIndex);
            }

            lblCount.setText(totalRecords + " bản ghi (trang " + (pageIndex + 1) + "/" + totalPages + ")");
        });

        task.setOnFailed(e -> showError("Lỗi tải dữ liệu: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    /** Load thống kê và chart riêng — không block UI */
    private void loadStatAndChart() {
        Task<List<ThongKeDiemDTO>> task = new Task<>() {
            @Override
            protected List<ThongKeDiemDTO> call() {
                return bus.getThongKeDiem();
            }
        };
        task.setOnSucceeded(e -> {
            thongKeData = task.getValue();
            updateStatCards();
            updateChart();
        });
        new Thread(task).start();
    }

    private void updateStatCards() {
        if (thongKeData == null) return;

        thongKeData.stream()
                .filter(t -> t.getLoaiKyThi().equals("THPT") && t.getSoLuong() > 0)
                .mapToDouble(ThongKeDiemDTO::getDiemTrungBinh).average()
                .ifPresentOrElse(avg -> lblTbThpt.setText(String.format("%.2f", avg)),
                        () -> lblTbThpt.setText("--"));

        thongKeData.stream()
                .filter(t -> t.getLoaiKyThi().equals("VSAT") && t.getSoLuong() > 0)
                .mapToDouble(ThongKeDiemDTO::getDiemTrungBinh).average()
                .ifPresentOrElse(avg -> lblTbVsat.setText(String.format("%.2f", avg)),
                        () -> lblTbVsat.setText("--"));

        thongKeData.stream()
                .filter(t -> t.getLoaiKyThi().equals("ĐGNL") && t.getSoLuong() > 0)
                .mapToDouble(ThongKeDiemDTO::getDiemTrungBinh).average()
                .ifPresentOrElse(avg -> lblTbDgnl.setText(String.format("%.2f", avg)),
                        () -> lblTbDgnl.setText("--"));

        lblDaNhap.setText(String.valueOf(totalRecords));
        lblDaNhapSub.setText("/ " + totalRecords + " thí sinh");
    }

    @FXML private void onChartFilter() { updateChart(); }

    private void updateChart() {
        if (thongKeData == null) return;
        String loai = cbLoaiDiem.getValue();
        if (loai == null) return;
        barChart.getData().clear();
        XYChart.Series<String, Number> seriesAvg = new XYChart.Series<>();
        seriesAvg.setName("Điểm TB");
        thongKeData.stream()
                .filter(t -> t.getLoaiKyThi().equals(loai))
                .forEach(t -> seriesAvg.getData().add(
                        new XYChart.Data<>(t.getTenMon(), t.getDiemTrungBinh())));
        barChart.getData().add(seriesAvg);
    }

    @FXML private void onAdd()    { openDialog(null); }
    @FXML private void onFilter() { loadData(0); }
    @FXML private void onSearch() { loadData(0); }

    @FXML
    private void onEdit() {
        DiemThiXetTuyenDTO selected = tblDiem.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn dòng cần sửa."); return; }
        openDialog(selected);
    }

    @FXML
    private void onDeleteSelected() {
        DiemThiXetTuyenDTO selected = tblDiem.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Vui lòng chọn dòng cần xóa."); return; }
        if (confirmDelete("Bảng điểm của thí sinh: " + selected.getCccd())) {
            String result = bus.deleteDiemThiXetTuyen(selected.getCccd());
            if (result.contains("successfully")) {
                showInfo("Thành công", "Đã xóa bảng điểm.");
                loadData(pagination.getCurrentPageIndex());
            } else {
                showError(result);
            }
        }
    }

    @FXML
    private void onImport() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("VSAT", "VSAT", "ĐGNL");
        dialog.setTitle("Chọn loại import");
        dialog.setHeaderText(null);
        dialog.setContentText("Chọn loại dữ liệu:");
        dialog.showAndWait().ifPresent(this::handleImport);
    }

    private void handleImport(String type) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn file CSV " + type);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            String msg = type.equals("VSAT") ? bus.importVsatCsvData(file) : bus.importDGNLCsvData(file);
            showInfo("Kết quả Import", msg);
            loadData(0);
            loadStatAndChart();
        }
    }

    private void openDialog(DiemThiXetTuyenDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/admissionManagement/desktop/views/admin/DiemThiXetTuyenDialogUI.fxml"));
            Parent root = loader.load();
            DiemThiXetTuyenDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm điểm thi" : "Cập nhật điểm thi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            dialogCtrl.init(stage, row, bus);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) {
                loadData(pagination.getCurrentPageIndex());
            }
        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }
}
