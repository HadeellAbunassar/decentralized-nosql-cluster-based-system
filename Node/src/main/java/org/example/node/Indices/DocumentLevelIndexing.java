package org.example.node.Indices;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class DocumentLevelIndexing {
    private Map<String, String> index;
    private final String indexPath;
    private final ObjectMapper objectMapper;

    public DocumentLevelIndexing(String collectionPath) {
        this.indexPath = collectionPath + "/indexing.json" ;
        this.objectMapper = new ObjectMapper();
        loadIndex();
    }

    public void addIndex(String id, String fileName) {
        index.put(id, fileName);
        saveIndex();
    }

    public void deleteIndex(String id) {
        if (index.containsKey(id)) {
            index.remove(id);
            saveIndex();
        }
    }

    public String getFileName(String id) {
        return index.get(id);
    }

    @SuppressWarnings("unchecked")
    private void loadIndex() {
        File indexFile = new File(indexPath);
        if (indexFile.exists() && indexFile.length() > 0) {
            try {
                index = objectMapper.readValue(indexFile, HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
                index = new HashMap<>();
            }
        } else {
            index = new HashMap<>();
        }
    }


    private void saveIndex() {
        File indexFile = new File(indexPath);
        try {
            objectMapper.writeValue(indexFile, index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
