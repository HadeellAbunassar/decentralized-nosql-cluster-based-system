package org.example.node.DataTransmission;

import org.example.node.Models.Node;
import org.example.node.Models.NodeUserCount;
import org.example.node.Services.SystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;


@Component
public class ToAffinity {
    //done!

    @Autowired
    SystemInfoService info;

    @Autowired
    NodeUserCount nodeUserCount;

    private RestTemplate restTemplate = new RestTemplate();


    public void QueryToAffinityNode(String query) throws IOException {
        List<Node> nodes = info.loadNodes();

        List<Node> affinityNodes = findAffinityNodes(nodes);

        Node leastLoadedAffinityNode = findLeastLoadedNode(affinityNodes);
        if (leastLoadedAffinityNode != null) {
            String nodeEndpoint = "http://" + leastLoadedAffinityNode.getName() + ":" + leastLoadedAffinityNode.getPort() + "/Affinity";
            restTemplate.postForObject(nodeEndpoint, query, String.class);
        }
    }

    private List<Node> findAffinityNodes(List<Node> nodes) {
        return nodes.stream()
                .filter(Node::getIsAffinity)
                .toList();
    }

    private Node findLeastLoadedNode(List<Node> nodes) {
        // Find the affinity node with the least number of users
        int minUserCount = Integer.MAX_VALUE;
        Node leastLoadedNode = null;
        for (Node node : nodes) {
            Integer userCount = nodeUserCount.getUserCounts().get(node.getPort());
            if (userCount != null && userCount < minUserCount) {
                minUserCount = userCount;
                leastLoadedNode = node;
            }
        }
        System.out.println(leastLoadedNode);
        return leastLoadedNode;
    }
}