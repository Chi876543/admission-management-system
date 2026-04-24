package com.admissionManagement.desktop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * com.admissionManagement.desktop.controllers.MainController — điều phối sidebar và content area.
 *
 * Nguyên lý: mỗi lần click nav item, load FXML tương ứng
 * vào contentArea (StackPane). Cache lại sau lần đầu load
 * để tránh load lại nhiều lần.
 */
public class MainController implements Initializable {

    // ── Sidebar buttons ──────────────────────────────
    @FXML private Button btnUsers;
    @FXML private Button btnThiSinh;
    @FXML private Button btnNganh;
    @FXML private Button btnToHop;
    @FXML private Button btnNganhToHop;
    @FXML private Button btnDiem;
    @FXML private Button btnDiemCong;
    @FXML private Button btnNguyenVong;
    @FXML private Button btnQuyDoi;

    // ── Topbar ───────────────────────────────────────
    @FXML private Label lblPageTitle;
    @FXML private Label lblPageSub;
    @FXML private Label lblAdminName;
    @FXML private Label lblUserBadge;

    // ── Content area ─────────────────────────────────
    @FXML private StackPane contentArea;

    // ── State ────────────────────────────────────────
    private Button activeNavBtn;
    private final Map<String, Node> screenCache = new HashMap<>();

    // ── Màn hình & metadata ──────────────────────────
    private static final String[][] SCREENS = {
        // { fxml-key, fxml-path, title, subtitle }
        {"users",       "admin/user-view.fxml",         "Quản lý người dùng",         "Danh sách tài khoản hệ thống"},
        {"thisinh",     "admin/thisinh-view.fxml",      "Quản lý thí sinh",            "Import · Xem · Tìm kiếm · Sửa"},
        {"nganh",       "admin/nganh-view.fxml",        "Quản lý ngành tuyển sinh",    "Thêm, sửa, xóa, import ngành"},
        {"tohop",       "admin/tohop-view.fxml",        "Quản lý tổ hợp môn",          "Thêm, sửa, xóa tổ hợp"},
        {"nganhtohop",  "admin/nganh-tohop-view.fxml",  "Ngành - Tổ hợp",              "Gán tổ hợp xét tuyển cho từng ngành"},
        {"diem",        "admin/diem-view.fxml",         "Quản lý điểm thí sinh",       "THPT · VSAT · ĐGNL · Thống kê"},
        {"diemcong",    "admin/diemcong-view.fxml",     "Quản lý điểm cộng",           "Ưu tiên đối tượng, khu vực"},
        {"nguyenvong",  "admin/nguyenvong-view.fxml",   "Nguyện vọng & xét tuyển",     "Chạy xét tuyển · Xem kết quả"},
        {"quydoi",      "admin/quydoi-view.fxml",       "Bảng quy đổi điểm",           "Quy đổi VSAT / ĐGNL sang thang 30"},
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set user info (sau BE sẽ lấy từ session)
        String currentUser = "admin";
        lblAdminName.setText(currentUser);
        lblUserBadge.setText("● " + currentUser);

        // Load màn hình mặc định: Người dùng
        loadScreen("users", btnUsers);
    }

    // ── Nav handlers ─────────────────────────────────
    @FXML private void onNavUsers()       { loadScreen("users",       btnUsers);       }
    @FXML private void onNavThiSinh()     { loadScreen("thisinh",     btnThiSinh);     }
    @FXML private void onNavNganh()       { loadScreen("nganh",       btnNganh);       }
    @FXML private void onNavToHop()       { loadScreen("tohop",       btnToHop);       }
    @FXML private void onNavNganhToHop()  { loadScreen("nganhtohop",  btnNganhToHop);  }
    @FXML private void onNavDiem()        { loadScreen("diem",        btnDiem);        }
    @FXML private void onNavDiemCong()    { loadScreen("diemcong",    btnDiemCong);    }
    @FXML private void onNavNguyenVong()  { loadScreen("nguyenvong",  btnNguyenVong);  }
    @FXML private void onNavQuyDoi()      { loadScreen("quydoi",      btnQuyDoi);      }

    @FXML private void onLogout() {
        // TODO: Xóa session, quay về login.fxml
        // SceneManager.getInstance().switchToLogin();
        System.out.println("Logout clicked");
    }

    // ── Core: load & switch screen ───────────────────
    private void loadScreen(String key, Button navBtn) {
        // Cập nhật active style cho sidebar
        if (activeNavBtn != null) {
            activeNavBtn.getStyleClass().remove("nav-item-active");
        }
        navBtn.getStyleClass().add("nav-item-active");
        activeNavBtn = navBtn;

        // Cập nhật topbar title
        for (String[] screen : SCREENS) {
            if (screen[0].equals(key)) {
                lblPageTitle.setText(screen[2]);
                lblPageSub.setText(screen[3]);
                break;
            }
        }

        // Load từ cache hoặc load mới
        Node screenNode = screenCache.get(key);
        if (screenNode == null) {
            screenNode = loadFXML(key);
            if (screenNode == null) return;
            screenCache.put(key, screenNode);
        }

        // Hiển thị
        contentArea.getChildren().setAll(screenNode);
    }

    private Node loadFXML(String key) {
        // Tìm path theo key
        String path = null;
        for (String[] screen : SCREENS) {
            if (screen[0].equals(key)) {
                path = screen[1];
                break;
            }
        }
        if (path == null) return null;

        try {
            URL fxmlUrl = getClass().getResource("/com/admissionManagement/desktop/views/" + path);
            if (fxmlUrl == null) {
                System.err.println("[com.admissionManagement.desktop.controllers.MainController] Không tìm thấy FXML: " + path);
                return null;
            }
            return FXMLLoader.load(fxmlUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
