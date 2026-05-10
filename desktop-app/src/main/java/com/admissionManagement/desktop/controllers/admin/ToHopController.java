package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.ToHopMonThiDTO;
import com.admissionManagement.core.service.ToHopMonThiBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ToHopController extends BaseController implements Initializable {

    public static final List<String> DS_MON = List.of(
            "TO", "LI", "HO", "SI", "VA", "SU", "DI", "TI",
            "N1", "KTPL", "N1_CC", "CNCN", "CNNN"
    );

    @FXML private Label     lblFormTitle, lblError, lblCount, lblImportStatus;
    @FXML private TextField tfMaToHop, tfTenToHop, tfSearch;
    @FXML private ComboBox<String> cbMon1, cbMon2, cbMon3;

    @FXML private TableView<ToHopRow>          tblToHop;
    @FXML private TableColumn<ToHopRow,String> colMa, colTen, colMon1, colMon2, colMon3;
    @FXML private TableColumn<ToHopRow,Void>   colAction;

    private final ObservableList<ToHopRow> allData = FXCollections.observableArrayList();
    private final ToHopMonThiBUS toHopBUS = new ToHopMonThiBUS();
    private ToHopRow editingRow;

    @Override
    public void initialize(URL u, ResourceBundle r) {
        setupComboBoxes();
        setupTable();
        loadData();
    }

    private void setupComboBoxes() {
        cbMon1.setItems(FXCollections.observableArrayList(DS_MON));
        cbMon2.setItems(FXCollections.observableArrayList(DS_MON));
        cbMon3.setItems(FXCollections.observableArrayList(DS_MON));
        cbMon1.setOnAction(e -> validateMonTrung());
        cbMon2.setOnAction(e -> validateMonTrung());
        cbMon3.setOnAction(e -> validateMonTrung());
    }

    private void validateMonTrung() {
        lblError.setText("");
        String m1 = cbMon1.getValue(), m2 = cbMon2.getValue(), m3 = cbMon3.getValue();
        if (m1 != null && m2 != null && m1.equals(m2)) lblError.setText("Môn 1 và Môn 2 không được trùng nhau.");
        else if (m1 != null && m3 != null && m1.equals(m3)) lblError.setText("Môn 1 và Môn 3 không được trùng nhau.");
        else if (m2 != null && m3 != null && m2.equals(m3)) lblError.setText("Môn 2 và Môn 3 không được trùng nhau.");
    }

    private void setupTable() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maToHop"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenToHop"));
        colMon1.setCellValueFactory(new PropertyValueFactory<>("mon1"));
        colMon2.setCellValueFactory(new PropertyValueFactory<>("mon2"));
        colMon3.setCellValueFactory(new PropertyValueFactory<>("mon3"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                    () -> loadToForm(getTableView().getItems().get(getIndex())),
                    () -> onDeleteRow(getTableView().getItems().get(getIndex()))
            );
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : box);
            }
        });
    }

    public void loadData() {
        List<ToHopMonThiDTO> list = new ArrayList<>(toHopBUS.getAllToHopMonThi());
        Collections.reverse(list);
        allData.setAll(list.stream().map(dto -> new ToHopRow(
                dto.getIdToHop(), dto.getMaToHop(), dto.getTenToHop(),
                dto.getMon1(), dto.getMon2(), dto.getMon3()
        )).collect(Collectors.toList()));
        refresh();
    }

    private void refresh() {
        String kw = tfSearch != null ? tfSearch.getText().trim().toLowerCase() : "";
        List<ToHopRow> f = allData.stream()
                .filter(r -> kw.isEmpty()
                        || r.getMaToHop().toLowerCase().contains(kw)
                        || r.getTenToHop().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        tblToHop.setItems(FXCollections.observableArrayList(f));
        lblCount.setText(f.size() + " tổ hợp");
    }

    @FXML private void onSearch() { refresh(); }

    @FXML private void onTableClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            ToHopRow row = tblToHop.getSelectionModel().getSelectedItem();
            if (row != null) loadToForm(row);
        }
    }

    private void loadToForm(ToHopRow row) {
        editingRow = row;
        lblFormTitle.setText("Sửa tổ hợp: " + row.getMaToHop());
        tfMaToHop.setText(row.getMaToHop()); tfMaToHop.setDisable(true);
        tfTenToHop.setText(row.getTenToHop());
        cbMon1.setValue(row.getMon1());
        cbMon2.setValue(row.getMon2());
        cbMon3.setValue(row.getMon3());
        lblError.setText("");
    }

    @FXML private void onSave() {
        lblError.setText("");
        String ma = tfMaToHop.getText().trim();
        if (ma.isEmpty()) { lblError.setText("Mã tổ hợp không được để trống."); return; }
        if (cbMon1.getValue() == null || cbMon2.getValue() == null || cbMon3.getValue() == null) {
            lblError.setText("Phải chọn đủ 3 môn thi."); return;
        }
        validateMonTrung();
        if (!lblError.getText().isEmpty()) return;

        ToHopMonThiDTO dto = new ToHopMonThiDTO(
                editingRow != null ? editingRow.getId() : 0,
                ma, cbMon1.getValue(), cbMon2.getValue(),
                cbMon3.getValue(), tfTenToHop.getText().trim()
        );

        String result = editingRow == null
                ? toHopBUS.addToHopMonThi(dto)
                : toHopBUS.updateToHopMonThi(editingRow.getId(), dto);

        if (result.startsWith("Lỗi")) { lblError.setText(result); return; }
        onReset();
        loadData();
    }

    @FXML private void onReset() {
        editingRow = null;
        lblFormTitle.setText("Thêm tổ hợp mới"); lblError.setText("");
        tfMaToHop.clear(); tfMaToHop.setDisable(false);
        tfTenToHop.clear(); cbMon1.setValue(null); cbMon2.setValue(null); cbMon3.setValue(null);
    }

    private void onDeleteRow(ToHopRow row) {
        if (!confirmDelete(row.getMaToHop() + " - " + row.getTenToHop())) return;
        String result = toHopBUS.deleteToHopMonThi(row.getId());
        if (result.startsWith("Lỗi")) {
            showError(result);
        } else {
            loadData();
        }
    }

    // ── Import CSV ────────────────────────────────────────────────────────────
    @FXML private void onImportCSV() {
        lblImportStatus.setText("");
        lblImportStatus.setStyle("-fx-text-fill: #2e7d32;");

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn file CSV danh sách tổ hợp môn thi");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showOpenDialog(tblToHop.getScene().getWindow());
        if (file == null) return;

        try (FileInputStream fis = new FileInputStream(file)) {
            // TODO: gọi BUS khi bạn hoàn thiện
            // String result = toHopBUS.importFromCSV(fis);
            // if (result.startsWith("Lỗi")) {
            //     lblImportStatus.setStyle("-fx-text-fill: #c62828;");
            //     lblImportStatus.setText(result);
            // } else {
            //     lblImportStatus.setText(result);
            //     loadData();
            // }

            lblImportStatus.setText("Đã chọn: " + file.getName() + " (chưa import)");
        } catch (Exception ex) {
            lblImportStatus.setStyle("-fx-text-fill: #c62828;");
            lblImportStatus.setText("Lỗi đọc file: " + ex.getMessage());
        }
    }

    public ObservableList<ToHopRow> getAllData() { return allData; }

    public static class ToHopRow {
        private int id;
        private String maToHop, tenToHop, mon1, mon2, mon3;

        public ToHopRow(int id, String maToHop, String tenToHop, String mon1, String mon2, String mon3) {
            this.id=id; this.maToHop=maToHop; this.tenToHop=tenToHop;
            this.mon1=mon1; this.mon2=mon2; this.mon3=mon3;
        }

        public int    getId()       { return id; }
        public String getMaToHop()  { return maToHop; }
        public String getTenToHop() { return tenToHop; }
        public String getMon1()     { return mon1; }
        public String getMon2()     { return mon2; }
        public String getMon3()     { return mon3; }
        public void setTenToHop(String v) { tenToHop=v; }
        public void setMon1(String v)     { mon1=v; }
        public void setMon2(String v)     { mon2=v; }
        public void setMon3(String v)     { mon3=v; }
    }
}