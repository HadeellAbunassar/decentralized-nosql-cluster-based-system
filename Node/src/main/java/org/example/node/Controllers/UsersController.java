package org.example.node.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpSession;
import org.example.node.Models.Node;
import org.example.node.Models.NodeUserCount;
import org.example.node.Models.User;
import org.example.node.Services.LoginService;
import org.example.node.Services.SystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/")
@SessionAttributes("user")
public class UsersController {

    @Autowired
    private LoginService loginService;
    User user = new  User();
    @Autowired
    Node node;
    @Autowired
    NodeUserCount nodeUserCount;
    @Autowired
    SystemInfoService info;

    // this node was assigned to this user
    @PostMapping("/newUser")
    public  ResponseEntity<String> receiveUser(@RequestBody User user){
        System.out.println("Serving a new user!");
        node.addUser(user);
        System.out.println(user.getUserName());
        return ResponseEntity.ok("User added successfully.");
    }

    // to keep track of number of users in each node
    @PostMapping("/updateNodeUserCount")
    public ResponseEntity<String> editUsers(@RequestBody NodeUserCount updatedNodeUserCount) throws IOException {

        nodeUserCount.setUserCounts(updatedNodeUserCount.getUserCounts());
        nodeUserCount.getUserCounts().forEach((key, value) -> {
            System.out.println(key + " : " +  value + "users" );        });

        return ResponseEntity.ok("Broadcast new users count is done.");
    }

    // for a new user coming from  bootstrap - all nodes must be aware about it add must add it to the user.json
    @PostMapping("/broadcastUser")
    public void broadcastUser(@RequestBody User newUser) throws IOException, URISyntaxException {
        System.out.println("new user come!");
        List<User> users = info.loadUsers();
        users.add(newUser);
        info.saveUsers(users);
    }


    // to check user login info
    @GetMapping("/login/{username}/{password}/{role}")
    public ResponseEntity<Object> login(@PathVariable String username,
                                        @PathVariable String password,
                                        @PathVariable String role,
                                        HttpSession session) throws IOException {
        user.setUserName(username);
        user.setPassword(password);
        user.setRole(role);
        if(loginService.processLogin(user)) {

            session.setAttribute("username", username);
            session.setAttribute("token", user.getToken());

            ConcurrentHashMap<String, String> sessionData = new ConcurrentHashMap<>();
            sessionData.put("username", username);
            sessionData.put("token", user.getToken());

            return ResponseEntity.ok().body(sessionData);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }



}
