package org.example.bootstrapnode.Models;

import java.util.HashMap;
import java.util.Map;

public class NodeUserCount {
    private Map<String, Integer> userCounts;

    public NodeUserCount() {
        userCounts = new HashMap<>();
    }

    public Map<String, Integer> getUserCounts() {
        return userCounts;
    }

    public void setUserCounts(Map<String, Integer> userCounts) {
        this.userCounts = userCounts;
    }

    public void addUserCount(String key, int value) {
        userCounts.put(key, value);
    }
}
