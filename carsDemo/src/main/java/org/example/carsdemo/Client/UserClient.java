package org.example.carsdemo.Client;

import org.example.carsdemo.Services.BootstrapService;
import org.example.carsdemo.Services.LoginService;
import org.example.carsdemo.Services.SendQueryService;


import java.util.*;

import static org.example.carsdemo.QueryHandler.*;

public class UserClient {

    private static UserClient instance;

    Scanner scanner = new Scanner(System.in);

    LoginService loginService = new LoginService();

    SendQueryService sendQueryService = new SendQueryService();

    BootstrapService bootstrapService = new BootstrapService();


    private UserClient() {
    }

    public static UserClient getInstance() {
        if (instance == null) {
            synchronized (AdminClient.class) {
                if (instance == null) {
                    instance = new UserClient();
                }
            }
        }
        return instance;
    }

    public void handleUser() {
        System.out.println("Please enter your username!");
        String name = scanner.next();


        System.out.println("Please enter your password!");
        String password = scanner.next();

        Object SessionData = loginService.isUser(name, password, "user");


        if (!(SessionData instanceof Boolean)) {
            while(true){
            System.out.println("What do you want to do? \n" +
                    "1- Show Available Cars \n" +
                    "2- Edit Car Info \n" +
                    "3- Add Car \n" +
                    "4- delete Car \n" +
                    "5- find Car by specific value \n" +
                    "6- Exit");


            int choice = scanner.nextInt();
            EnterQuery(SessionData, choice);

        } }
            else {
            System.out.println("Invalid userName or password");
            System.exit(0);
        }

    }

    int userChoice;

    private void EnterQuery(Object sessionData, int choice) {
        switch (choice) {
            case 1:
                List<Map<String, Object>> response = parseJsonArray((String) sendQueryService.sendQuery("Vehicles.cars.findAll()", (Map<String, String>) sessionData));
                printItems(response);
                System.out.println();
                break;
            case 2:
                response = parseJsonArray((String) sendQueryService.sendQuery("Vehicles.cars.findAll()", (Map<String, String>) sessionData));
                printItems(response);
                System.out.println("choose the number of the field you want to update");
                int Id = scanner.nextInt();
                String docId = getIdFromUserInput(response, Id);
                long timestamp = getTimestampFromUserInput(response, Id);
                System.out.println("choose the key you want to update");
                String key = scanner.next();
                System.out.println("choose the new Value");
                String val = scanner.next();
                System.out.println(sendQueryService.sendQuery("Vehicles.cars.update(" + docId + "," +
                        timestamp + "," +
                        key + ":" + val + ")", (Map<String, String>) sessionData));
                System.out.println();

                break;
            case 3:
                System.out.println("Enter car brand:");
                String val1 = scanner.next();
                System.out.println("Enter car price");
                String val2 = scanner.next();
                System.out.println("Enter car color");
                String val3 = scanner.next();
                System.out.println(sendQueryService.sendQuery("Vehicles.cars.insert(type" + ":" + val1 +
                        "," + "price:" + val2 + "," + "color:" + val3 + ")", (Map<String, String>) sessionData));
                System.out.println();

                break;
            case 4:
                response = parseJsonArray((String) sendQueryService.sendQuery("Vehicles.cars.findAll()", (Map<String, String>) sessionData));
                printItems(response);
                System.out.println("choose the number of the field you want to delete");
                userChoice = scanner.nextInt();
                String id = getIdFromUserInput(response, userChoice);
                System.out.println(sendQueryService.sendQuery("Vehicles.cars.delete(" + id + ")", (Map<String, String>) sessionData));
                System.out.println();

                break;
            case 5:
                System.out.println("Choose cars with specific key! \n " +
                        "1- brand \n" +
                        "2- color \n" +
                        "3- price") ;
                userChoice = scanner.nextInt();
                System.out.println("what is the value you are searching for? ");
                String SearchVal = scanner.next();
                String result = (userChoice == 1) ? "brand" : (userChoice == 2) ? "color" : "price";
                response = parseJsonArray((String) sendQueryService.sendQuery("Vehicles.cars.find(" + result + ":" +SearchVal + ")", (Map<String, String>) sessionData));
                printItems(response);
                System.out.println();
                break;
            default:
                System.out.println("Invalid choice");
                System.out.println();
                break;
            case 6:
                System.exit(0);
        }
    }




}