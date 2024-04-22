package org.example.node.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.node.Models.Node;
import org.example.node.Models.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.List;

@Service
public class SystemInfoService {

    private ObjectMapper objectMapper = new ObjectMapper();

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

    public synchronized void saveUsers(List<User> users) throws IOException {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        URL resource = getClass().getClassLoader().getResource("Data/users.json");
        if (resource != null) {
            File file1 = new File(resource.getFile());
            try (OutputStream os = new FileOutputStream(file1)) {
                objectMapper.writeValue(os, users);
            }
        }

    }


}
