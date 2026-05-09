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

    public List<User> getAllWithSession(Session session) {
        return session.createQuery("FROM User ORDER BY id ASC", User.class)
                .getResultList();
    }

    public void updateWithSession(Session session, User user) {
        session.merge(user);
    }

    public void deleteWithSession(Session session, User user) {
        session.remove(user);
    }
}
