package com.admissionManagement.desktop.controllers.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ThiSinhController extends BaseController implements Initializable {

    // ── List view ────────────────────────────────────
    @FXML private TextField   tfSearch;
    @FXML private TableView<ThiSinhRow>          tblThiSinh;
    @FXML private TableColumn<ThiSinhRow, String> colStt, colCccd, colHoTen,
                                                   colNgaySinh, colGioiTinh, colSdt, colEmail;
    @FXML private TableColumn<ThiSinhRow, Void>   colAction;
    @FXML private Label      lblCount;
    @FXML private Pagination pagination;

    // ── Dialog fields ────────────────────────────────
    @FXML private Label         lblDialogTitle;
    @FXML private TextField     tfCccd, tfHoTen, tfSdt, tfEmail;
    @FXML private DatePicker    dpNgaySinh;
    @FXML private ComboBox<String> cbGioiTinh;
    @FXML private TextArea      tadiaChi;
    @FXML private Label         lblError;

    private final ObservableList<ThiSinhRow> allData = FXCollections.observableArrayList();
    private List<ThiSinhRow> filtered = new ArrayList<>();
    private int currentPage = 0;
    private ThiSinhRow editingRow;
    private Stage dialogStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Kiểm tra đang load màn hình list hay dialog
        if (tblThiSinh != null) {
            setupTable();
            loadData();
        }
        if (cbGioiTinh != null) {
            cbGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        }
    }

    // ── Table setup ──────────────────────────────────
    private void setupTable() {
        colStt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStt()));
        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                () -> openDialog(getTableView().getItems().get(getIndex())),
                () -> onDelete(getTableView().getItems().get(getIndex()))
            );
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        pagination.currentPageIndexProperty().addListener((o, ov, nv) -> {
            currentPage = nv.intValue(); showPage();
        });
    }

    // ── Data ─────────────────────────────────────────
    private void loadData() {
        // DUMMY — thay bằng thiSinhService.getAll()
        allData.setAll(
            new ThiSinhRow("1","001234567890","Nguyễn Văn An",  "15/03/2006","Nam","0901234567","an@gmail.com"),
            new ThiSinhRow("2","001234567891","Trần Thị Bình",  "22/07/2006","Nữ","0912345678","binh@gmail.com"),
            new ThiSinhRow("3","001234567892","Lê Minh Châu",   "10/11/2005","Nam","0923456789","chau@gmail.com"),
            new ThiSinhRow("4","001234567893","Phạm Thị Dung",  "05/01/2006","Nữ","0934567890","dung@gmail.com"),
            new ThiSinhRow("5","001234567894","Hoàng Văn Em",   "30/09/2006","Nam","0945678901","em@gmail.com")
        );
        applyFilter();
    }

    private void applyFilter() {
        String kw = tfSearch.getText().trim().toLowerCase();
        filtered = allData.stream()
            .filter(r -> kw.isEmpty()
                || r.getCccd().contains(kw)
                || r.getHoTen().toLowerCase().contains(kw))
            .collect(Collectors.toList());
        for (int i = 0; i < filtered.size(); i++) filtered.get(i).setStt(String.valueOf(i+1));
        currentPage = 0;
        pagination.setPageCount(pageCount(filtered.size()));
        pagination.setCurrentPageIndex(0);
        showPage();
    }

    private void showPage() {
        tblThiSinh.setItems(getPage(filtered, currentPage));
        lblCount.setText(filtered.size() + " thí sinh");
    }

    @FXML private void onSearch()  { applyFilter(); }
    @FXML private void onAdd()     { openDialog(null); }
    @FXML private void onImport()  {
        // TODO: đọc CSV, parse, thêm vào allData
        showInfo("Import CSV", "Chức năng import sẽ hoạt động sau khi kết nối BE.");
    }

    private void onDelete(ThiSinhRow row) {
        if (confirmDelete(row.getHoTen())) {
            allData.remove(row);
            // TODO: thiSinhService.delete(row.getCccd());
            applyFilter();
        }
    }

    // ── Dialog ───────────────────────────────────────
    private void openDialog(ThiSinhRow row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/admissionManagement/desktop/views/admin/thisinh-dialog.fxml"));
            Parent root = loader.load();
            ThiSinhController ctrl = loader.getController();

            dialogStage = new Stage();
            dialogStage.setTitle(row == null ? "Thêm thí sinh" : "Sửa thí sinh");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            ctrl.initDialog(dialogStage, row, allData, this);
            dialogStage.showAndWait();
            applyFilter();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void initDialog(Stage stage, ThiSinhRow row,
                           ObservableList<ThiSinhRow> data, ThiSinhController parent) {
        this.dialogStage = stage;
        this.editingRow  = row;

        if (row != null) {
            lblDialogTitle.setText("Sửa thí sinh");
            tfCccd.setText(row.getCccd()); tfCccd.setDisable(true);
            tfHoTen.setText(row.getHoTen());
            tfSdt.setText(row.getSdt());
            tfEmail.setText(row.getEmail());
            cbGioiTinh.setValue(row.getGioiTinh());
        } else {
            lblDialogTitle.setText("Thêm thí sinh");
        }
    }

    @FXML private void onDialogSave() {
        if (tfHoTen.getText().trim().isEmpty()) { lblError.setText("Họ tên không được để trống."); return; }
        if (tfCccd.getText().trim().length() != 12) { lblError.setText("CCCD phải đủ 12 số."); return; }

        if (editingRow == null) {
            ThiSinhRow r = new ThiSinhRow(
                String.valueOf(allData.size()+1),
                tfCccd.getText().trim(), tfHoTen.getText().trim(),
                dpNgaySinh.getValue() != null ? dpNgaySinh.getValue().toString() : "",
                cbGioiTinh.getValue() != null ? cbGioiTinh.getValue() : "",
                tfSdt.getText().trim(), tfEmail.getText().trim()
            );
            // TODO: thiSinhService.create(r)
            // Nếu BE trả về id thì cập nhật lại r
            allData.add(r);
        } else {
            editingRow.setHoTen(tfHoTen.getText().trim());
            editingRow.setSdt(tfSdt.getText().trim());
            editingRow.setEmail(tfEmail.getText().trim());
            editingRow.setGioiTinh(cbGioiTinh.getValue());
            // TODO: thiSinhService.update(editingRow)
        }
        dialogStage.close();
    }

    @FXML private void onDialogCancel() { dialogStage.close(); }

    // ── Row DTO ──────────────────────────────────────
    public static class ThiSinhRow {
        private String stt, cccd, hoTen, ngaySinh, gioiTinh, sdt, email;
        public ThiSinhRow(String stt, String cccd, String hoTen, String ngaySinh,
                          String gioiTinh, String sdt, String email) {
            this.stt=stt; this.cccd=cccd; this.hoTen=hoTen; this.ngaySinh=ngaySinh;
            this.gioiTinh=gioiTinh; this.sdt=sdt; this.email=email;
        }
        public String getStt()      { return stt; }
        public String getCccd()     { return cccd; }
        public String getHoTen()    { return hoTen; }
        public String getNgaySinh() { return ngaySinh; }
        public String getGioiTinh() { return gioiTinh; }
        public String getSdt()      { return sdt; }
        public String getEmail()    { return email; }
        public void setStt(String v)      { stt=v; }
        public void setHoTen(String v)    { hoTen=v; }
        public void setSdt(String v)      { sdt=v; }
        public void setEmail(String v)    { email=v; }
        public void setGioiTinh(String v) { gioiTinh=v; }
    }
}
