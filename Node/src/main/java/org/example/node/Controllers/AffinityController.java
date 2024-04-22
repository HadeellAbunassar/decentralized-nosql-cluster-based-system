package org.example.node.Controllers;


import org.example.node.DataTransmission.Broadcast;
import org.example.node.Models.Document;
import org.example.node.QueriesHandling.QueryDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class AffinityController {

    @Autowired
    private QueryDispatcher QD;

    @Autowired
    Broadcast broadcast;

    @Autowired
    Document document;


    @PostMapping("/Affinity")
    public ResponseEntity<String> toAffinity(@RequestBody String query) throws IOException {

        QD.dispatchQuery(query);
        broadcast.broadcastDocumentToNodes(document);

        return ResponseEntity.ok("Query Execution done successfully");
    }



}


