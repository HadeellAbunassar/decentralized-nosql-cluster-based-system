package org.example.node.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.node.Models.Document;
import org.example.node.PATHS;
import org.example.node.QueriesHandling.QueryDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/")
public class HandleBroadcastController {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String path;



    @Autowired
    QueryDispatcher QD;

    @PostMapping("/receiveDocument") // done
    public void receiveDocument(@RequestBody Document receivedDocument) {
        String dbName = receivedDocument.getDBName();
        String collectionName = receivedDocument.getCollectionName();
        String documentFileName = receivedDocument.getDocumentFileName();
        ConcurrentHashMap<String, Object> jsonData = receivedDocument.getJsonData();

        String filePath = PATHS.DATABASE_PATH + "/" + dbName + "/" + collectionName + "/" + documentFileName;

        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try {
            objectMapper.writeValue(file, jsonData);
            System.out.println("Document updated successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Error updating document: " + filePath);
            e.printStackTrace();
        }
    }

    @PostMapping("/CDI")
    public ResponseEntity<String> broadcastQuery(@RequestBody String query){
        QD.dispatchQuery(query);
       return ResponseEntity.ok("Query handled successfully");
    }






}




