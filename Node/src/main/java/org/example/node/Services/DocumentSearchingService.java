package org.example.node.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.example.node.Indices.KeyValueLevelIndexing;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentSearchingService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String path;


    public DocumentSearchingService(String path){
        this.path = path;
    }


    public List<String> getIndexValuesForKey(String collectionDirectory, String key, String value) throws IOException {
        String indexPath = collectionDirectory + File.separator + key + ".json";
        JsonObject indexObject = KeyValueLevelIndexing.readJsonFile(new File(indexPath));
        JsonArray valueArray = indexObject.getAsJsonArray(value);
        if (valueArray != null) {
            List<String> documentIds = new ArrayList<>();
            for (JsonElement element : valueArray) {
                documentIds.add(element.getAsString());
            }
            return documentIds;
        }
        return Collections.emptyList();
    }

    public Set<String> getIndexedKeys(String collectionDirectory) {
        Set<String> indexedKeys = new HashSet<>();
        File dir = new File(collectionDirectory);
        File[] indexFiles = dir.listFiles((d, name) -> name.endsWith(".json") && !name.startsWith("_"));
        if (indexFiles != null) {
            for (File indexFile : indexFiles) {
                indexedKeys.add(indexFile.getName().replace(".json", ""));
            }
        }
        return indexedKeys;
    }

    public List<ConcurrentHashMap<String, Object>> findDocumentsGeneric(String collectionDirectory, Map<String, Object> criteria) {
        List<ConcurrentHashMap<String, Object>> matchingDocuments = new ArrayList<>();
        File[] files = new File(collectionDirectory).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    try {
                        ConcurrentHashMap<String, Object> document = objectMapper.readValue(file, new TypeReference<ConcurrentHashMap<String, Object>>() {});
                        boolean match = true;
                        for (String key : criteria.keySet()) {
                            if (!document.containsKey(key) || !document.get(key).equals(criteria.get(key))) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            matchingDocuments.add(document);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return matchingDocuments;
    }

    public synchronized void updateLastFileNumber(int lastFileNumber) throws IOException {
        String lastFileNumberPath = path + "/_lastFileNumber.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(lastFileNumberPath))) {
            writer.write(Integer.toString(lastFileNumber));
        }
    }

    public synchronized int getNextFileNumber() throws IOException {
        String lastFileNumberPath = path + "/_lastFileNumber.txt";
        File lastFileNumberFile = new File(lastFileNumberPath);
        if (!lastFileNumberFile.exists()) {
            return 0;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(lastFileNumberFile))) {
            String lastNumberStr = reader.readLine();
            return Integer.parseInt(lastNumberStr);
        } catch (NumberFormatException e) {
            throw new IOException("Failed to parse last file number", e);
        }
    }

    // this to insert the index
    public synchronized void insertIndex(String indexPath, String keyValue, String documentFileName) throws IOException {
        File indexFile = new File(indexPath);
        JsonObject indexObject = new JsonObject();
        if (indexFile.exists()) {
            indexObject = KeyValueLevelIndexing.readJsonFile(indexFile);
        }
        JsonArray documentsArray = indexObject.getAsJsonArray(keyValue);
        if (documentsArray == null) {
            documentsArray = new JsonArray();
        }
        JsonPrimitive jsonFileName = new JsonPrimitive(documentFileName);
        if (!documentsArray.contains(jsonFileName)) {
            documentsArray.add(jsonFileName);
            indexObject.add(keyValue, documentsArray);
        }
        KeyValueLevelIndexing.saveChanges(indexFile, indexObject);
    }

    public ConcurrentHashMap<String, Object> deserializeJson(String jsonDocument) throws IOException {
        return objectMapper.readValue(jsonDocument, new TypeReference<ConcurrentHashMap<String, Object>>() {});
    }

}
