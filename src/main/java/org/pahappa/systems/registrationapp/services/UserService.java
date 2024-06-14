package org.pahappa.systems.registrationapp.services;

import org.pahappa.systems.registrationapp.dao.UserDAO;
import org.pahappa.systems.registrationapp.models.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    //Add a user to the list
    public boolean addUser(User user) {
        try {
            userDAO.save(user);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to add user: " + e.getMessage());
            return false;
        }
    }


    // Retrieve all users
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    // Retrieve a user by Username
    public Optional<User> getUserByUsername(String username) {
        return userDAO.getByUsername(username);
    }

    // Update user details
    public boolean updateUser(User updatedUser) {
        userDAO.update(updatedUser);
        return true;
    }

    // Delete a user by Username
    public boolean deleteUserByUsername(String username) {
        Optional<User> optionalUser = getUserByUsername(username);
        if (optionalUser.isPresent()) {
            userDAO.delete(optionalUser.get());
            return true;
        }
        return false;
    }

    // Delete all users
    public void deleteAllUsers() {
        List<User> users = getAllUsers();
        for (User user : users) {
            userDAO.delete(user);
        }
    }

}
