package org.example.node.QueriesHandling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.node.DAOS.DocumentDAO;
import org.example.node.Models.Document;
import org.example.node.Transaction.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;



public class DocumentQueriesHandler {

    private final DocumentDAO documentDAO;
    private final ObjectMapper objectMapper;




    public DocumentQueriesHandler(String DBName,String CollectionName,Document document) {
        this.documentDAO = new DocumentDAO(DBName,CollectionName,document);
        this.objectMapper = new ObjectMapper();
    }

    public Response findById(String id) throws IOException {
        return documentDAO.findDocumentById(id);
    }

    public Response insertDocument(String json) throws IOException {
         return documentDAO.insertDocument(json);
    }

    public String updateDocument(String args) throws IOException {
        String[] parts = args.split(",", 3);
        if (parts.length == 3) {
            String id = parts[0].trim().replaceAll("^\"|\"$", "");
            String timestamp = parts[1].trim().replaceAll("^\"|\"$", "");
            String newKeyValueJson = parts[2].trim().replaceAll("^\"|\"$", "").replace("\\\"", "\"");
            return documentDAO.updateDocument(id, timestamp, newKeyValueJson).getMessage();
        } else {
            return "Invalid arguments for update operation.";
        }
    }




    public String deleteDocument(String id) throws IOException {
        return documentDAO.deleteDocument(id).getMessage();
    }

    public Response findDocuments(String jsonCriteria) {
        try {
            ConcurrentHashMap<String, Object> criteria = objectMapper.readValue(jsonCriteria, new TypeReference<ConcurrentHashMap<String, Object>>() {});
            List<ConcurrentHashMap<String, Object>> foundDocuments = documentDAO.findDocuments(criteria).getDocuments();
            if (foundDocuments.isEmpty()) {
                return new Response(Response.Status.DB_ERROR, "No documents found matching the criteria.");
            } else {
                return new Response(Response.Status.SUCCESS, foundDocuments);
            }
        } catch (IOException e) {
            return new Response(Response.Status.UNKNOWN_ERROR, "An I/O error occurred: ");
        }
    }

    public Response findAllDocuments() {
        try {
            return documentDAO.findAllDocuments();
        } catch (IOException e) {
            return new Response(Response.Status.IO_EXCEPTION ,"An I/O error occurred" );
        }
    }


}
