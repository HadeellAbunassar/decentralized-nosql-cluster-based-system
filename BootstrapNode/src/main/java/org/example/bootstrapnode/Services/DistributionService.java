package org.example.bootstrapnode.Services;

import org.example.bootstrapnode.Models.Node;
import org.example.bootstrapnode.Models.NodeUserCount;
import org.example.bootstrapnode.Models.Nodes;
import org.example.bootstrapnode.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DistributionService {

    @Autowired
    LoadingInfoService info;

    @Autowired
    NodeUserCount nodeUserCount;

    @Autowired
    private Nodes nodes;


    private RestTemplate restTemplate = new RestTemplate();

    public void init() throws IOException {
        List<User> users = info.loadUsers();
        List<Node> nodes = info.loadNodes();
        distributeUsersAmongNodes(users, nodes);

        for (Node node : nodes) {
            distributeNodeConfiguration(node);
        }
    }

    public void distributeUsersAmongNodes(List<User> users, List<Node> nodes){
        int nodeIndex = 0;
        for (User user : users) {
            Node node = nodes.get(nodeIndex);
            node.addUser(user);
            nodeIndex = (nodeIndex + 1) % nodes.size();
        }

        for(Node node:nodes){
            this.nodes.addNode(node);
            nodeUserCount.addUserCount(node.getPort() , node.getUsers().size());
            System.out.println(node.getName() + " " + node.getUsers().size());
        }
    }

    public void distributeNodeConfiguration(Node node) throws IOException {
        broadcastUsersCountToAllNodes();
        Node config = new Node();
        config.setName(node.getName());
        config.setPort(node.getPort());
        config.setIsAffinity(node.getIsAffinity());
        config.setUsers(node.getUsers());
        String nodeEndpoint = "http://" + node.getName() + ":" + node.getPort() + "/configureNode";
        restTemplate.postForObject(nodeEndpoint, config, String.class);
    }

    public void broadcastUsersCountToAllNodes() throws IOException {
        List<Node> nodes = info.loadNodes();
        for (Node node : nodes) {
            String url = "http://" + node.getName() + ":" + node.getPort() + "/updateNodeUserCount"; // to keep track of the users in all nodes
            restTemplate.postForObject(url, nodeUserCount, String.class);
        }
    }



}
