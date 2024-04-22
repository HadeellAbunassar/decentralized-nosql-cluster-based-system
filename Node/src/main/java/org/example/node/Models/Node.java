package org.example.node.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.node.Models.User;

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

      @Override
       public String toString(){
            return this.name + " " + this.isAffinity + " " + this.port + " " + users.size();
      }
}
