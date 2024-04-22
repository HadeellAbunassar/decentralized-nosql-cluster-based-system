package org.example.node.DAOS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.example.node.Cache.Cache;
import org.example.node.Indices.DocumentLevelIndexing;
import org.example.node.Models.Document;
import org.example.node.PATHS;
import org.example.node.Services.DocumentSearchingService;
import org.example.node.Transaction.Response;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.node.Indices.KeyValueLevelIndexing.*;
import static org.example.node.Indices.KeyValueLevelIndexing.updateIndexForKey;


public class DocumentDAO {




    private  ObjectMapper objectMapper;
    private DocumentLevelIndexing documentIndexing;
    private String path;
    private String indexingFile;

    private DocumentSearchingService documentSearchingService;

    private Cache<String, ConcurrentHashMap<String, Object>> documentCache = new Cache<>();
    private Cache<String, List<ConcurrentHashMap<String, Object>>> ListOfocumentCache = new Cache<>();


   Document document;
    public DocumentDAO(String DBName, String CollectionName,Document document) {
        this.document = document;
        this.objectMapper = new ObjectMapper();
        this.documentIndexing = new DocumentLevelIndexing(PATHS.DATABASE_PATH + "/" + DBName + "/" + CollectionName + "/indexing");
        this.path = PATHS.DATABASE_PATH + "/" + DBName + "/" + CollectionName;
        this.documentSearchingService = new DocumentSearchingService(path);
        this.indexingFile = PATHS.DATABASE_PATH + "/" + DBName + "/" + CollectionName + "/indexing/indexing.json";
    }

    public Response insertDocument(String docu) throws IOException {
        ConcurrentHashMap<String, Object> doc = documentSearchingService.deserializeJson(docu);
        int nextFileNumber = documentSearchingService.getNextFileNumber() + 1;
        String hash = UUID.randomUUID().toString();
        doc.put("_id", hash);
        doc.put("_timestamp", new Date());

        String documentFileName = nextFileNumber + ".json";

        document.setJsonData(doc); // set the json -> this is for broadcast
        document.setDocumentFileName(documentFileName); // setfile name

        objectMapper.writeValue(new File(path + "/" + documentFileName), doc); // the file is inserted

        documentIndexing.addIndex(hash, documentFileName); // default indexing by id

        // Pre-chosen keys for indexing
        Set<String> indexedKeys = documentSearchingService.getIndexedKeys(path + "/indexing/");

        // Iterate through all keys in the document to check for existing index files
        for (String key : doc.keySet()) {
            if (!key.equals("_id") && indexedKeys.contains(key)) {
                Object keyValue = doc.get(key);
                String indexPath = path + "/indexing/" + key + ".json";
                // Since we're only indexing pre-chosen keys, we check the existence of the index file
                if (new File(indexPath).exists()) { // Check if the index file exists
                    documentSearchingService.insertIndex(indexPath, keyValue.toString(), documentFileName);
                }
            }
        }
        nextFileNumber++;
        documentSearchingService.updateLastFileNumber(nextFileNumber-1);

        return new Response(Response.Status.SUCCESS, " Document inserted successfully" );

    }



    public synchronized Response deleteDocument(String id) throws IOException {
        String fileName = documentIndexing.getFileName(id);
        if (fileName != null) {
            File file = new File(path + "/" + fileName);
            if (file.delete()) {
                documentIndexing.deleteIndex(id);
                deletePointerFromIndexes(path + "/indexing/", fileName);
                return new Response(Response.Status.SUCCESS, " Document deleted successfully" );

            }
        } else {
            return new Response(Response.Status.UNKNOWN_ERROR, "An error occured while deleting the document ");
        }
        return new Response(Response.Status.DB_ERROR, "Document not found with ID: " + id);
    }



    public synchronized Response updateDocument(String id,String timestamp, String newValuesJson) throws IOException {
        String fileName = documentIndexing.getFileName(id);

        document.setDocumentFileName(fileName); // for broadcast

        if (fileName == null) {
            new Response(Response.Status.DB_ERROR , "Document not found with ID: " + id);
        }

        File file = new File(path + "/" + fileName);

        if (!file.exists() || !file.isFile()) {
            new Response(Response.Status.DB_ERROR , "Document not found with ID: " + id);
        }

        ConcurrentHashMap<String, Object> doc = objectMapper.readValue(file, new TypeReference<>() {});

        // Check if the document's timestamp matches the provided timestamp
        Object docTimestamp = doc.get("_timestamp");
        if (docTimestamp == null || !docTimestamp.toString().equals(timestamp)) {
            return new Response(Response.Status.BAD_REQUEST, "Document update failed due to timestamp mismatch.");
        }

        Map<String, Object> newValues = objectMapper.readValue(newValuesJson, new TypeReference<>() {});

        for (String key : newValues.keySet()) {
            if (!Objects.equals(doc.get(key), newValues.get(key))) {
                // This key has been updated, check if an index exists for this key
                String indexPath = path + "/indexing/" + key + ".json";
                File indexFile = new File(indexPath);
                if (indexFile.exists()) {
                    String oldValue = doc.containsKey(key) ? doc.get(key).toString() : "";
                    String newValue = newValues.get(key).toString();
                    updateIndexForKey(indexPath, oldValue, newValue, fileName);
                }
            }
        }

        doc.putAll(newValues);
        doc.put("_timestamp", new Date());

        document.setJsonData(doc); // for broadcasting

        objectMapper.writeValue(file, doc);

        return new Response(Response.Status.SUCCESS , "updating the document done successfully.");
    }

