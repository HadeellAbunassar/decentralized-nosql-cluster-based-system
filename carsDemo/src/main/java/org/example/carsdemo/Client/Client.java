package org.example.carsdemo.Client;

import org.example.carsdemo.Services.BootstrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Scanner;

@Service
public class Client {

    UserClient userClient = UserClient.getInstance();
    AdminClient adminClient = AdminClient.getInstance();
    BootstrapService bootstrapService = new BootstrapService();
    Scanner scanner = new Scanner(System.in);
    int option;

    public void ClientMain() {


        bootstrapService.startBootstrap(); // start the cluster


        System.out.println("Welcome to our Cars gallery! \n" +
                "1- log in \n" +
                "2- sign up");

        option = scanner.nextInt();
        switch (option) {
            case 1:
                login();
                break;
            case 2:
                signUp();
                break;
            default:
                notValid();

        }
    }


    private void login() {
        while (true) {
            System.out.println("Log in as: \n 1- Admin \n 2- User");
            option = scanner.nextInt();

            if (option == 1) {
                adminClient.handleAdmin();
                break;
            } else if (option == 2) {
                userClient.handleUser();
                break;
            } else {
                System.out.println("Invalid option! Please enter 1 or 2.");
            }
        }
    }

    private void signUp(){
        System.out.println("Please enter your username!");
        String name = scanner.next();
        ResponseEntity<Map<String, String>> response = bootstrapService.addUser(name);
        Map<String, String> userInfo = response.getBody();
        System.out.println("Hello " + name + "! Your password is: " + userInfo.get("password") +
                " and you are assigned to " + userInfo.get("nodeName"));
        System.exit(0);
    }

    private void notValid(){
        while (true) {
            System.out.println("Invalid option! Please enter 1 or 2.");
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    login();
                    return;
                case 2:
                    signUp();
                    return;
                default:
                    break;
            }
        }
    }

}

