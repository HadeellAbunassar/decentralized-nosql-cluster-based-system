package org.example.bootstrapnode.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class Node {
    private String name;
    private boolean isAffinity;
    private String port;
    private List<User> users;

    public boolean getIsAffinity() {
        return isAffinity;
    }
    public void setIsAffinity(boolean isAffinity){
        this.isAffinity = isAffinity;
    }
    public void addUser(User user) {
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        this.users.add(user);
    }

    public boolean hasUser(String userName) {
        for (User user : users) {
            if (user.getUserName().equals(userName)) {
                return true;
            }
        }
        return false;
    }
}
