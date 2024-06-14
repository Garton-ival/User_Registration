package org.pahappa.systems.registrationapp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.pahappa.systems.registrationapp.config.SessionConfiguration;
import org.pahappa.systems.registrationapp.models.User;
import org.pahappa.systems.registrationapp.exception.UserRegistrationException;

import java.util.List;
import java.util.Optional;

public class UserDAO {

    private final SessionFactory sessionFactory;

    public UserDAO() {
        sessionFactory = SessionConfiguration.getSessionFactory();
    }

    public void save(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Check if username already exists
            if (getByUsername(user.getUsername()).isEmpty()) {
                session.save(user);
                transaction.commit();
            } else {
                throw new UserRegistrationException("Username " + user.getUsername() + " already exists.");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new UserRegistrationException("Failed to save user: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public void update(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new UserRegistrationException("Failed to update user: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public void delete(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new UserRegistrationException("Failed to delete user: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public Optional<User> getByUsername(String username) {
        Session session = sessionFactory.openSession();
        try {
            Query query = session.createQuery("FROM User WHERE username = :username");
            query.setParameter("username", username);
            User user = (User) query.uniqueResult();
            return Optional.ofNullable(user);
        } finally {
            session.close();
        }
    }

    public List<User> findAll() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM User").list();
        } finally {
            session.close();
        }
    }

}
