package com.admissionManagement.desktop.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnDashboard, btnCandidate, btnMajor, btnScore, btnUser;

    private Button currentActiveButton;

    @FXML
    public void initialize() {
        showCandidate(null);
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN CLICK MENU ---

    @FXML
    private void showDashboard(ActionEvent event) {
        // loadComponent("DashboardView.fxml");
        setActiveButton(btnDashboard);
        showTemporaryMessage("Giao diện Tổng quan đang được xây dựng...");
    }

    @FXML
    private void showCandidate(ActionEvent event) {
        // loadComponent("CandidateView.fxml");

        setActiveButton(btnCandidate);
        showTemporaryMessage("Đây là khu vực hiển thị Bảng Quản lý Thí Sinh");
    }

    @FXML
    private void showMajor(ActionEvent event) {
        // loadComponent("MajorView.fxml");
        setActiveButton(btnMajor);
        showTemporaryMessage("Đây là khu vực hiển thị Quản lý Ngành");
    }

    @FXML
    private void showScore(ActionEvent event) {
        // loadComponent("ScoreView.fxml");
        setActiveButton(btnScore);
        showTemporaryMessage("Đây là khu vực hiển thị Điểm Thi");
    }

    @FXML
    private void showUser(ActionEvent event) {
        // loadComponent("UserView.fxml");
        setActiveButton(btnUser);
        showTemporaryMessage("Đây là khu vực hiển thị Quản lý Người Dùng");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admissionManagement/desktop/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 500, 600));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // --- HÀM LÕI: TẢI GIAO DIỆN CON VÀO CHÍNH GIỮA ---
    private void loadComponent(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admissionManagement/desktop/views/" + fxmlFileName));
            Node component = loader.load();

            // Xóa nội dung cũ ở giữa và chèn nội dung mới vào
            contentArea.getChildren().setAll(component);
        } catch (IOException e) {
            System.out.println("Không tìm thấy file: " + fxmlFileName);
            showTemporaryMessage("Lỗi 404: Không tìm thấy giao diện " + fxmlFileName);
        }
    }

    private void setActiveButton(Button clickedButton) {
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("menu-btn-active");
        }
        if (clickedButton != null) {
            clickedButton.getStyleClass().add("menu-btn-active");
            currentActiveButton = clickedButton;
        }
    }

    private void showTemporaryMessage(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #95a5a6;");
        contentArea.getChildren().setAll(label);
    }
}