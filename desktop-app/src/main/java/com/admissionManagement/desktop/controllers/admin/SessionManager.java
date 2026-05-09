package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.UserDTO;

/**
 * Singleton lưu thông tin user đang đăng nhập.
 * Dùng trong toàn bộ desktop-app sau khi login thành công.
 */
public class SessionManager {

    private static SessionManager instance;
    private UserDTO currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public UserDTO getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserDTO user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "Admin".equals(currentUser.getRole());
    }
}