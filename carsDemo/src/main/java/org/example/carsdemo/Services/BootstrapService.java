package org.example.carsdemo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Node;

import java.util.Map;

@Service
public class BootstrapService {



    private RestTemplate restTemplate = new RestTemplate();

    public  void startBootstrap(){
        String url = "http://bootstrap:8082/startCluster";
        restTemplate.getForEntity(url, String.class);

    }

    public  ResponseEntity<Map<String, String>> addUser(String username) {
        String url = "http://bootstrap:8082/newUser/" + username;
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, String>>() {},
                username
        );
        return response;
    }

    public ResponseEntity<Map<String, Integer>> getUserCount(){
        String url = "http://bootstrap:8082/GetUserCountsEachNode";

        return restTemplate.exchange(url,
                        HttpMethod.POST,
                        null,
                        new ParameterizedTypeReference<Map<String, Integer>>() {});
    }

    public Map<String, String> UserNode(String username) {
        String url = "http://bootstrap:8082/UserNode/" + username;
        ResponseEntity<Map<String, String>> res = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, String>>() {},
                username
        );
        return res.getBody();
    }





}
