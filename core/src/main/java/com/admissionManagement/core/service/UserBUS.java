package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.UserDAO;
import com.admissionManagement.core.dto.UserDTO;
import com.admissionManagement.core.entity.User;
import com.admissionManagement.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class UserBUS {

    private final UserDAO dao;
    private final SessionFactory factory;

    public UserBUS() {
        this.dao = new UserDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    // ── Chuyển entity → DTO ──────────────────────────
    private UserDTO toDTO(User entity) {
        return new UserDTO(
                entity.getId(),
                entity.getUsername(),
                entity.getHoTen(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.getStatus()
        );
    }

    // ── Login: trả về UserDTO nếu đúng, null nếu sai ─
    public UserDTO login(String username, String password) {
        try (Session session = factory.openSession()) {
            User user = dao.getByUsernameWithSession(session, username);
            if (user == null) return null;
            if (!user.getPassword().equals(password)) return null;
            return toDTO(user);
        }
    }

    // ── Lấy tất cả ──────────────────────────────────
    public List<UserDTO> getAllUsers(int pageIndex, int pageSize) {
        try (Session session = factory.openSession()) {
            return dao.getAllWithSession(session, pageIndex, pageSize).stream().map(this::toDTO).toList();
        }
    }

    // ── Lấy theo ID ─────────────────────────────────
    public UserDTO getUser(int id) {
        try (Session session = factory.openSession()) {
            User user = dao.getWithSession(session, id);
            return user == null ? null : toDTO(user);
        }
    }

    // ── Thêm mới ────────────────────────────────────
    public String addUser(UserDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty())
            return "Lỗi: Username không được để trống";
        if (dto.getPassword() == null || dto.getPassword().length() < 6)
            return "Lỗi: Mật khẩu tối thiểu 6 ký tự";

        Transaction tx = null;
        try (Session session = factory.openSession()) {
            // Kiểm tra username trùng
            if (dao.getByUsernameWithSession(session, dto.getUsername()) != null)
                return "Lỗi: Username đã tồn tại";

            tx = session.beginTransaction();
            User entity = new User();
            entity.setUsername(dto.getUsername().trim());
            entity.setHoTen(dto.getHoTen());
            entity.setEmail(dto.getEmail());
            entity.setPassword(dto.getPassword());
            entity.setRole(dto.getRole() != null ? dto.getRole() : "User");
            entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "Hoạt động");

            dao.addWithSession(session, entity);
            tx.commit();
            return "Thêm người dùng thành công!";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi: " + e.getMessage();
        }
    }

    // ── Sửa ─────────────────────────────────────────
    public String updateUser(int id, UserDTO dto) {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            User entity = dao.getWithSession(session, id);
            if (entity == null) return "Lỗi: Không tìm thấy người dùng";

            entity.setHoTen(dto.getHoTen());
            entity.setEmail(dto.getEmail());
            entity.setRole(dto.getRole());
            entity.setStatus(dto.getStatus());
            // Chỉ đổi password nếu có truyền vào
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                entity.setPassword(dto.getPassword());
            }

            dao.updateWithSession(session, entity);
            tx.commit();
            return "Cập nhật thành công!";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi: " + e.getMessage();
        }
    }

    // ── Khóa / Mở khóa ──────────────────────────────
    public String setStatus(int id, String newStatus) {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            User entity = dao.getWithSession(session, id);
            if (entity == null) return "Lỗi: Không tìm thấy người dùng";

            entity.setStatus(newStatus);
            dao.updateWithSession(session, entity);
            tx.commit();
            return "Cập nhật trạng thái thành công!";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi: " + e.getMessage();
        }
    }

    // ── Xóa ─────────────────────────────────────────
    public String deleteUser(int id) {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            User entity = dao.getWithSession(session, id);
            if (entity == null) return "Lỗi: Không tìm thấy người dùng";

            dao.deleteWithSession(session, entity);
            tx.commit();
            return "Xóa thành công!";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi: " + e.getMessage();
        }
    }
}