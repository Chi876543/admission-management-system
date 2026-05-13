package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemThiXetTuyenDTO;
import com.admissionManagement.core.dto.ThongKeDTO;
import com.admissionManagement.core.service.DiemThiXetTuyenBUS;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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
    private final ObservableList<DiemThiXetTuyenDTO> masterData  = FXCollections.observableArrayList();
    private final ObservableList<DiemThiXetTuyenDTO> displayData = FXCollections.observableArrayList();
    private List<ThongKeDTO> thongKeData;
    private PauseTransition searchDebounce;

    private static final int PAGE_SIZE = 50;
    private int currentPage = 0;

    // ── Stat cards ───────────────────────────────────
    @FXML private Label lblTbThpt, lblTbVsat, lblTbDgnl, lblDaNhap, lblDaNhapSub;

    // ── Chart ────────────────────────────────────────
    @FXML private BarChart<String, Number> barChart;
    @FXML private ComboBox<String> cbLoaiDiem, cbToHopChart;

    // ── Toolbar ──────────────────────────────────────
    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbLoaiDiemFilter;

    // ── Table ────────────────────────────────────────
    @FXML private TableView<DiemThiXetTuyenDTO> tblDiem;

    // Thông tin chung
    @FXML private TableColumn<DiemThiXetTuyenDTO, String>     colCccd, colSbd, colPhuongThuc;

    // Điểm THPT
    @FXML private TableColumn<DiemThiXetTuyenDTO, BigDecimal> colToan, colVan, colLy, colHoa,
            colSinh, colSu, colDia, colTin,
            colKtpl, colN1Thi, colN1Cc;
    // Điểm VSAT
    @FXML private TableColumn<DiemThiXetTuyenDTO, BigDecimal> colToanVsat, colVanVsat, colLyVsat,
            colHoaVsat, colSinhVsat, colSuVsat,
            colDiaVsat, colN1Vsat;
    // ĐGNL & Năng khiếu
    @FXML private TableColumn<DiemThiXetTuyenDTO, BigDecimal> colNl1, colCncn, colCnnn,
            colNk1, colNk2, colNk3,
            colNk4, colNk5, colNk6;
    // Thao tác
    @FXML private TableColumn<DiemThiXetTuyenDTO, Void> colAction;

    // ── Pagination ───────────────────────────────────
    @FXML private Label      lblCount;
    @FXML private Pagination pagination;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchDebounce = new PauseTransition(Duration.millis(300));
        searchDebounce.setOnFinished(e -> applyFilter());
        tfSearch.textProperty().addListener((obs, old, val) -> searchDebounce.playFromStart());

        cbLoaiDiemFilter.setItems(FXCollections.observableArrayList("Tất cả", "THPT", "VSAT", "ĐGNL"));
        cbLoaiDiemFilter.setValue("Tất cả");
        cbLoaiDiemFilter.setOnAction(e -> applyFilter());

        cbLoaiDiem.setItems(FXCollections.observableArrayList("THPT", "VSAT", "ĐGNL", "Năng khiếu"));
        cbLoaiDiem.setValue("THPT");
        cbLoaiDiem.setOnAction(e -> updateChart());

        setupTable();
        setupPagination();
        loadData();
    }

    private void setupTable() {
        tblDiem.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Thông tin chung
        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colSbd.setCellValueFactory(new PropertyValueFactory<>("soBaoDanh"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("phuongThuc"));

        // Điểm THPT
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

        // Điểm VSAT
        colToanVsat.setCellValueFactory(new PropertyValueFactory<>("diemToanVSAT"));
        colVanVsat.setCellValueFactory(new PropertyValueFactory<>("diemVanVSAT"));
        colLyVsat.setCellValueFactory(new PropertyValueFactory<>("diemLyVSAT"));
        colHoaVsat.setCellValueFactory(new PropertyValueFactory<>("diemHoaVSAT"));
        colSinhVsat.setCellValueFactory(new PropertyValueFactory<>("diemSinhVSAT"));
        colSuVsat.setCellValueFactory(new PropertyValueFactory<>("diemSuVSAT"));
        colDiaVsat.setCellValueFactory(new PropertyValueFactory<>("diemDiaVSAT"));
        colN1Vsat.setCellValueFactory(new PropertyValueFactory<>("n1VSAT"));

        // ĐGNL & Năng khiếu
        colNl1.setCellValueFactory(new PropertyValueFactory<>("nl1"));
        colCncn.setCellValueFactory(new PropertyValueFactory<>("cncn"));
        colCnnn.setCellValueFactory(new PropertyValueFactory<>("cnnn"));
        colNk1.setCellValueFactory(new PropertyValueFactory<>("nk1"));
        colNk2.setCellValueFactory(new PropertyValueFactory<>("nk2"));
        colNk3.setCellValueFactory(new PropertyValueFactory<>("nk3"));
        colNk4.setCellValueFactory(new PropertyValueFactory<>("nk4"));
        colNk5.setCellValueFactory(new PropertyValueFactory<>("nk5"));
        colNk6.setCellValueFactory(new PropertyValueFactory<>("nk6"));

        // Thao tác
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

        tblDiem.setItems(displayData);
    }

    private void setupPagination() {
        pagination.setPageFactory(pageIndex -> {
            currentPage = pageIndex;
            updatePage();
            return tblDiem;
        });
    }

    private void updatePage() {
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, masterData.size());
        displayData.setAll(masterData.subList(from, to));
    }

    private void refreshPagination() {
        int pageCount = Math.max(1, (int) Math.ceil((double) masterData.size() / PAGE_SIZE));
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        currentPage = 0;
        updatePage();
        lblCount.setText(masterData.size() + " bản ghi");
    }

    private void loadData() {
        masterData.setAll(bus.getAllDiemThiXetTuyen());
        thongKeData = bus.getThongKeDiem();
        updateStatCards();
        updateChart();
        applyFilter();
    }

    private void applyFilter() {
        String key  = tfSearch.getText().trim().toLowerCase();
        String loai = cbLoaiDiemFilter.getValue();

        List<DiemThiXetTuyenDTO> filtered = bus.getAllDiemThiXetTuyen().stream()
                .filter(d -> {
                    boolean matchKey = key.isEmpty()
                            || d.getCccd().toLowerCase().contains(key)
                            || (d.getSoBaoDanh() != null && d.getSoBaoDanh().toLowerCase().contains(key));
                    boolean matchLoai = loai == null || loai.equals("Tất cả")
                            || (d.getPhuongThuc() != null && d.getPhuongThuc().equalsIgnoreCase(loai));
                    return matchKey && matchLoai;
                })
                .toList();

        masterData.setAll(filtered);
        refreshPagination();
    }

    private void updateStatCards() {
        if (thongKeData == null) return;

        // THPT - lấy TB tất cả môn THPT có dữ liệu
        thongKeData.stream()
                .filter(t -> t.getLoaiKyThi().equals("THPT") && t.getSoLuong() > 0)
                .mapToDouble(ThongKeDTO::getDiemTrungBinh)
                .average()
                .ifPresentOrElse(
                        avg -> lblTbThpt.setText(String.format("%.2f", avg)),
                        () -> lblTbThpt.setText("--")
                );

        // VSAT - lấy TB tất cả môn VSAT có dữ liệu
        thongKeData.stream()
                .filter(t -> t.getLoaiKyThi().equals("VSAT") && t.getSoLuong() > 0)
                .mapToDouble(ThongKeDTO::getDiemTrungBinh)
                .average()
                .ifPresentOrElse(
                        avg -> lblTbVsat.setText(String.format("%.2f", avg)),
                        () -> lblTbVsat.setText("--")
                );

        // ĐGNL
        thongKeData.stream()
                .filter(t -> t.getLoaiKyThi().equals("ĐGNL") && t.getSoLuong() > 0)
                .mapToDouble(ThongKeDTO::getDiemTrungBinh)
                .average()
                .ifPresentOrElse(
                        avg -> lblTbDgnl.setText(String.format("%.2f", avg)),
                        () -> lblTbDgnl.setText("--")
                );

        // Số thí sinh đã nhập điểm
        long total = masterData.size();
        lblDaNhap.setText(String.valueOf(total));
        lblDaNhapSub.setText("/ " + total + " thí sinh");
    }

    @FXML
    private void onChartFilter() { updateChart(); }

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
                        new XYChart.Data<>(t.getTenMon(), t.getDiemTrungBinh())
                ));

        barChart.getData().add(seriesAvg);
    }

    @FXML private void onAdd() { openDialog(null); }

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
            String msg = type.equals("VSAT")
                    ? bus.importVsatCsvData(file)
                    : bus.importDGNLCsvData(file);
            showInfo("Kết quả Import", msg);
            loadData();
        }
    }

    @FXML private void onFilter() { applyFilter(); }
    @FXML private void onSearch() { applyFilter(); }

    private void onDelete(DiemThiXetTuyenDTO row) {
        if (confirmDelete("Bảng điểm của thí sinh: " + row.getCccd())) {
            String result = bus.deleteDiemThiXetTuyen(row.getCccd());
            if (result.contains("successfully")) {
                loadData();
                showInfo("Thành công", "Đã xóa bảng điểm.");
            } else {
                showError(result);
            }
        }
    }

    private void openDialog(DiemThiXetTuyenDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/admissionManagement/desktop/views/admin/DiemThiXetTuyenDialogUI.fxml"
                    )
            );
            Parent root = loader.load();
            DiemThiXetTuyenDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm điểm thi" : "Cập nhật điểm thi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            dialogCtrl.init(stage, row, bus);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) loadData();

        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }
}