package org.example.node.Services;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PreprocessQueryService {

    public String preprocessQueryTODB(String query) {
        if (isDocument(query)) {
            // Match the pattern: tabels.hi.insert(name:haneen,age:27)
            Pattern pattern = Pattern.compile("^(\\w+\\.\\w+)\\.(insert|update|find)\\((.+?)\\)$");
            Matcher matcher = pattern.matcher(query);
            if (matcher.matches()) {
                String collection = matcher.group(1);
                String operation = matcher.group(2);
                String arguments = matcher.group(3);
                String convertedQuery = String.format("%s.%s(%s)", collection, operation, convertArguments(operation, arguments));
                return convertedQuery;
            }
        }
        return query;
    }

    private String convertArguments(String operation, String arguments) {
        if (operation.equals("find")  || operation.equals("insert")) {
            // Split arguments and format them as JSON key-value pairs
            String[] pairs = arguments.split(",");
            StringBuilder convertedArgs = new StringBuilder();
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (convertedArgs.length() > 0) {
                    convertedArgs.append(", ");
                }
                // Check if the value is numeric or not
                String value = keyValue[1].trim();
                if (value.matches("-?\\d+(\\.\\d+)?")) {
                    // Numeric value, don't enclose in quotes
                    convertedArgs.append(String.format("\"%s\": %s", keyValue[0], value));
                } else {
                    // Non-numeric value, enclose in quotes
                    convertedArgs.append(String.format("\"%s\": \"%s\"", keyValue[0], value));
                }
            }
            return "{" + convertedArgs.toString() + "}";
        } else if (operation.equals("update")) {
            String[] parts = arguments.split(",", 3);
            if (parts.length == 3) {
                String id = parts[0].trim();
                String timestamp = parts[1].trim();
                String keyValuePairs = parts[2].trim();
                String[] keyValueArray = keyValuePairs.split(":");
                // Construct the updated field with ID, timestamp, and key-value pair
                String valuePart;
                if (keyValueArray.length > 1) {
                    String value = keyValueArray[1].trim();
                    valuePart = isNumeric(value) ? value : "\"" + value + "\"";
                } else {
                    valuePart = "";
                }
                // Return the updated arguments including the timestamp
                return String.format("\"%s\", \"%s\", \"{\\\"%s\\\":%s}\"", id, timestamp, keyValueArray[0].trim(), valuePart);
            }
        }
        return arguments;
    }


    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }


    private boolean isDocument(String query) {
        return query.matches(".+\\.(insert|update|find)\\(.+\\)$");
    }

}
