package org.example.node.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Getter
@Service
public class User {

    @JsonProperty("_id")
    private String id;
    private String userName;
    private String storedHash;
    private String token;
    private String password;
    private Node node;
    private  byte[] salt;
    private String role;

    public User(String id,String password,byte[] salt,String userName,String storedHash,String role){
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.salt = salt;
        this.storedHash = storedHash;
        this.role = role;
    }


    public User() {

    }

}
