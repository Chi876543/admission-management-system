package com.admissionManagement.core.dao;

import com.admissionManagement.core.entity.User;
import org.hibernate.Session;

import java.util.List;

public class UserDAO {

    public void addWithSession(Session session, User user) {
        session.persist(user);
    }

    public User getWithSession(Session session, int id) {
        return session.get(User.class, id);
    }

    public User getByUsernameWithSession(Session session, String username) {
        return session.createQuery("FROM User WHERE username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
    }

    public List<User> getAllWithSession(Session session, int pageIndex, int pageSize) {
        return getAllWithSession(session, null, pageIndex, pageSize);
    }

    public List<User> getAllWithSession(Session session, String keyword, int pageIndex, int pageSize) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = hasKeyword
                ? "FROM User WHERE LOWER(username) LIKE :kw OR LOWER(hoTen) LIKE :kw OR LOWER(email) LIKE :kw ORDER BY id ASC"
                : "FROM User ORDER BY id ASC";

        if(pageSize <= 0) {
            var q = session.createQuery(query, User.class);
            if (hasKeyword) q.setParameter("kw", "%" + keyword.trim().toLowerCase() + "%");
            return q.getResultList();
        }

        int offset = pageIndex * pageSize;
        var q = session.createQuery(query, User.class).setFirstResult(offset).setMaxResults(pageSize);
        if (hasKeyword) q.setParameter("kw", "%" + keyword.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public long getTotalWithSession(Session session, String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String query = hasKeyword
                ? "SELECT COUNT(u) FROM User u WHERE LOWER(u.username) LIKE :kw OR LOWER(u.hoTen) LIKE :kw OR LOWER(u.email) LIKE :kw"
                : "SELECT COUNT(u) FROM User u";
        var q = session.createQuery(query, Long.class);
        if (hasKeyword) q.setParameter("kw", "%" + keyword.trim().toLowerCase() + "%");
        return q.getSingleResult();
    }

    public void updateWithSession(Session session, User user) {
        session.merge(user);
    }

    public long getTotalWithSession(Session session) {
        return getTotalWithSession(session, null);
    }

    public void deleteWithSession(Session session, User user) {
        session.remove(user);
    }
}
