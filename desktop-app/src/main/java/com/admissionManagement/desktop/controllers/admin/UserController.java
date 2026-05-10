package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.UserDTO;
import com.admissionManagement.core.service.UserBUS;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserController implements Initializable {

    // ── FXML bindings (list view) ──────────────────────
    @FXML private TextField        tfSearch;
    @FXML private ComboBox<String> cbRole;
    @FXML private ComboBox<String> cbStatus;

    @FXML private TableView<UserRow>            tblUsers;
    @FXML private TableColumn<UserRow, String>  colStt;
    @FXML private TableColumn<UserRow, String>  colUsername;
    @FXML private TableColumn<UserRow, String>  colHoTen;
    @FXML private TableColumn<UserRow, String>  colEmail;
    @FXML private TableColumn<UserRow, String>  colRole;
    @FXML private TableColumn<UserRow, String>  colStatus;
    @FXML private TableColumn<UserRow, Void>    colAction;

    @FXML private Label      lblRecordCount;
    @FXML private Pagination pagination;

    // ── FXML bindings (dialog) ─────────────────────────
    @FXML private Label            lblDialogTitle;
    @FXML private TextField        tfUsername;
    @FXML private TextField        tfHoTen;
    @FXML private TextField        tfEmail;
    @FXML private PasswordField    tfPassword;
    @FXML private VBox             vboxPassword;
    @FXML private ComboBox<String> cbRoleDialog;
    @FXML private ComboBox<String> cbStatusDialog;
    @FXML private Label            lblError;

    // ── State ──────────────────────────────────────────
    private ObservableList<UserRow> allData  = FXCollections.observableArrayList();
    private ObservableList<UserRow> filtered = FXCollections.observableArrayList();

    private final UserBUS userBUS   = new UserBUS();
    private static final int PAGE_SIZE = 20;
    private int currentPage = 0;

    private UserRow   editingRow       = null;
    private Stage     dialogStage      = null;
    private UserController parentController = null;

    // ── Initialize ────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (tblUsers != null) {
            setupComboBoxes();
            setupTable();
            loadData();
        }
    }

    private void setupComboBoxes() {
        // Thêm "Tất cả" là item đầu tiên — chọn nó = bỏ filter
        cbRole.getItems().clear();
        cbRole.getItems().addAll("Tất cả", "Admin", "User");
        cbRole.setValue("Tất cả");

        cbStatus.getItems().clear();
        cbStatus.getItems().addAll("Tất cả", "Hoạt động", "Đã khóa");
        cbStatus.setValue("Tất cả");

        cbRole.setOnAction(e -> applyFilter());
        cbStatus.setOnAction(e -> applyFilter());
    }

    private void setupTable() {
        colStt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStt()));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            { badge.getStyleClass().add("badge"); setAlignment(Pos.CENTER); setGraphic(badge); }
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) { badge.setVisible(false); return; }
                badge.setVisible(true);
                badge.setText(role);
                badge.getStyleClass().removeAll("badge-amber", "badge-blue");
                badge.getStyleClass().add(role.equals("Admin") ? "badge-amber" : "badge-blue");
            }
        });

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            { badge.getStyleClass().add("badge"); setAlignment(Pos.CENTER); setGraphic(badge); }
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { badge.setVisible(false); return; }
                badge.setVisible(true);
                badge.setText(status);
                badge.getStyleClass().removeAll("badge-green", "badge-red");
                badge.getStyleClass().add(status.equals("Hoạt động") ? "badge-green" : "badge-red");
            }
        });

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("Sửa");
            private final Button btnToggle = new Button("Khóa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox   box       = new HBox(6, btnEdit, btnToggle, btnDelete);
            {
                box.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().addAll("btn-default", "btn-sm");
                btnToggle.getStyleClass().addAll("btn-warning", "btn-sm");
                btnDelete.getStyleClass().addAll("btn-danger", "btn-sm");

                btnEdit.setOnAction(e -> openDialog(getTableView().getItems().get(getIndex())));
                btnToggle.setOnAction(e -> toggleStatus(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> confirmDelete(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                UserRow row = getTableView().getItems().get(getIndex());
                boolean locked = "Đã khóa".equals(row.getStatus());
                btnToggle.setText(locked ? "Mở khóa" : "Khóa");
                btnToggle.getStyleClass().removeAll("btn-warning", "btn-success");
                btnToggle.getStyleClass().add(locked ? "btn-success" : "btn-warning");
                setGraphic(box);
            }
        });

        pagination.currentPageIndexProperty().addListener((obs, o, n) -> {
            currentPage = n.intValue();
            showPage();
        });
    }

    // ── Data ──────────────────────────────────────────
    private void loadData() {
        List<UserDTO> users = userBUS.getAllUsers();
        List<UserRow> rows = new ArrayList<>();
        for (int i = users.size() - 1; i >= 0; i--) {
            UserDTO u = users.get(i);
            rows.add(new UserRow(
                    String.valueOf(i + 1), u.getId(),
                    u.getUsername(),
                    u.getHoTen()  != null ? u.getHoTen()  : "",
                    u.getEmail()  != null ? u.getEmail()   : "",
                    u.getRole()   != null ? u.getRole()    : "User",
                    u.getStatus() != null ? u.getStatus()  : "Hoạt động"
            ));
        }
        allData.setAll(rows);
        applyFilter();
    }

    private void applyFilter() {
        String keyword = tfSearch.getText().trim().toLowerCase();
        String role    = "Tất cả".equals(cbRole.getValue())   ? null : cbRole.getValue();
        String status  = "Tất cả".equals(cbStatus.getValue()) ? null : cbStatus.getValue();


        List<UserRow> result = allData.stream()
                .filter(r -> keyword.isEmpty()
                        || r.getUsername().toLowerCase().contains(keyword)
                        || r.getHoTen().toLowerCase().contains(keyword)
                        || r.getEmail().toLowerCase().contains(keyword))
                .filter(r -> role   == null || r.getRole().equals(role))
                .filter(r -> status == null || r.getStatus().equals(status))
                .collect(Collectors.toList());

        for (int i = 0; i < result.size(); i++) result.get(i).setStt(String.valueOf(i + 1));

        filtered.setAll(result);
        currentPage = 0;
        pagination.setPageCount(Math.max(1, (int) Math.ceil((double) filtered.size() / PAGE_SIZE)));
        pagination.setCurrentPageIndex(0);
        showPage();
        tblUsers.refresh();
    }

    private void showPage() {
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, filtered.size());
        tblUsers.setItems(FXCollections.observableArrayList(filtered.subList(from, Math.max(from, to))));
        lblRecordCount.setText(filtered.size() + " bản ghi");
    }

    // ── Handlers ──────────────────────────────────────
    @FXML private void onSearch()      { applyFilter(); }
    @FXML private void onAdd()         { openDialog(null); }
    @FXML private void onClearFilter() { tfSearch.clear(); cbRole.setValue(null); cbStatus.setValue(null); applyFilter(); }

    private void confirmDelete(UserRow row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa người dùng: " + row.getUsername());
        alert.setContentText("Bạn có chắc muốn xóa tài khoản này không?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String msg = userBUS.deleteUser(row.getId());
            showAlert(msg.startsWith("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION, msg);
            loadData();
        }
    }

    private void toggleStatus(UserRow row) {
        String newStatus = "Hoạt động".equals(row.getStatus()) ? "Đã khóa" : "Hoạt động";
        String msg = userBUS.setStatus(row.getId(), newStatus);
        if (msg.startsWith("Lỗi")) showAlert(Alert.AlertType.ERROR, msg);
        loadData();
    }

    private void openDialog(UserRow row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/admissionManagement/desktop/views/admin/user-dialog.fxml"));
            Parent root = loader.load();
            UserController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm người dùng" : "Sửa người dùng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            dialogCtrl.initDialog(stage, row, this);
            stage.showAndWait();
            loadData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể mở dialog: " + e.getMessage());
        }
    }

    public void initDialog(Stage stage, UserRow row, UserController parent) {
        this.dialogStage      = stage;
        this.editingRow       = row;
        this.parentController = parent;

        cbRoleDialog.setItems(FXCollections.observableArrayList("Admin", "User"));
        cbStatusDialog.setItems(FXCollections.observableArrayList("Hoạt động", "Đã khóa"));

        if (row != null) {
            lblDialogTitle.setText("Sửa người dùng");
            tfUsername.setText(row.getUsername());
            tfUsername.setDisable(true);
            tfHoTen.setText(row.getHoTen());
            tfEmail.setText(row.getEmail());
            cbRoleDialog.setValue(row.getRole());
            cbStatusDialog.setValue(row.getStatus());
            if (vboxPassword != null) { vboxPassword.setVisible(false); vboxPassword.setManaged(false); }
        } else {
            lblDialogTitle.setText("Thêm người dùng");
            cbRoleDialog.setValue("User");
            cbStatusDialog.setValue("Hoạt động");
            if (vboxPassword != null) { vboxPassword.setVisible(true); vboxPassword.setManaged(true); }
        }
    }

    @FXML private void onDialogSave() {
        if (!validateDialog()) return;

        String result;
        if (editingRow == null) {
            UserDTO dto = new UserDTO(0,
                    tfUsername.getText().trim(), tfHoTen.getText().trim(),
                    tfEmail.getText().trim(),    tfPassword.getText(),
                    cbRoleDialog.getValue(),     cbStatusDialog.getValue());
            result = userBUS.addUser(dto);
        } else {
            String newPass = (tfPassword != null && !tfPassword.getText().isEmpty())
                    ? tfPassword.getText() : null;
            UserDTO dto = new UserDTO(editingRow.getId(),
                    editingRow.getUsername(),    tfHoTen.getText().trim(),
                    tfEmail.getText().trim(),    newPass,
                    cbRoleDialog.getValue(),     cbStatusDialog.getValue());
            result = userBUS.updateUser(editingRow.getId(), dto);
        }

        if (result.startsWith("Lỗi")) { showError(result); return; }
        dialogStage.close();
    }

    @FXML private void onDialogCancel() { if (dialogStage != null) dialogStage.close(); }

    private boolean validateDialog() {
        if (lblError != null) lblError.setText("");
        if (tfUsername != null && tfUsername.getText().trim().isEmpty())
        { showError("Username không được để trống"); return false; }
        if (tfHoTen != null && tfHoTen.getText().trim().isEmpty())
        { showError("Họ tên không được để trống"); return false; }
        if (tfEmail != null && !tfEmail.getText().trim().contains("@"))
        { showError("Email không hợp lệ"); return false; }
        if (editingRow == null && tfPassword != null && tfPassword.getText().length() < 6)
        { showError("Mật khẩu tối thiểu 6 ký tự"); return false; }
        if (cbRoleDialog != null && cbRoleDialog.getValue() == null)
        { showError("Vui lòng chọn quyền"); return false; }
        return true;
    }

    private void showError(String msg) { if (lblError != null) lblError.setText(msg); }
    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    // ═══════════════════════════════════════════════════
    public static class UserRow {
        private String stt, username, hoTen, email, role, status;
        private int id;

        public UserRow(String stt, int id, String username, String hoTen,
                       String email, String role, String status) {
            this.stt = stt; this.id = id; this.username = username;
            this.hoTen = hoTen; this.email = email; this.role = role; this.status = status;
        }

        public int    getId()        { return id; }
        public String getStt()       { return stt; }
        public String getUsername()  { return username; }
        public String getHoTen()     { return hoTen; }
        public String getEmail()     { return email; }
        public String getRole()      { return role; }
        public String getStatus()    { return status; }

        public void setStt(String v)      { stt = v; }
        public void setUsername(String v) { username = v; }
        public void setHoTen(String v)    { hoTen = v; }
        public void setEmail(String v)    { email = v; }
        public void setRole(String v)     { role = v; }
        public void setStatus(String v)   { status = v; }
    }
}