    public Response findDocumentById(String id) throws IOException {

        // if you find it in the cache stop return it
        ConcurrentHashMap<String, Object> cachedDocument = documentCache.get(id);
        if (cachedDocument != null) {
            return new Response(Response.Status.SUCCESS, cachedDocument);
        }

        String fileName = documentIndexing.getFileName(id);
        if (fileName != null) {
            File file = new File(path + "/" + fileName );
            if (file.exists() && file.isFile()) {
                ConcurrentHashMap<String, Object> doc = objectMapper.readValue(file, new TypeReference<ConcurrentHashMap<String, Object>>() {});
                // Cache the retrieved document
                documentCache.put(id, doc);
                return new Response(Response.Status.SUCCESS , doc);
            }
            else
                return new Response(Response.Status.UNKNOWN_ERROR , "An error occurred. Try again later");
        }
        else
             return new Response(Response.Status.NOT_FOUND , "There is no document with this id");
    }




    public Response findAllDocuments() throws IOException {
        List<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    try (FileInputStream fis = new FileInputStream(file);
                         InputStreamReader isr = new InputStreamReader(fis);
                         BufferedReader reader = new BufferedReader(isr)) {
                        ConcurrentHashMap<String, Object> document = objectMapper.readValue(reader, ConcurrentHashMap.class);
                        documents.add(document);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new Response(Response.Status.SUCCESS , documents);
        } else
            return new Response(Response.Status.NOT_FOUND , "No documents found!");

    }



    public Response findDocuments(ConcurrentHashMap<String, Object> criteria) throws IOException {

        String cacheKey = generateCacheKey(criteria);

        // Check if the result is cached
        List<ConcurrentHashMap<String, Object>> cachedResult = ListOfocumentCache.get(cacheKey);
        if (cachedResult != null) {
            return new Response(Response.Status.SUCCESS, cachedResult);
        }

        List<ConcurrentHashMap<String, Object>> matchingDocuments = new ArrayList<>();
        // Check if criteria contain keys with index files
        Set<String> indexedKeys = documentSearchingService.getIndexedKeys(path + "/indexing/");
        Set<String> criteriaKeys = criteria.keySet();

        if (indexedKeys.isEmpty() || !indexedKeys.containsAll(criteriaKeys)) {
            matchingDocuments.addAll(documentSearchingService.findDocumentsGeneric(path, criteria)); // search in the generic search
        } else {
            List<String> documentIds = null;
            for (String key : criteriaKeys) {
                List<String> keyValues = documentSearchingService.getIndexValuesForKey(path +  "/indexing/",  key, criteria.get(key).toString());
                if (keyValues.isEmpty()) {
                    return new Response(Response.Status.SUCCESS, matchingDocuments);
                }

                if (documentIds == null) {
                    documentIds = new ArrayList<>(keyValues);
                } else {
                    documentIds.retainAll(keyValues);
                }
            }

            if (documentIds != null && !documentIds.isEmpty()) {
                for (String documentId : documentIds) {
                    Response response = findDocumentById(findIdByFileName(documentId));
                    ConcurrentHashMap<String, Object> document = response.getDocument();
                    if (document != null) {
                        matchingDocuments.add(document);
                    }
                }
            }
        }
        ListOfocumentCache.put(cacheKey, matchingDocuments);

        return new Response(Response.Status.SUCCESS, matchingDocuments);
    }

    public String findIdByFileName(String fileName) throws IOException {
        File file = new File(indexingFile);

        JsonObject jsonObject;
        try (FileReader reader = new FileReader(file)) {

            jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        }

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (entry.getValue().getAsString().equals(fileName)) {
                return entry.getKey();
            }
        }

        return null; // Return null if no match found
    }

    // for the find based on specific pair , the pai is hashed
    private String generateCacheKey(Map<String, Object> criteria) {
        StringBuilder keyBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            keyBuilder.append(entry.getKey()).append(entry.getValue());
        }
        return keyBuilder.toString();
    }




}




