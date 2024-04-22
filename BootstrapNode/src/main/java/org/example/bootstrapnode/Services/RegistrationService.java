package org.example.bootstrapnode.Services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.bootstrapnode.Models.Node;
import org.example.bootstrapnode.Models.NodeUserCount;
import org.example.bootstrapnode.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.example.bootstrapnode.Services.PasswordService.*;


@Service
public class RegistrationService {

    @Autowired
    private LoadingInfoService loadingInfoService;

    @Autowired
    NodeUserCount nodeUserCount;
    private ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate restTemplate = new RestTemplate();



    public  User register(String name) throws IOException, URISyntaxException {
        nodeUserCount.getUserCounts().forEach((key, value) -> {
            System.out.println(key +  ":" + value + "users" );        });

        List<User> users = loadingInfoService.loadUsers();

        String userId = UUID.randomUUID().toString();
        byte[] salt = generateSalt();
        String password = passwordInitializer();
        String hashedPassword = hashPassword(password,salt);
        User newUser = new User(userId,password,salt,name,hashedPassword,"user");

        Node node = AssignUserToNode(newUser);
        users.add(newUser);
        saveUsers(users);

        User user = new User(userId,password,salt,name,hashedPassword,"user");
        user.setNode(node);

        return user;
    }

    private synchronized void saveUsers(List<User> users) throws IOException, URISyntaxException {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        URL resource = getClass().getClassLoader().getResource("Data/users.json");
        if (resource != null) {
            File file1 = new File(resource.getFile());
            try (OutputStream os = new FileOutputStream(file1)) {
                objectMapper.writeValue(os, users);
                System.out.println("writing done");
            }
        }

    }

    public void broadcastNewUserToAllNodes(User newUser) throws IOException {
        List<Node> nodes = loadingInfoService.loadNodes();
        for (Node node : nodes) {
            String url = "http://" + node.getName() + ":" + node.getPort() + "/broadcastUser"; // all nodes must add it to the .json file
            restTemplate.postForObject(url, newUser, String.class);
        }
    }

    public Node AssignUserToNode(User newUser) throws IOException {
        Node nodeWithLeastUsers = findNodeWithLeastUsers();
        nodeWithLeastUsers.addUser(newUser);
        String url = "http://" + nodeWithLeastUsers.getName() + ":" + nodeWithLeastUsers.getPort() + "/newUser"; // to add the user tho the node users
        restTemplate.postForObject(url, newUser, String.class);

        // Update the user count for the node to which the new user is assigned
        System.out.println(nodeUserCount.getUserCounts().get(nodeWithLeastUsers.getPort())); // checkk
        nodeUserCount.getUserCounts().put(nodeWithLeastUsers.getPort(), nodeUserCount.getUserCounts().get(nodeWithLeastUsers.getPort()) +1 );
        broadcastUsersCountToAllNodes();

        return nodeWithLeastUsers;
    }

    private Node findNodeWithLeastUsers() throws IOException {
        List<Node> nodes = loadingInfoService.loadNodes();

        String nodeNameWithLeastUsers = getNodeWithLeastUsers();

        for (Node node : nodes) {
            if (node.getPort().equals(nodeNameWithLeastUsers)) {
                return node;
            }
        }

        return null;
    }

    public String getNodeWithLeastUsers() {
        int minUserCount = Integer.MAX_VALUE; // Initialize with maximum possible value
        String nodeNameWithMinUsers = null ;

        for (Map.Entry<String, Integer> entry : nodeUserCount.getUserCounts().entrySet()) {
            String nodeName = entry.getKey();
            int userCount = entry.getValue();

            if (userCount < minUserCount) {
                minUserCount = userCount;
                nodeNameWithMinUsers = nodeName;
            }
        }
        return nodeNameWithMinUsers;
    }

    public void broadcastUsersCountToAllNodes() throws IOException {
        List<Node> nodes = loadingInfoService.loadNodes();
        for (Node node : nodes) {
            String url = "http://" + node.getName() + ":" + node.getPort() + "/updateNodeUserCount"; // to keep track of the users in all nodes
            restTemplate.postForObject(url, nodeUserCount, String.class);
        }
    }




}
