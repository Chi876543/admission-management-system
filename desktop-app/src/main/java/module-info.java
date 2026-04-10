module desktop.app {
    requires javafx.controls;
    requires javafx.fxml;
    // Cấp quyền cho JavaFX khởi chạy App từ thư mục gốc
    exports com.admissionManagement.desktop;
    opens com.admissionManagement.desktop to javafx.fxml;

    // THÊM DÒNG NÀY: Cấp quyền cho FXML truy cập vào thư mục controllers để xử lý giao diện
    opens com.admissionManagement.desktop.controllers to javafx.fxml;
}