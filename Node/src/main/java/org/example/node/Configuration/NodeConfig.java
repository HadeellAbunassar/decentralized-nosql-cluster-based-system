package org.example.node.Configuration;


import org.example.node.Models.Document;
import org.example.node.Models.Node;
import org.example.node.Models.NodeUserCount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfig {

    @Bean
    public Node node() {
        return new Node();
    }

    @Bean
    public NodeUserCount nodeUserCount() {
        return new NodeUserCount();
    }

    @Bean
    public Document document(){return new Document();}




}