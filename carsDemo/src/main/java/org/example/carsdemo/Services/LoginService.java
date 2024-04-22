package org.example.carsdemo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class LoginService {

    private RestTemplate restTemplate = new RestTemplate();
    private BootstrapService bootstrapService = new BootstrapService();

    public Object isUser(String username, String password, String role) {
        String url;

        Map<String,String> nodeData = bootstrapService.UserNode(username);

        if (!nodeData.isEmpty()) {
            url = "http://" + nodeData.get("nodeName") + ":" + nodeData.get("port") + "/login/" + username + "/" + password + "/" + role;
            try {
                ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, String>>() {
                        }
                );

                Map<String, String> sessionData = response.getBody();
                if (sessionData != null && sessionData.containsKey("username") && sessionData.containsKey("token")) {
                    return sessionData;
                } else {
                    return false;
                }
            } catch (HttpClientErrorException.Unauthorized e) {
                return false;
            }
        }
        return false;
    }

}