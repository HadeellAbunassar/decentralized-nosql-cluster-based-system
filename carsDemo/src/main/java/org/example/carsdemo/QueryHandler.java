package org.example.carsdemo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class QueryHandler {

    public static List<Map<String, Object>> parseJsonArray(String jsonArrayString) {
        List<Map<String, Object>> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonArrayString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, Object> map = new HashMap<>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                map.put(key, value);
            }
            list.add(map);
        }
        return list;
    }

    public static void printItems(List<Map<String, Object>> response) {
        for (int i = 0; i < response.size(); i++) {
            Map<String, Object> item = response.get(i);
            String type = (String) item.get("type");
            int price = (int) item.get("price");
            String color = (String) item.get("color");
            System.out.println((i + 1) + "- Type: " + type + ", Price: $" + price + ", Color: " + color);
        }
    }

    public static long getTimestampFromUserInput(List<Map<String, Object>> response, int userInput) {
        if (userInput < 1 || userInput > response.size()) {
            System.out.println("Invalid input. Please enter a number between 1 and " + response.size());
            return -1;
        }

        long selectedTimestamp = (long) response.get(userInput - 1).get("_timestamp");
        return selectedTimestamp;
    }

    public static String getIdFromUserInput(List<Map<String, Object>> response, int userInput) {
        if (userInput < 1 || userInput > response.size()) {
            System.out.println("Invalid input. Please enter a number between 1 and " + response.size());
            return null;
        }

        String selectedId = (String) response.get(userInput - 1).get("_id");
        return selectedId;
    }
}
