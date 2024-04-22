package org.example.node.QueriesHandling;

import org.example.node.DAOS.CollectionDAO;
import org.example.node.PATHS;
import org.example.node.Transaction.Response;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionQueriesHandler {

    private CollectionDAO cDAO;

    private CollectionQueriesHandler() {
        cDAO = new CollectionDAO();
    }

    public static CollectionQueriesHandler getInstance() {
        return new CollectionQueriesHandler();
    }

    public String createCollection(String query) {
        String[] parts = extractNameAndDatabase(query, "create");
        if (parts != null) {
            String collectionName = parts[0];
            String dbName = parts[1];
            if (databaseExists(dbName)) {
                return cDAO.createCollection(collectionName, dbName).getMessage();
            } else {
                return "Database does not exist.";
            }
        } else {
            return "Invalid query.";
        }
    }

    public String dropCollection(String query) {
        String[] parts = extractNameAndDatabase(query, "drop");
        if (parts != null) {
            String collectionName = parts[0];
            String dbName = parts[1];
            if (databaseExists(dbName)) {
                return cDAO.dropCollection(collectionName, dbName).getMessage();
            } else {
                return "Database does not exist.";
            }
        } else {
            return "Invalid query.";
        }
    }

    private static String[] extractNameAndDatabase(String query, String action) {
        String pattern = "^" + action + "\\s+collection\\s+(\\w+)\\s+in\\s+(\\w+)$";
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(query);
        if (matcher.find()) {
            return new String[]{matcher.group(1), matcher.group(2)};
        }
        return null;
    }

    private boolean databaseExists(String dbName) {
        Path databasePath = Paths.get(PATHS.DATABASE_PATH, dbName);
        return Files.exists(databasePath) && Files.isDirectory(databasePath);
    }
}
