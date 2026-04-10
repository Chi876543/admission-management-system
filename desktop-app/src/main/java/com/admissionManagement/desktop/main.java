package com.admissionManagement.desktop;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class main extends Application {

    private int count = 0; // Biến lưu trữ số đếm

    @Override
    public void start(Stage primaryStage) {
        // 1. Tạo các thành phần giao diện (Controls)
        Label titleLabel = new Label("Máy Đếm Số");
        titleLabel.setFont(new Font("Arial", 24)); // Chỉnh cỡ chữ cho tiêu đề

        Label countLabel = new Label("0");
        countLabel.setFont(new Font("Arial", 80)); // Chỉnh cỡ chữ thật to cho số đếm

        Button incrementButton = new Button("Tăng Số Đếm");
        incrementButton.setPrefWidth(150); // Chỉnh chiều rộng cho nút bấm

        // 2. Xử lý sự kiện khi bấm nút (Event Handling)
        incrementButton.setOnAction(event -> {
            count++; // Tăng biến đếm lên 1
            countLabel.setText(String.valueOf(count)); // Cập nhật số hiển thị trên giao diện
        });

        // 3. Tạo layout chứa các thành phần và căn giữa chúng
        VBox root = new VBox(20); // VBox xếp các phần tử theo chiều dọc, cách nhau 20px
        root.setAlignment(Pos.CENTER); // Căn giữa tất cả các phần tử trong VBox
        root.getChildren().addAll(titleLabel, countLabel, incrementButton);

        // 4. Tạo Scene (Cảnh) và Stage (Sân khấu)
        Scene scene = new Scene(root, 400, 300); // Tạo cảnh với kích thước 400x300px
        primaryStage.setTitle("Ứng dụng JavaFX Đầu Tiên"); // Đặt tiêu đề cho cửa sổ
        primaryStage.setScene(scene); // Gắn cảnh vào sân khấu
        primaryStage.show(); // Hiển thị sân khấu
    }

    public static void main(String[] args) {
        launch(args); // Lệnh khởi chạy ứng dụng JavaFX
    }
}
