package org.example.node.Controllers;

import org.example.node.Models.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class ConfigureNodeController {

    @Autowired
    private Node node;



    @PostMapping("/configureNode")
    public ResponseEntity<String> configureNode(@RequestBody Node nodeConfig) {
        System.out.println(nodeConfig);

        node.setName(nodeConfig.getName());
        node.setIsAffinity(nodeConfig.getIsAffinity());
        node.setPort(nodeConfig.getPort());
        node.setUsers(nodeConfig.getUsers());

        return ResponseEntity.ok("Node configured successfully in port : " + nodeConfig.getPort());
    }




}
