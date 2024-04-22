package org.example.node.QueriesHandling;

import org.example.node.DAOS.DatabaseDAO;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseQueriesHandler {

    private DatabaseDAO dbDAO;

    private DatabaseQueriesHandler() {
        this.dbDAO = new DatabaseDAO();
    }

    public static DatabaseQueriesHandler getInstance() {
        return new DatabaseQueriesHandler();
    }

    public String createDatabase(String query) throws IOException {
            String dbName = extractDatabaseName(query, "^create\\s+database\\s+(\\w+)$");
           return  dbDAO.createDB(dbName).getMessage();
    }

    public String dropDatabase(String query) throws IOException {
            String dbName = extractDatabaseName(query, "^drop\\s+database\\s+(\\w+)$");
            return dbDAO.dropDB(dbName).getMessage();
    }

    private static String extractDatabaseName(String query, String pattern) {
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(query);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
