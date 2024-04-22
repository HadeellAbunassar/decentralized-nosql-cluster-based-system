package org.example.carsdemo.Services;

import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SendQueryService {

    RestTemplate restTemplate = new RestTemplate();
    BootstrapService bootstrapService = new BootstrapService();


    public Object sendQuery(String query, Map<String, String> sessionData) {
        String username = sessionData.get("username");
        Map<String,String> nodeData = bootstrapService.UserNode(username);
        String token = sessionData.get("token");
        String url = "http://" + nodeData.get("nodeName") + ":" + nodeData.get("port") + "/query/"  + query + "/"  + username + "/" + token;
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }





}
