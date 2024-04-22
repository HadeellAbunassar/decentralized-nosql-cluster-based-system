package org.example.node.Services;

import org.example.node.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LoginService {

    @Autowired
    VerificationService verificationService;

    @Autowired
    TokenService tokenService;

    public boolean processLogin(User user) throws IOException {
        boolean isValid = verificationService.isValidUser(user);
        if (isValid) {
            String token = tokenService.generateToken(user);
            user.setToken(token);
            System.out.println(token);
            return true;
        } else {
            return false;
        }
    }
}
