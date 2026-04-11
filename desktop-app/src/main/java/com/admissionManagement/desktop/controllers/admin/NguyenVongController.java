package com.admissionManagement.desktop.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════════════════
//  NguyenVongController
// ═══════════════════════════════════════════════════════════════
public class NguyenVongController extends BaseController implements Initializable {

    @FXML private Label     lblTongNV, lblTrung, lblChuaXet, lblKhongDu, lblCount;
    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbNganh, cbKetQua;
    @FXML private TableView<NVRow>           tblNguyenVong;
    @FXML private TableColumn<NVRow,String>  colCccd, colHoTen, colNV1, colNV2, colNV3,
            colDiemXet, colKetQua;
    @FXML private TableColumn<NVRow,Void>    colAction;
    @FXML private Pagination pagination;

    private final ObservableList<NVRow> allData = FXCollections.observableArrayList();
    private List<NVRow> filtered = new ArrayList<>();
    private int currentPage = 0;

    @Override public void initialize(URL u, ResourceBundle r) {
        cbKetQua.setItems(FXCollections.observableArrayList("Trúng tuyển","Chưa xét","Không đủ ĐK"));
        cbNganh.setItems(FXCollections.observableArrayList("CNTT","Kế toán","Kỹ thuật ĐT"));
        cbKetQua.setOnAction(e -> applyFilter());
        cbNganh.setOnAction(e  -> applyFilter());

        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colNV1.setCellValueFactory(new PropertyValueFactory<>("nv1"));
        colNV2.setCellValueFactory(new PropertyValueFactory<>("nv2"));
        colNV3.setCellValueFactory(new PropertyValueFactory<>("nv3"));
        colDiemXet.setCellValueFactory(new PropertyValueFactory<>("diemXet"));
        colKetQua.setCellValueFactory(new PropertyValueFactory<>("ketQua"));
        colKetQua.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            { badge.getStyleClass().add("badge"); setGraphic(badge); }
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty||v==null) { badge.setVisible(false); return; }
                badge.setVisible(true); badge.setText(v);
                badge.getStyleClass().removeAll("badge-green","badge-amber","badge-red");
                badge.getStyleClass().add(v.equals("Trúng tuyển")?"badge-green":v.equals("Chưa xét")?"badge-amber":"badge-red");
            }
        });
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Chi tiết");
            { btn.getStyleClass().addAll("btn-default","btn-sm");
                btn.setOnAction(e -> showInfo("Chi tiết", "CCCD: " + getTableView().getItems().get(getIndex()).getCccd())); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : btn);
            }
        });

        pagination.currentPageIndexProperty().addListener((o,ov,nv) -> { currentPage=nv.intValue(); showPage(); });

        allData.setAll(
                new NVRow("001234567890","Nguyễn Văn An","CNTT","Kế toán","","27.25","Trúng tuyển"),
                new NVRow("001234567891","Trần Thị Bình","CNTT","Kỹ thuật ĐT","","23.5","Chưa xét"),
                new NVRow("001234567892","Lê Minh Châu","Kế toán","","","17.0","Không đủ ĐK")
        );
        applyFilter();
        updateStats();
    }

    private void updateStats() {
        lblTongNV.setText(String.valueOf(allData.size()));
        lblTrung.setText(String.valueOf(allData.stream().filter(r->r.getKetQua().equals("Trúng tuyển")).count()));
        lblChuaXet.setText(String.valueOf(allData.stream().filter(r->r.getKetQua().equals("Chưa xét")).count()));
        lblKhongDu.setText(String.valueOf(allData.stream().filter(r->r.getKetQua().equals("Không đủ ĐK")).count()));
    }

    private void applyFilter() {
        String kw = tfSearch != null ? tfSearch.getText().trim().toLowerCase() : "";
        String ng = cbNganh  != null ? cbNganh.getValue()  : null;
        String kq = cbKetQua != null ? cbKetQua.getValue() : null;
        filtered = allData.stream()
                .filter(r -> kw.isEmpty() || r.getCccd().contains(kw) || r.getHoTen().toLowerCase().contains(kw))
                .filter(r -> ng == null || r.getNv1().equals(ng) || r.getNv2().equals(ng))
                .filter(r -> kq == null || r.getKetQua().equals(kq))
                .collect(Collectors.toList());
        currentPage = 0;
        pagination.setPageCount(pageCount(filtered.size()));
        pagination.setCurrentPageIndex(0);
        showPage();
    }

    private void showPage() {
        tblNguyenVong.setItems(getPage(filtered, currentPage));
        lblCount.setText(filtered.size() + " bản ghi");
    }

    @FXML private void onSearch()  { applyFilter(); }
    @FXML private void onFilter()  { applyFilter(); }
    @FXML private void onAdd()     { showInfo("Thêm NV", "Chức năng thêm nguyện vọng sẽ hoạt động sau khi kết nối BE."); }

    @FXML private void onXetTuyen() {
        // TODO: Gọi API xét tuyển BE, cập nhật kết quả
        showInfo("Xét tuyển", "Đang chạy xét tuyển...\nChức năng sẽ hoạt động khi kết nối BE.");
    }

    public static class NVRow {
        private String cccd,hoTen,nv1,nv2,nv3,diemXet,ketQua;
        public NVRow(String cccd,String hoTen,String nv1,String nv2,String nv3,String diem,String kq){
            this.cccd=cccd;this.hoTen=hoTen;this.nv1=nv1;this.nv2=nv2;this.nv3=nv3;this.diemXet=diem;this.ketQua=kq;
        }
        public String getCccd()    {return cccd;}
        public String getHoTen()   {return hoTen;}
        public String getNv1()     {return nv1;}
        public String getNv2()     {return nv2;}
        public String getNv3()     {return nv3;}
        public String getDiemXet() {return diemXet;}
        public String getKetQua()  {return ketQua;}
        public void setKetQua(String v){ketQua=v;}
    }
}
