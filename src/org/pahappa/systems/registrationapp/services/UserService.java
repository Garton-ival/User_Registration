package org.pahappa.systems.registrationapp.services;

import org.pahappa.systems.registrationapp.models.User;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class UserService {
    private final List<User> users = new ArrayList<>();

    //Add a user to the list
    public boolean addUser(User user) {
        // Validate if the username already exists
        if (getUserByUsername(user.getUsername()).isPresent()) {
            return false;
        }
        users.add(user);
        return true;
    }


    // Retrieve all users
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Retrieve a user by username
    public Optional<User> getUserByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    // Update user details
    public boolean updateUser(User updatedUser) {
        Optional<User> optionalUser = getUserByUsername(updatedUser.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstname(updatedUser.getFirstname());
            user.setLastname(updatedUser.getLastname());
            user.setDateOfBirth(updatedUser.getDateOfBirth());
            return true;
        }
        return false;
    }

    // Delete a user by username
    public boolean deleteUser(String username) {
        return users.removeIf(user -> user.getUsername().equals(username));
    }

    // Delete all users
    public void deleteAllUsers() {
        users.clear();
    }
}
