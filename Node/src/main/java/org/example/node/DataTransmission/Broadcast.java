package org.example.node.DataTransmission;

import org.example.node.Models.Document;
import org.example.node.Models.Node;
import org.example.node.Services.SystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class Broadcast {

    @Autowired
    SystemInfoService info;
    @Autowired
    private Node node;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    Document document;

    public void broadcastDocumentToNodes(Document document) throws IOException {

        List<Node> nodes = info.loadNodes();
        for (Node targetNode : nodes) {
            if (targetNode.getPort() != node.getPort()) {
                String nodeEndpoint = "http://" + targetNode.getName() + ":" + targetNode.getPort() + "/receiveDocument";
                restTemplate.postForObject(nodeEndpoint, document, Document.class);
            }
        }
    }

    public void broadcastCollDBAndIndicesToNodes(String query) throws IOException {
        List<Node> nodes = info.loadNodes();
        for (Node targetNode : nodes) {
            if (targetNode.getPort() != node.getPort()) {
                String nodeEndpoint = "http://" + targetNode.getName() + ":" + targetNode.getPort() + "/CDI";
                restTemplate.postForObject(nodeEndpoint, query, String.class);
            }
        }

    }

}
