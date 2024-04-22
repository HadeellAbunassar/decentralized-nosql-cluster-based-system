package org.example.bootstrapnode.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bootstrapnode.Models.Node;
import org.example.bootstrapnode.Models.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class LoadingInfoService {
    public List<User> loadUsers() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("Data/users.json").getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<List<User>>(){});
        }
    }

    public List<Node> loadNodes() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("Data/nodes.json").getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<List<Node>>(){});
        }
    }


}
