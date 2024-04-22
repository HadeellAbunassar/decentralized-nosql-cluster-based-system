package org.example.bootstrapnode.Controllers;

import org.example.bootstrapnode.Models.Node;
import org.example.bootstrapnode.Models.NodeUserCount;
import org.example.bootstrapnode.Models.Nodes;
import org.example.bootstrapnode.Models.User;
import org.example.bootstrapnode.Services.DistributionService;
import org.example.bootstrapnode.Services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/")
public class BootstrapIndexController {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private DistributionService distributionService;
    @Autowired
    private NodeUserCount nodeUserCount;

    @Autowired
    Nodes nodes;



    @GetMapping("/startCluster") // web
    public ResponseEntity<String> startCluster() throws IOException {

        distributionService.init();
        return ResponseEntity.ok("Cluster startup is done.");
    }

    @GetMapping("/newUser/{username}") // web
    public ResponseEntity<Map<String, String>> register(@PathVariable String username) throws IOException, URISyntaxException {
        System.out.println(username);
        User user = registrationService.register(username);
        registrationService.broadcastNewUserToAllNodes(user);
        System.out.println(user.getUserName() + " " + user.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUserName());
        response.put("password", user.getPassword());
        response.put("nodeName", user.getNode().getPort());
        return ResponseEntity.ok(response);
    }

   @PostMapping("/GetUserCountsEachNode")
   public ResponseEntity<Map<String, Integer>> getUserCount(){
        return ResponseEntity.ok(nodeUserCount.getUserCounts());
   }

    @PostMapping("/updateNodeUserCount")  // node
    public ResponseEntity<String> editUsers(@RequestBody NodeUserCount updatedNodeUserCount) throws IOException {
        nodeUserCount.setUserCounts(updatedNodeUserCount.getUserCounts());
        return ResponseEntity.ok("Broadcast new users count is done.");
    }

    @GetMapping("/UserNode/{userName}")
    public ResponseEntity<Map<String, String>> userNode(@PathVariable String userName) {
        Map<String, String> result = new HashMap<>();

        for (Node node : nodes.getNodes()) {
            if (node.hasUser(userName)) {
                System.out.println(node.getName());
                System.out.println(node.getPort());
                result.put("nodeName", node.getName());
                result.put("port", String.valueOf(node.getPort()));
            }
        }
            return ResponseEntity.ok(result);
    }


}



