package org.example.node.QueriesHandling;

import org.example.node.DAOS.KeyValueIndexDAO;
import org.example.node.Models.Document;
import org.example.node.Transaction.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QueryDispatcher {

    private final DatabaseQueriesHandler databaseQueriesHandler;
    private final CollectionQueriesHandler collectionQueriesHandler;
    private  DocumentQueriesHandler documentQueriesHandler;

    @Autowired
    Document document;


    private KeyValueIndexDAO kvDAO;

    public QueryDispatcher() {
        this.databaseQueriesHandler = DatabaseQueriesHandler.getInstance();
        this.collectionQueriesHandler = CollectionQueriesHandler.getInstance();
        kvDAO = new KeyValueIndexDAO();
    }

    public Object dispatchQuery(String query) {
        try {
            if (query.matches("(?i)^create\\s+database\\s+.+$") || query.matches("(?i)^drop\\s+database\\s+.+$")) {
                return handleDatabaseQuery(query);
            } else if (query.matches("(?i)^create\\s+collection\\s+.+in\\s+.+$") || query.matches("(?i)^drop\\s+collection\\s+.+in\\s+.+$")) {
                return handleCollectionQuery(query);
            }else if (query.matches("(?i).+\\.(insert|update|delete|find|findAll|findById)(\\(.*\\))?")) {
                return handleDocumentQuery(query);
            }
            else if (query.matches("(.+)\\.(.+)\\.(createIndex|deleteIndex)\\.(.+)")) {
                return handleIndicesQuery(query);
            } else {
                return new Response(Response.Status.INVALID_QUERY, "Invalid Query, check the manual");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(Response.Status.UNKNOWN_ERROR, "ERROR, TRY LATER");
        }
    }

    private String handleDatabaseQuery(String query) throws IOException {
        if (query.toLowerCase().startsWith("create database")) {
            return databaseQueriesHandler.createDatabase(query);
        } else if (query.toLowerCase().startsWith("drop database")) {
            return   databaseQueriesHandler.dropDatabase(query);
        } else {
            return "Invalid Query, check the manual";
        }
    }

    private String handleCollectionQuery(String query) throws IOException {
        if (query.toLowerCase().contains("create collection")) {
           return collectionQueriesHandler.createCollection(query);
        } else if (query.toLowerCase().contains("drop collection")) {
          return  collectionQueriesHandler.dropCollection(query);
        } else {
            return "Invalid Query, check the manual";
        }
    }

    public Object handleDocumentQuery(String query) throws IOException {
        Pattern pattern = Pattern.compile("([a-zA-Z]+)\\.([a-zA-Z]+)\\.(insert|update|delete|find|findAll|findById)\\((.*)\\)");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String dbName = matcher.group(1);
            String collectionName = matcher.group(2);
            String operation = matcher.group(3);
            String args = matcher.group(4);

            document.setCollectionName(collectionName);
            document.setDBName(dbName);

            documentQueriesHandler = new DocumentQueriesHandler(dbName,collectionName,document);


            switch (operation) {
                case "insert":
                    return documentQueriesHandler.insertDocument(args).getMessage(); // string
                case "update":
                    return documentQueriesHandler.updateDocument(args); // string
                case "delete":
                    return documentQueriesHandler.deleteDocument(args.trim()); // string
                case "find":
                    return documentQueriesHandler.findDocuments(args);
                case "findAll":
                    return documentQueriesHandler.findAllDocuments();
                case "findById":
                    return documentQueriesHandler.findById(args.trim());
                default:
                    return  "Invalid operation, check the manual";
            }
        } else {
            return "Invalid Query, check the manual";
        }
    }

    private String handleIndicesQuery(String query) throws IOException {
        Pattern pattern = Pattern.compile("(.+)\\.(.+)\\.(createIndex|deleteIndex)\\.(.+)");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String dbName = matcher.group(1);
            String collectionName = matcher.group(2);
            String operation = matcher.group(3);
            String indexKey = matcher.group(4);

            switch (operation.toLowerCase()) {
                case "createindex":
                    return  kvDAO.createIndexFile(dbName, collectionName, indexKey);
                case "deleteindex":
                    return kvDAO.deleteIndexFile(dbName, collectionName, indexKey);
                default:
                    return "Invalid operation, check the manual";
            }
        } else {
            return "Invalid Query, check the manual";
        }
    }


}

