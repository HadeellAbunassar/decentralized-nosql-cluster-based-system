package org.example.node.Controllers;

import org.example.node.DataTransmission.Broadcast;
import org.example.node.DataTransmission.ToAffinity;
import org.example.node.Models.Document;
import org.example.node.Models.Node;
import org.example.node.QueriesHandling.QueryDispatcher;
import org.example.node.Services.PreprocessQueryService;
import org.example.node.Services.TokenService;
import org.example.node.Transaction.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/")
public class QueriesController {

    @Autowired
    private Node node;

    @Autowired
    private QueryDispatcher QD;

    @Autowired
    Broadcast broadcast;

    @Autowired
    ToAffinity toAffinity;

    @Autowired
    PreprocessQueryService preprocessQuery;

    @Autowired
    Document document;

    @Autowired
    TokenService tokenService;

    @GetMapping("/query/{query}/{username}/{token}")
    public ResponseEntity<Object> handleQuery(@PathVariable String query,
                                              @PathVariable String username,
                                              @PathVariable String token) throws IOException {

        if (!tokenService.validateToken(token,username))
            return ResponseEntity.ok("Your session is Expired , please try to log in again.");


        String processedQuery = preprocessQuery.preprocessQueryTODB(query);

        Object obj = null;
        if (isWrite(processedQuery)) {
            if (node.getIsAffinity()) {
              obj =  QD.dispatchQuery(processedQuery);
              broadcast.broadcastDocumentToNodes(document);
            } else {
                 toAffinity.QueryToAffinityNode(processedQuery);
                return ResponseEntity.ok("Sent to Affinity Node");
            }
        }
        else {
          obj = QD.dispatchQuery(processedQuery); // any Node can read!
            if(isCreationDeletionEntites(query)){
                broadcast.broadcastCollDBAndIndicesToNodes(query);
            }
        }

        // return the result

        if( obj instanceof String) {
            System.out.println((String) obj);
            return ResponseEntity.ok((String) obj);
        }
        else  {
            if (((Response) obj).getStatus().equals(Response.Status.SUCCESS)) {
                if (((Response) obj).getDocument() != null)
                    return ResponseEntity.ok((ConcurrentHashMap<String, Object>) ((Response) obj).getDocument());
                else {
                    return ResponseEntity.ok((List<ConcurrentHashMap<String, Object>>) ((Response) obj).getDocuments());
                }
            } else
                return ResponseEntity.ok(((Response) obj).getMessage());
        }
    }


    private boolean isWrite(String query) {
        return query.matches("(?i).*\\b(update|insert)\\b.*");
    }

    private boolean isCreationDeletionEntites(String query) {
        return query.matches("(?i).*\\b(drop|create|delete)\\b.*") ||
                query.matches("(.+)\\.(.+)\\.(createIndex|deleteIndex)\\.(.+)");
    }




}
