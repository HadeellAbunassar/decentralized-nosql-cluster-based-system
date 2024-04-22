package org.example.carsdemo.Client;

import org.example.carsdemo.Services.BootstrapService;
import org.example.carsdemo.Services.LoginService;
import org.example.carsdemo.Services.SendQueryService;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Scanner;

public class AdminClient {

    private static AdminClient instance;

    Scanner scanner = new Scanner(System.in);

    BootstrapService bootstrapService = new BootstrapService();

    LoginService loginService = new LoginService();

    SendQueryService sendQueryService = new SendQueryService();


    private AdminClient() {
    }

    public static AdminClient getInstance() {
        if (instance == null) {
            synchronized (AdminClient.class) {
                if (instance == null) {
                    instance = new AdminClient();
                }
            }
        }
        return instance;
    }

    public void handleAdmin(){
        System.out.println("Please enter your username!");
        String name = scanner.next();

        System.out.println("Please enter your password!");
        String password = scanner.next();

        Object isValid = loginService.isUser(name,password,"admin");

        if(!(isValid instanceof Boolean)) {
            System.out.println("What do you want to do? \n" +
                    "1- Execute Queries \n" +
                    "2- Show number of users among nodes");


            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    Query(isValid);
                    break;
                case 2:
                    GetUsersCount();
                    System.out.println("Do you want to enter queries? (y/n)");

                    String ch = scanner.next();
                    if (ch.equals("y")) {
                        Query(isValid);
                    } else {
                        System.exit(0);
                    }

                    break;
                default:
                    inValid(isValid);
            }
        }
        else{
            System.out.println("Invalid userName or password");
            System.exit(0);
            }

    }

    private void GetUsersCount(){
        ResponseEntity<Map<String, Integer>> responseEntity = bootstrapService.getUserCount();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Map<String, Integer> userCounts = responseEntity.getBody();
            if (userCounts != null) {
                System.out.println("User Counts Among Nodes:");
                for (Map.Entry<String, Integer> entry : userCounts.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            } else {
                System.out.println("No user counts data available.");
            }
        } else {
            System.out.println("Failed to retrieve user counts. Status code: " + responseEntity.getStatusCodeValue());
        }
    }

    private void Query(Object sessionData) {
        String DBName;
        String collectionName;
        String indexName;
        while (true) {
            System.out.println("What do you want to do? \n" +
                    "1- Add database \n" +
                    "2- Add Collection \n" +
                    "3- Add index \n" +
                    "4- Drop database \n" +
                    "5- drop collection \n" +
                    "6- drop index file \n" +
                    "7- Exit" );


            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("What is the database Name?");
                    DBName = scanner.next();
                    System.out.println(sendQueryService.sendQuery("create database " + DBName, (Map<String, String>) sessionData));
                    System.out.println();
                    break;
                case 2:
                    System.out.println("What is the Collection Name?");
                    collectionName = scanner.next();
                    System.out.println("What is the database Name?");
                    DBName = scanner.next();
                    System.out.println(sendQueryService.sendQuery("create collection " + collectionName + " in " + DBName, (Map<String, String>) sessionData));
                    System.out.println();
                    break;
                case 3:
                    System.out.println("What is the Collection Name?");
                    collectionName = scanner.next();
                    System.out.println("What is the database Name?");
                    DBName = scanner.next();
                    System.out.println("What is the index file Name?");
                    indexName = scanner.next();
                    System.out.println(sendQueryService.sendQuery(DBName + "." + collectionName + ".createIndex." + indexName, (Map<String, String>) sessionData));
                    System.out.println();
                    break;
                case 4:
                    System.out.println("What is the database Name?");
                    DBName = scanner.next();
                    System.out.println(sendQueryService.sendQuery("drop database " + DBName, (Map<String, String>) sessionData));
                    System.out.println();
                    break;
                case 5:
                    System.out.println("What is the Collection Name?");
                    collectionName = scanner.next();
                    System.out.println("What is the database Name?");
                    DBName = scanner.next();
                    System.out.println(sendQueryService.sendQuery("drop collection " + collectionName + " in " + DBName, (Map<String, String>) sessionData));
                    System.out.println();
                    break;
                case 6:
                    System.out.println("What is the Collection Name?");
                    collectionName = scanner.next();
                    System.out.println("What is the database Name?");
                    DBName = scanner.next();
                    System.out.println("What is the index file Name?");
                    indexName = scanner.next();
                    System.out.println(sendQueryService.sendQuery(DBName + "." + collectionName + ".deleteIndex." + indexName, (Map<String, String>) sessionData));
                    System.out.println();
                    break;
                case 7:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }

        }

    }

    private void inValid(Object isValid){
        System.out.println("Invalid choice");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                Query(isValid);
                return;
            case 2:
                GetUsersCount();

                System.out.println("Do you want to enter queries? (y/n)");
                String ch = scanner.next();
                if (ch.equals("y")) {
                    Query(isValid);
                } else {
                    System.exit(0);
                }
                return;
            default:
                break;
        }
    }
}


