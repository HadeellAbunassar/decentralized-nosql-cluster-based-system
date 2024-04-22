package org.example.bootstrapnode.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {

    @JsonProperty("_id")
    private String id;
    private String userName;
    private String storedHash;
    private String password;
    private byte[] salt;
    private String role;
    @JsonIgnore
    private Node node;


    public User(){}
    public User(String id,String password,byte[] salt,String userName,String storedHash,String role){
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.salt = salt;
        this.storedHash = storedHash;
        this.role = role;
    }


}
