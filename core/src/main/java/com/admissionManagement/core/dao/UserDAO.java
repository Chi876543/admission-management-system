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
        String query = "FROM User ORDER BY id ASC";

        if(pageIndex == 0 || pageSize == 0)
            return session.createQuery(query, User.class).getResultList();

        int offset = pageIndex * pageSize;
        return session.createQuery(query, User.class).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }

    public void updateWithSession(Session session, User user) {
        session.merge(user);
    }

    public long getTotalWithSession(Session session) {
        return session.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
    }

    public void deleteWithSession(Session session, User user) {
        session.remove(user);
    }
}
