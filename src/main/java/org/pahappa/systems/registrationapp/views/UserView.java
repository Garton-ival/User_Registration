package org.pahappa.systems.registrationapp.views;

import org.pahappa.systems.registrationapp.models.User;
import org.pahappa.systems.registrationapp.services.UserService;
import org.pahappa.systems.registrationapp.exception.UserRegistrationException;

import java.text.ParseException;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UserView {

    private final Scanner scanner;
    private final UserService userService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public UserView(){

        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }


    public void displayMenu() {
        System.out.println("********* User Registration System *********");
        boolean running = true;

        while (running) {
            System.out.println("\nChoose an operation:");
            System.out.println("1. Register a user");
            System.out.println("2. Display all users");
            System.out.println("3. Get a user by username");
            System.out.println("4. Update user details by username");
            System.out.println("5. Delete User of username");
            System.out.println("6. Delete all users");
            System.out.println("7. Exit");
            try{
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                switch (choice) {
                    case 1:
                        registerUser();
                        break;
                    case 2:
                        displayAllUsers();
                        break;
                    case 3:
                        getUserByUsername();
                        break;
                    case 4:
                        updateUserByUsername();
                        break;
                    case 5:
                        deleteUserByUsername();
                        break;
                    case 6:
                        deleteAllUsers();
                        break;
                    case 7:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }catch (Exception e){
                System.out.println("Invalid choice. Please try again.");
                scanner.nextLine(); // Consume the newline character
            }
        }
    }

    private void registerUser() {
        try {
            // Get and validate username
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            if (username.isEmpty()) {
                throw new UserRegistrationException("Username cannot be empty.");
            }
            if (userService.getUserByUsername(username).isPresent()) {
                throw new UserRegistrationException("Username already exists");
            }
            if (username.matches("\\d+")) {
                throw new UserRegistrationException("Username cannot be a number");
            }

            // Get and validate first name
            System.out.println("Enter first name:");
            String firstname = scanner.nextLine();
            if (firstname.isEmpty() || firstname.trim().isEmpty() || firstname.matches("\\d+")) {
                throw new UserRegistrationException("First name cannot be empty or a number.");
            }

            // Get and validate last name
            System.out.println("Enter last name:");
            String lastname = scanner.nextLine();
            if (lastname.isEmpty() || lastname.trim().isEmpty() || lastname.matches("\\d+")) {
                throw new UserRegistrationException("Last name cannot be empty or a number.");
            }

            // Get and validate date of birth
            System.out.println("Enter date of birth (dd/MM/yyyy):");
            String dob;
            dob = scanner.nextLine();
            Date dateOfBirth;
            try {
                dateOfBirth = dateFormat.parse(dob);
                if (dateOfBirth.after(new Date())) {
                    throw new UserRegistrationException("Date of birth cannot be in the future.");
                }
            } catch (ParseException e) {
                throw new UserRegistrationException("Invalid date format. Please use dd/MM/yyyy.");
            }

            // Create and add the user
            User user = new User(username, firstname, lastname, dateOfBirth);
            if (userService.addUser(user)) {
                System.out.println("User registered successfully.");
            } else {
                System.out.println("User registration failed.");
            }
        } catch (UserRegistrationException e){
            System.out.println(e.getMessage());
        }
    }

    private void displayAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User user : users) {
                System.out.println("ID: " + user.getId() +
                        ", Username: " + user.getUsername() +
                        ", First Name: " + user.getFirstname() +
                        ", Last Name: " + user.getLastname() +
                        ", Date of Birth: " + dateFormat.format(user.getDateOfBirth()));
            }
        }
    }

    private void getUserByUsername() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();

        Optional<User> userOptional = userService.getUserByUsername(username);
        userOptional.ifPresent(user -> {
            System.out.println("ID: " + user.getId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("First Name: " + user.getFirstname());
            System.out.println("Last Name: " + user.getLastname());
            System.out.println("Date of Birth: " + dateFormat.format(user.getDateOfBirth()));
        });
        if (userOptional.isEmpty()) {
            System.out.println("User not found.");
        }
    }

    private void updateUserByUsername() {
        try {
            System.out.println("Enter username to update:");
            String username = scanner.nextLine();

            Optional<User> optionalUser = userService.getUserByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Get and validate new first name
                System.out.println("Enter new first name:");
                String firstname = scanner.nextLine();
                if (firstname.isEmpty() || firstname.trim().isEmpty() || firstname.matches("\\d+")) {
                    throw new UserRegistrationException("First name cannot be empty or a number.");
                }

                // Get and validate new last name
                System.out.println("Enter new last name:");
                String lastname = scanner.nextLine();
                if (lastname.isEmpty() || lastname.trim().isEmpty() || lastname.matches("\\d+")) {
                    throw new UserRegistrationException("Last name cannot be empty or a number.");
                }

                // Get and validate new date of birth
                System.out.println("Enter new date of birth (dd/MM/yyyy):");
                String dob = scanner.nextLine();
                Date dateOfBirth;
                try {
                    dateOfBirth = dateFormat.parse(dob);
                    if (dateOfBirth.after(new Date())) {
                        throw new UserRegistrationException("Date of birth cannot be in the future.");
                    }
                } catch (ParseException e) {
                    throw new UserRegistrationException("Invalid date format. Please use dd/MM/yyyy format.");
                }

                // Update user details
                user.setFirstname(firstname);
                user.setLastname(lastname);
                user.setDateOfBirth(dateOfBirth);
                if (userService.updateUser(user)) {
                    System.out.println("User updated successfully.");
                } else {
                    System.out.println("User update failed.");
                }
            } else {
                System.out.println("User not found.");
            }
        } catch (UserRegistrationException e){
            System.out.println(e.getMessage());
        }
    }

    private void deleteUserByUsername() {
        System.out.println("Enter username to delete:");
        String username = scanner.nextLine();

        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            System.out.println("Are you sure you want to delete this user? (yes/no)");
            String confirmation = scanner.nextLine();
            if (confirmation.equalsIgnoreCase("yes")) {
                if (userService.deleteUserByUsername(username)) {
                    System.out.println("User deleted successfully.");
                } else {
                    System.out.println("User deletion failed.");
                }
            } else {
                System.out.println("User deletion cancelled.");
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private void deleteAllUsers() {
        System.out.println("Are you sure you want to delete all users? (yes/no)");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            userService.deleteAllUsers();
            System.out.println("All users deleted.");
        } else {
            System.out.println("Deletion of all users cancelled.");
        }
    }
}
