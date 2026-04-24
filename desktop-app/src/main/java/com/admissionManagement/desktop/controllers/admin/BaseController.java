package com.admissionManagement.desktop.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BaseController — chứa các helper dùng chung cho tất cả màn hình Admin.
 * Các Controller cụ thể extend class này.
 */
public abstract class BaseController {

    protected static final int PAGE_SIZE = 20;

    // ── Alert helpers ────────────────────────────────
    protected void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }

    protected void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle("Lỗi"); a.setHeaderText(null); a.showAndWait();
    }

    protected boolean confirmDelete(String itemName) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa \"" + itemName + "\"?",
                ButtonType.YES, ButtonType.NO);
        a.setTitle("Xác nhận xóa"); a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    // ── File chooser CSV ────────────────────────────
    protected File chooseCSV(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn file CSV");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        return fc.showOpenDialog(stage);
    }

    // ── Badge label factory ──────────────────────────
    protected Label makeBadge(String text, String styleClass) {
        Label lbl = new Label(text);
        lbl.getStyleClass().addAll("badge", styleClass);
        return lbl;
    }

    // ── Pagination helper ────────────────────────────
    protected <T> ObservableList<T> getPage(List<T> data, int pageIndex) {
        int from = pageIndex * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, data.size());
        if (from >= data.size()) return FXCollections.observableArrayList();
        return FXCollections.observableArrayList(data.subList(from, to));
    }

    protected int pageCount(int totalItems) {
        return Math.max(1, (int) Math.ceil((double) totalItems / PAGE_SIZE));
    }

    // ── Action cell helper ───────────────────────────
    /**
     * Tạo HBox chứa 2 button Sửa + Xóa dùng chung.
     * Truyền vào Runnable onEdit và onDelete.
     */
    protected javafx.scene.layout.HBox makeActionCell(Runnable onEdit, Runnable onDelete) {
        Button btnEdit = new Button("Sửa");
        Button btnDel  = new Button("Xóa");
        btnEdit.getStyleClass().addAll("btn-default", "btn-sm");
        btnDel.getStyleClass().addAll("btn-danger",   "btn-sm");
        btnEdit.setOnAction(e -> onEdit.run());
        btnDel.setOnAction(e  -> onDelete.run());
        javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, btnEdit, btnDel);
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
