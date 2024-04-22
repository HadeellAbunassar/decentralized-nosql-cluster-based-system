package org.example.node.Services;


import org.example.node.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class VerificationService {

    @Autowired
    SystemInfoService info;

    public boolean isValidUser(User user) throws IOException {


        User user2 = getUserByUsername(user.getUserName());

        if(user2 == null)
            return false;

        if(!(user2.getRole().equals(user.getRole())))
            return false;

        byte[] storedSalt = user2.getSalt();
        String providedPassword= user.getPassword();
        String hashedPassword = PasswordService.hashPassword(providedPassword, storedSalt);
        String storedHashedPassword = user2.getStoredHash();

        return hashedPassword.equals(storedHashedPassword);
    }

    public User getUserByUsername(String username) throws IOException {
        List<User> users = info.loadUsers();

            for (User user : users) {
                if (user.getUserName().equals(username)) {
                    return user;
                }
            }


        return null;
    }


}

