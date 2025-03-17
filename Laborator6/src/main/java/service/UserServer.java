package service;

import domain.User;
import repository.DataBase.UserDBRepository;
import java.util.Optional;

public class UserServer {
    private UserDBRepository userDBRepository;

    public UserServer(UserDBRepository userDBRepository) {
        this.userDBRepository = userDBRepository;
    }

    /**
     * Finds the user ID based on user information.
     * Returns an Optional with the user ID if found, or an empty Optional if not.
     */
    public Optional<Long> findId(User user) {
        if (user == null) {
            return Optional.empty(); // Return empty if the user is null.
        }

        // Check each user in the repository for a match
        for (User u : userDBRepository.findAll()) {
            // Null checks for the user's first name, last name, and password
            if (u.getFirstName() != null && u.getLastName() != null && u.getPassword() != null &&
                    u.getFirstName().equals(user.getFirstName()) &&
                    u.getLastName().equals(user.getLastName()) &&
                    u.getPassword().equals(user.getPassword())) {
                return Optional.of(u.getId()); // Return the ID if a match is found
            }
        }
        return Optional.empty(); // Return empty if no match is found
    }

    /**
     * Finds a user by first name, last name, and password.
     * Returns the user if found, or null if not found.
     */
    public User findOne(String firstName, String lastName, String password) {
        // Create a user object from the provided information
        User user = new User(firstName, lastName, password);

        try {
            Optional<Long> id = findId(user);  // Find the ID using the provided user details

            // If the ID is not found, return null
            if (id.isEmpty()) {
                return null;
            }

            // Fetch the user by their ID
            return userDBRepository.findOne(id.get());  // Return the found user

        } catch (Exception e) {
            // It's better to log the error here, or throw a custom exception
            System.err.println("Error finding user: " + e.getMessage());
            e.printStackTrace();
            return null;  // Return null in case of any exception
        }
    }

    public Long findID(String firstName, String lastName) {
        for (User u : userDBRepository.findAll()) {
            if(u.getFirstName().equals(firstName) && u.getLastName().equals(lastName)){
                return u.getId();
            }
        }
        return null;
    }
}
