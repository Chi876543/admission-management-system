package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NganhToHopDTO;
import com.admissionManagement.core.dto.ToHopMonThiDTO;
import com.admissionManagement.core.service.NganhBUS;
import com.admissionManagement.core.service.NganhToHopBUS;
import com.admissionManagement.core.service.ToHopMonThiBUS;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class NganhToHopController extends BaseController implements Initializable {

    private static final Map<String, String> MON_COT = Map.of(
            "TO","toan", "LI","ly", "HO","hoa", "SI","sinh",
            "VA","van",  "SU","su", "DI","dia", "TI","tin",
            "N1","anh",  "KTPL","ktpl"
    );

    // ── Panel trái ────────────────────────────────────
    @FXML private TableView<NganhDTO>          tblNganh;
    @FXML private TableColumn<NganhDTO,String> colMaNganh, colTenNganh;
    @FXML private TextField                    tfSearchNganh;

    // ── Panel phải ────────────────────────────────────
    @FXML private Label                         lblSelectedNganh, lblError, lblImportStatus;
    @FXML private Label                         lblFormMode; // "Gán mới" hoặc "Đang sửa"
    @FXML private ComboBox<ToHopMonThiDTO>      cbToHopGan;
    @FXML private TextField                     tfHsMon1, tfHsMon2, tfHsMon3;
    @FXML private Button                        btnGan; // text đổi giữa "+ Gán" và "💾 Lưu"
    @FXML private Button                        btnHuyEdit; // ẩn khi không sửa
    @FXML private TableView<NganhToHopDTO>      tblToHopCuaNganh;
    @FXML private TableColumn<NganhToHopDTO,String> colMaTH, colTenTH, colMon1, colMon2, colMon3;
    @FXML private TableColumn<NganhToHopDTO,String> colHs1, colHs2, colHs3;
    @FXML private TableColumn<NganhToHopDTO,Void>   colXoa, colSua;

    private final NganhBUS       nganhBUS      = new NganhBUS();
    private final ToHopMonThiBUS toHopBUS      = new ToHopMonThiBUS();
    private final NganhToHopBUS  nganhToHopBUS = new NganhToHopBUS();

    private final ObservableList<NganhDTO>       danhSachNganh = FXCollections.observableArrayList();
    private final ObservableList<ToHopMonThiDTO> danhSachToHop = FXCollections.observableArrayList();
    private final ObservableList<NganhToHopDTO>  toHopCuaNganh = FXCollections.observableArrayList();

    private NganhDTO selectedNganh;
    private NganhToHopDTO editingRow = null; // null = đang gán mới, non-null = đang sửa

    @Override
    public void initialize(URL u, ResourceBundle r) {
        setupNganhTable();
        setupToHopTable();
        setupComboBox();
        loadAllData();
    }

    // ── Setup ─────────────────────────────────────────
    private void setupNganhTable() {
        colMaNganh.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMaNganh()));
        colTenNganh.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTenNganh()));
        tblNganh.setItems(danhSachNganh);
    }

    private void setupToHopTable() {
        colMaTH.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMaToHop()));
        colTenTH.setCellValueFactory(d -> {
            String ma = d.getValue().getMaToHop();
            return new SimpleStringProperty(
                    danhSachToHop.stream().filter(t -> t.getMaToHop().equals(ma))
                            .findFirst().map(ToHopMonThiDTO::getTenToHop).orElse(ma)
            );
        });
        colMon1.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getThMon1()));
        colMon2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getThMon2()));
        colMon3.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getThMon3()));
        colHs1.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getHsMon1() != null ? d.getValue().getHsMon1().toString() : "1"));
        colHs2.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getHsMon2() != null ? d.getValue().getHsMon2().toString() : "1"));
        colHs3.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getHsMon3() != null ? d.getValue().getHsMon3().toString() : "1"));

        colXoa.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Gỡ");
            {
                btn.getStyleClass().addAll("btn-danger", "btn-sm");
                btn.setOnAction(e -> onGoToHop(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : btn);
            }
        });

        // Cột Sửa
        colSua.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Sửa");
            {
                btn.getStyleClass().addAll("btn-default", "btn-sm");
                btn.setOnAction(e -> loadToFormEdit(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : btn);
            }
        });

        // Double click cũng mở sửa
        tblToHopCuaNganh.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                NganhToHopDTO row = tblToHopCuaNganh.getSelectionModel().getSelectedItem();
                if (row != null) loadToFormEdit(row);
            }
        });

        tblToHopCuaNganh.setItems(toHopCuaNganh);
    }

    private void setupComboBox() {
        cbToHopGan.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(ToHopMonThiDTO dto) {
                return dto == null ? "" : dto.getMaToHop() + " - " + dto.getTenToHop();
            }
            @Override public ToHopMonThiDTO fromString(String s) { return null; }
        });
        cbToHopGan.setOnAction(e -> {
            if (cbToHopGan.getValue() != null) {
                tfHsMon1.setText("1"); tfHsMon2.setText("1"); tfHsMon3.setText("1");
            }
        });
    }

    // ── Load ──────────────────────────────────────────
    public void loadAllData() {
        danhSachNganh.setAll(nganhBUS.getAllNganh());
        danhSachToHop.setAll(toHopBUS.getAllToHopMonThi());
        cbToHopGan.setItems(danhSachToHop);
        if (selectedNganh != null) {
            selectedNganh = danhSachNganh.stream()
                    .filter(n -> n.getMaNganh().equals(selectedNganh.getMaNganh()))
                    .findFirst().orElse(null);
            loadToHopCuaNganh();
        }
    }

    private void loadToHopCuaNganh() {
        if (selectedNganh == null) { toHopCuaNganh.clear(); return; }
        List<NganhToHopDTO> list = nganhToHopBUS.getAllNganhToHop().stream()
                .filter(dto -> dto.getMaNganh().equals(selectedNganh.getMaNganh()))
                .collect(Collectors.toList());
        toHopCuaNganh.setAll(list);
    }

    @FXML private void onSearchNganh() {
        String kw = tfSearchNganh.getText().trim().toLowerCase();
        List<NganhDTO> f = nganhBUS.getAllNganh().stream()
                .filter(n -> kw.isEmpty()
                        || n.getMaNganh().toLowerCase().contains(kw)
                        || n.getTenNganh().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        danhSachNganh.setAll(f);
    }

    @FXML private void onNganhClick(MouseEvent e) {
        NganhDTO row = tblNganh.getSelectionModel().getSelectedItem();
        if (row == null) return;
        selectedNganh = row;
        lblSelectedNganh.setText("Tổ hợp của ngành: " + row.getTenNganh());
        loadToHopCuaNganh();
        lblError.setText("");
    }

    // ── Load dữ liệu hàng vào form để sửa ───────────
    private void loadToFormEdit(NganhToHopDTO row) {
        editingRow = row;

        // Tìm ToHopMonThiDTO tương ứng để set vào ComboBox
        ToHopMonThiDTO th = danhSachToHop.stream()
                .filter(t -> t.getMaToHop().equals(row.getMaToHop()))
                .findFirst().orElse(null);
        cbToHopGan.setValue(th);
        cbToHopGan.setDisable(true); // Không cho đổi tổ hợp khi sửa — chỉ sửa hệ số

        tfHsMon1.setText(row.getHsMon1() != null ? row.getHsMon1().toString() : "1");
        tfHsMon2.setText(row.getHsMon2() != null ? row.getHsMon2().toString() : "1");
        tfHsMon3.setText(row.getHsMon3() != null ? row.getHsMon3().toString() : "1");

        lblFormMode.setText("✏ Đang sửa: " + row.getMaToHop());
        lblFormMode.setStyle("-fx-text-fill: #e65100; -fx-font-weight: bold;");
        btnGan.setText("💾 Lưu sửa");
        btnGan.getStyleClass().removeAll("btn-success");
        btnGan.getStyleClass().add("btn-primary");
        btnHuyEdit.setVisible(true);
        btnHuyEdit.setManaged(true);
        lblError.setText("");
    }

    // ── Hủy sửa, về chế độ gán mới ─────────────────
    @FXML private void onHuyEdit() {
        editingRow = null;
        cbToHopGan.setValue(null);
        cbToHopGan.setDisable(false);
        tfHsMon1.clear(); tfHsMon2.clear(); tfHsMon3.clear();
        lblFormMode.setText("➕ Gán tổ hợp mới");
        lblFormMode.setStyle("-fx-text-fill: #1a2340; -fx-font-weight: bold;");
        btnGan.setText("+ Gán");
        btnGan.getStyleClass().removeAll("btn-primary");
        btnGan.getStyleClass().add("btn-success");
        btnHuyEdit.setVisible(false);
        btnHuyEdit.setManaged(false);
        lblError.setText("");
    }

    // ── Gán tổ hợp (hoặc lưu sửa) ───────────────────
    @FXML private void onGanToHop() {
        if (editingRow != null) {
            onLuuSua(); // Đang sửa → lưu
        } else {
            onGanMoi(); // Chưa có editing → gán mới
        }
    }

    private void onGanMoi() {
        if (selectedNganh == null) { lblError.setText("Vui lòng chọn ngành trước."); return; }
        ToHopMonThiDTO th = cbToHopGan.getValue();
        if (th == null) { lblError.setText("Vui lòng chọn tổ hợp."); return; }

        boolean exists = toHopCuaNganh.stream()
                .anyMatch(dto -> dto.getMaToHop().equals(th.getMaToHop()));
        if (exists) { lblError.setText("Tổ hợp này đã được gán cho ngành."); return; }

        byte hs1, hs2, hs3;
        try {
            hs1 = Byte.parseByte(tfHsMon1.getText().trim().isEmpty() ? "1" : tfHsMon1.getText().trim());
            hs2 = Byte.parseByte(tfHsMon2.getText().trim().isEmpty() ? "1" : tfHsMon2.getText().trim());
            hs3 = Byte.parseByte(tfHsMon3.getText().trim().isEmpty() ? "1" : tfHsMon3.getText().trim());
        } catch (NumberFormatException ex) {
            lblError.setText("Hệ số môn phải là số nguyên (1 hoặc 2)."); return;
        }

        boolean[] flags = buildMonFlags(th.getMon1(), th.getMon2(), th.getMon3());

        NganhToHopDTO dto = new NganhToHopDTO(
                0,
                selectedNganh.getMaNganh(), th.getMaToHop(),
                th.getMon1(), hs1,
                th.getMon2(), hs2,
                th.getMon3(), hs3,
                selectedNganh.getMaNganh() + "_" + th.getMaToHop(),
                flags[0], flags[1], flags[2], flags[3], flags[4],
                flags[5], flags[6], flags[7], flags[8], flags[9],
                flags[10], flags[11], flags[12], flags[13]
                , flags[14], flags[15], flags[16], flags[17]
                , flags[18], BigDecimal.ZERO
        );

        String result = nganhToHopBUS.addNganhToHop(dto);
        if (result.startsWith("Lỗi")) { lblError.setText(result); return; }
        lblError.setText("");
        cbToHopGan.setValue(null);
        tfHsMon1.clear(); tfHsMon2.clear(); tfHsMon3.clear();
        loadToHopCuaNganh();
    }

    private void onLuuSua() {
        byte hs1, hs2, hs3;
        try {
            hs1 = Byte.parseByte(tfHsMon1.getText().trim().isEmpty() ? "1" : tfHsMon1.getText().trim());
            hs2 = Byte.parseByte(tfHsMon2.getText().trim().isEmpty() ? "1" : tfHsMon2.getText().trim());
            hs3 = Byte.parseByte(tfHsMon3.getText().trim().isEmpty() ? "1" : tfHsMon3.getText().trim());
        } catch (NumberFormatException ex) {
            lblError.setText("Hệ số môn phải là số nguyên (1 hoặc 2)."); return;
        }

        // Giữ nguyên tất cả field, chỉ cập nhật hệ số 3 môn
        NganhToHopDTO dto = new NganhToHopDTO(
                editingRow.getId(),
                editingRow.getMaNganh(), editingRow.getMaToHop(),
                editingRow.getThMon1(), hs1,
                editingRow.getThMon2(), hs2,
                editingRow.getThMon3(), hs3,
                editingRow.getTbKeys(),
                editingRow.getAnh(),  editingRow.getToan(), editingRow.getLy(),
                editingRow.getHoa(),  editingRow.getSinh(), editingRow.getVan(),
                editingRow.getSu(),   editingRow.getDia(),  editingRow.getTin(),
                editingRow.getNk1(),  editingRow.getNk2(),  editingRow.getNk3(),
                editingRow.getNk4(),  editingRow.getNk5(),  editingRow.getNk6(),
                editingRow.getCncn(), editingRow.getCnnn(),
                editingRow.getKhac(), editingRow.getKtpl(),
                editingRow.getDoLech()
        );

        String result = nganhToHopBUS.updateNganhToHop(editingRow.getId(), dto);
        if (result.startsWith("Lỗi")) { lblError.setText(result); return; }

        onHuyEdit();
        loadToHopCuaNganh();
    }

    private boolean[] buildMonFlags(String m1, String m2, String m3) {
        Set<String> monSet = new HashSet<>(List.of(m1, m2, m3));
        Set<String> known = Set.of("TO", "LI", "HO", "SI", "VA", "SU", "DI", "TI",
                "N1", "KTPL", "CNCN", "CNNN", "NK1", "NK2", "NK3", "NK4",
                "NK5", "NK6");
        boolean hasKhac = monSet.stream().anyMatch(m -> !known.contains(m));
        return new boolean[]{
                monSet.contains("N1"),
                monSet.contains("TO"),
                monSet.contains("LI"),
                monSet.contains("HO"),
                monSet.contains("SI"),
                monSet.contains("VA"),
                monSet.contains("SU"),
                monSet.contains("DI"),
                monSet.contains("TI"),
                monSet.contains("NK1"),
                monSet.contains("NK2"),
                monSet.contains("NK3"),
                monSet.contains("NK4"),
                monSet.contains("NK5"),
                monSet.contains("NK6"),
                monSet.contains("CNCN"),
                monSet.contains("CNNN"),
                hasKhac,
                monSet.contains("KTPL")
        };
    }

    // ── Gỡ tổ hợp ────────────────────────────────────
    private void onGoToHop(NganhToHopDTO row) {
        if (!confirmDelete("gỡ tổ hợp " + row.getMaToHop())) return;
        String result = nganhToHopBUS.deleteNganhToHop(row.getId());
        if (result.startsWith("Lỗi")) {
            showError(result);
        } else {
            loadToHopCuaNganh();
        }
    }

    // ── Import CSV ────────────────────────────────────────────────────────────
    @FXML private void onImportCSV() {
        lblImportStatus.setText("");
        lblImportStatus.setStyle("-fx-text-fill: #2e7d32;");

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn file CSV ngành - tổ hợp");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showOpenDialog(tblNganh.getScene().getWindow());
        if (file == null) return;

        try {
            String result = nganhToHopBUS.importCsvData(file);
            if (result.startsWith("Lỗi")) {
                lblImportStatus.setStyle("-fx-text-fill: #c62828;");
                lblImportStatus.setText(result);
            } else {
                lblImportStatus.setText(result);
                loadAllData();
            }
        } catch (Exception ex) {
            lblImportStatus.setStyle("-fx-text-fill: #c62828;");
            lblImportStatus.setText("Lỗi đọc file: " + ex.getMessage());
        }
    }
}