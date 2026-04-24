package com.admissionManagement.desktop.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DiemController extends BaseController implements Initializable {

    // ── Stat cards ────────────────────────────────────
    @FXML private Label lblTbThpt, lblTbVsat, lblTbDgnl, lblDaNhap, lblDaNhapSub;

    // ── Chart ─────────────────────────────────────────
    @FXML private BarChart<String, Number>  barChart;
    @FXML private CategoryAxis              xAxis;
    @FXML private NumberAxis                yAxis;
    @FXML private ComboBox<String>          cbLoaiDiem, cbToHopChart;

    // ── Toolbar + table ───────────────────────────────
    @FXML private TextField                 tfSearch;
    @FXML private ComboBox<String>          cbLoaiDiemFilter;
    @FXML private TableView<DiemRow>        tblDiem;
    @FXML private TableColumn<DiemRow,String> colCccd, colHoTen, colLoai,
                                               colMon1, colMon2, colMon3, colTong;
    @FXML private TableColumn<DiemRow,Void>   colAction;
    @FXML private Label      lblCount;
    @FXML private Pagination pagination;

    private final ObservableList<DiemRow> allData = FXCollections.observableArrayList();
    private List<DiemRow> filtered = new ArrayList<>();
    private int currentPage = 0;

    @Override public void initialize(URL u, ResourceBundle r) {
        setupComboBoxes();
        setupTable();
        loadData();
        updateStats();
        updateChart();
    }

    private void setupComboBoxes() {
        List<String> loai = List.of("THPT","VSAT","ĐGNL");
        cbLoaiDiem.setItems(FXCollections.observableArrayList(loai));
        cbLoaiDiemFilter.setItems(FXCollections.observableArrayList(loai));
        cbToHopChart.setItems(FXCollections.observableArrayList("A00","A01","B00","D01"));
        cbLoaiDiem.setValue("THPT");
        cbLoaiDiemFilter.setOnAction(e -> applyFilter());
    }

    private void setupTable() {
        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colMon1.setCellValueFactory(new PropertyValueFactory<>("mon1"));
        colMon2.setCellValueFactory(new PropertyValueFactory<>("mon2"));
        colMon3.setCellValueFactory(new PropertyValueFactory<>("mon3"));
        colTong.setCellValueFactory(new PropertyValueFactory<>("tong"));

        // Cột loại — badge màu
        colLoai.setCellValueFactory(new PropertyValueFactory<>("loai"));
        colLoai.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            { badge.getStyleClass().add("badge"); setGraphic(badge); }
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty||v==null){badge.setVisible(false);return;}
                badge.setVisible(true); badge.setText(v);
                badge.getStyleClass().removeAll("badge-green","badge-blue","badge-amber");
                badge.getStyleClass().add(
                    v.equals("THPT") ? "badge-green" : v.equals("VSAT") ? "badge-blue" : "badge-amber"
                );
            }
        });

        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                () -> { /* TODO: mở dialog sửa điểm */ },
                () -> onDeleteRow(getTableView().getItems().get(getIndex()))
            );
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : box);
            }
        });

        pagination.currentPageIndexProperty().addListener((o, ov, nv) -> {
            currentPage = nv.intValue(); showPage();
        });
    }

    private void loadData() {
        // DUMMY — thay bằng diemService.getAll()
        allData.setAll(
            new DiemRow("001234567890","Nguyễn Văn An","THPT","9.0","8.5","8.0","25.5"),
            new DiemRow("001234567891","Trần Thị Bình","VSAT","","","","720"),
            new DiemRow("001234567892","Lê Minh Châu","ĐGNL","","","","850"),
            new DiemRow("001234567893","Phạm Thị Dung","THPT","7.5","8.0","7.0","22.5"),
            new DiemRow("001234567894","Hoàng Văn Em","THPT","8.0","9.0","8.5","25.5")
        );
        applyFilter();
    }

    private void applyFilter() {
        String kw   = tfSearch != null ? tfSearch.getText().trim().toLowerCase() : "";
        String loai = cbLoaiDiemFilter != null ? cbLoaiDiemFilter.getValue() : null;
        filtered = allData.stream()
            .filter(r -> kw.isEmpty() || r.getCccd().contains(kw) || r.getHoTen().toLowerCase().contains(kw))
            .filter(r -> loai == null || r.getLoai().equals(loai))
            .collect(Collectors.toList());
        currentPage = 0;
        pagination.setPageCount(pageCount(filtered.size()));
        pagination.setCurrentPageIndex(0);
        showPage();
    }

    private void showPage() {
        tblDiem.setItems(getPage(filtered, currentPage));
        lblCount.setText(filtered.size() + " bản ghi");
    }

    private void updateStats() {
        // DUMMY stats — thay bằng service
        lblTbThpt.setText("24.5");
        lblTbVsat.setText("680");
        lblTbDgnl.setText("720");
        lblDaNhap.setText("198");
        lblDaNhapSub.setText("/ 235 thí sinh");
    }

    private void updateChart() {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        // DUMMY phân bố điểm THPT
        series.getData().addAll(
            new XYChart.Data<>("<15",  5),
            new XYChart.Data<>("15-18",12),
            new XYChart.Data<>("18-21",38),
            new XYChart.Data<>("21-24",55),
            new XYChart.Data<>("24-27",48),
            new XYChart.Data<>("27-30",22)
        );
        barChart.getData().add(series);
        // Đặt màu bar sau khi add
        series.getData().forEach(d ->
            d.getNode().setStyle("-fx-bar-fill: #3B6D11;")
        );
    }

    @FXML private void onSearch()      { applyFilter(); }
    @FXML private void onFilter()      { applyFilter(); }
    @FXML private void onChartFilter() { updateChart(); }

    @FXML private void onAdd() {
        showInfo("Thêm điểm", "Chức năng thêm điểm sẽ hoạt động sau khi kết nối BE.");
    }

    @FXML private void onImport() {
        showInfo("Import CSV", "Import điểm CSV sau khi kết nối BE.");
    }

    private void onDeleteRow(DiemRow row) {
        if (confirmDelete(row.getHoTen() + " (" + row.getLoai() + ")")) {
            allData.remove(row);
            // TODO: diemService.delete(row)
            applyFilter(); updateStats();
        }
    }

    // ── Row DTO ──────────────────────────────────────
    public static class DiemRow {
        private String cccd, hoTen, loai, mon1, mon2, mon3, tong;
        public DiemRow(String cccd,String hoTen,String loai,String m1,String m2,String m3,String tong){
            this.cccd=cccd;this.hoTen=hoTen;this.loai=loai;this.mon1=m1;this.mon2=m2;this.mon3=m3;this.tong=tong;
        }
        public String getCccd()  {return cccd;}
        public String getHoTen() {return hoTen;}
        public String getLoai()  {return loai;}
        public String getMon1()  {return mon1;}
        public String getMon2()  {return mon2;}
        public String getMon3()  {return mon3;}
        public String getTong()  {return tong;}
    }
}
