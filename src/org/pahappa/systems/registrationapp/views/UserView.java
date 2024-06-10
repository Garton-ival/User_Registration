package org.pahappa.systems.registrationapp.views;

import org.pahappa.systems.registrationapp.models.User;
import org.pahappa.systems.registrationapp.services.UserService;

import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Constructor;

public class UserView {

    private final Scanner scanner;
    private final UserService userService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public UserView(){

        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
    }


    public void displayMenu() {
        System.out.println("********* User Registration System *********");
        boolean running = true;

        while (running) {
            System.out.println("\nChoose an operation:");
            System.out.println("1. Register a user");
            System.out.println("2. Display all users");
            System.out.println("3. Get a user of username");
            System.out.println("4. Update user details of username");
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
                        getUserOfUsername();
                        break;
                    case 4:
                        updateUserOfUsername();
                        break;
                    case 5:
                        deleteUserOfUsername();
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
        // Get and validate username
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }
        if (username.matches("\\d+")) {
            System.out.println("Username cannot be a number.");
            return;
        }       if (userService.getUserByUsername(username).isPresent()) {
            System.out.println("Username already exists.");
            return;
        }

        // Get and validate first name
        System.out.println("Enter first name:");
        String firstname = scanner.nextLine();
        if (firstname.isEmpty() || firstname.trim().isEmpty() || firstname.matches("\\d+")) {
            System.out.println("First name cannot be empty or a number.");
            return;
        }

        // Get and validate last name
        System.out.println("Enter last name:");
        String lastname = scanner.nextLine();
        if (lastname.isEmpty() || lastname.trim().isEmpty() || lastname.matches("\\d+")) {
            System.out.println("Last name cannot be empty or a number.");
            return;
        }

        // Get and validate date of birth
        System.out.println("Enter date of birth (dd/MM/yyyy):");
        String dob;
        dob = scanner.nextLine();
        Date dateOfBirth;
        try {
            dateOfBirth = dateFormat.parse(dob);
            if (dateOfBirth.after(new Date())) {
                System.out.println("Date of birth cannot be in the future.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
            return;
        }

        // Create and add the user using reflection to access the private constructor
        try {
            Constructor<User> constructor = User.class.getDeclaredConstructor(String.class, String.class, String.class, Date.class);
            constructor.setAccessible(true); // Make the private constructor accessible
            User user = constructor.newInstance(username, firstname, lastname, dateOfBirth);
            if (userService.addUser(user)) {
                System.out.println("User registered successfully.");
            } else {
                System.out.println("User registration failed.");
            }
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    private void displayAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users registered.");
        } else {
            for (User user : users) {
                System.out.println("Username: " + user.getUsername() +
                        ", First Name: " + user.getFirstname() +
                        ", Last Name: " + user.getLastname() +
                        ", Date of Birth: " + dateFormat.format(user.getDateOfBirth()));
            }
        }
    }

    private void getUserOfUsername() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        Optional<User> userOptional = userService.getUserByUsername(username);
        userOptional.ifPresent(user -> {
            System.out.println("Username: " + user.getUsername());
            System.out.println("First Name: " + user.getFirstname());
            System.out.println("Last Name: " + user.getLastname());
            System.out.println("Date of Birth: " + dateFormat.format(user.getDateOfBirth()));
        });
        if (userOptional.isEmpty()) {
            System.out.println("User not found.");
        }

    }

    private void updateUserOfUsername() {
        System.out.println("Enter username of the user to update:");
        String username = scanner.nextLine();
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Get and validate new first name
            System.out.println("Enter new first name:");
            String firstname = scanner.nextLine();
            if (firstname.isEmpty() || firstname.trim().isEmpty() || firstname.matches("\\d+")) {
                System.out.println("First name cannot be empty or a number.");
                return;
            }

            // Get and validate new last name
            System.out.println("Enter new last name:");
            String lastname = scanner.nextLine();
            if (lastname.isEmpty() || lastname.trim().isEmpty() || lastname.matches("\\d+")) {
                System.out.println("Last name cannot be empty or a number.");
                return;
            }

            // Get and validate new date of birth
            System.out.println("Enter new date of birth (dd/MM/yyyy):");
            String dob = scanner.nextLine();
            Date dateOfBirth;
            try {
                dateOfBirth = dateFormat.parse(dob);
                if (dateOfBirth.after(new Date())) {
                    System.out.println("Date of birth cannot be in the future.");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
                return;
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
    }

    private void deleteUserOfUsername() {
        System.out.println("Enter username of the user to delete:");
        String username = scanner.nextLine();
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            System.out.println("Are you sure you want to delete this user? (yes/no)");
            String confirmation = scanner.nextLine();
            if (confirmation.equalsIgnoreCase("yes")) {
                if (userService.deleteUser(username)) {
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
