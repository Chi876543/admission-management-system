module desktop.app {
    requires javafx.controls;
    requires javafx.fxml;
    // 1. Cho phép JavaFX FXML truy cập vào package để gán sự kiện giao diện
    opens com.admissionManagement.desktop to javafx.fxml;

    // 2. Cho phép lõi JavaFX (javafx.graphics) truy cập để khởi chạy ứng dụng
    exports com.admissionManagement.desktop;
}