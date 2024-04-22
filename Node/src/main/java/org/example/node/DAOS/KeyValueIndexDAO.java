package org.example.node.DAOS;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.node.Indices.KeyValueLevelIndexing;
import org.example.node.PATHS;
import java.io.File;
import java.io.IOException;


public class KeyValueIndexDAO {

    public static String createIndexFile(String DB, String Collection, String indexKey) {
        String msg;
        String collectionDirectoryPath = PATHS.DATABASE_PATH + "/" + DB + "/" + Collection;
        String indexingDirectoryPath = collectionDirectoryPath + File.separator + "indexing";

        File dir = new File(collectionDirectoryPath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json")); // get all doucmnets

        String indexPath = indexingDirectoryPath + File.separator + indexKey + ".json";

        File indexFile = new File(indexPath);
        try {
            if (indexFile.createNewFile()) {
                msg = "Index file created: " + indexFile.getName();
            } else {
                msg = "Index file already exists: " + indexFile.getName();
            }
        } catch (IOException e) {
            msg = "An error occurred while creating the index file: " + e.getMessage();
        }

        insertIndexToOldDocuments(files,indexKey,indexPath);

        return msg;
    }

    public static String deleteIndexFile(String DB, String Collection, String indexKey) {
        String collectionDirectoryPath = PATHS.DATABASE_PATH + "/" + DB + "/" + Collection;
        String indexingDirectoryPath = collectionDirectoryPath + File.separator + "indexing";
        String indexPath = indexingDirectoryPath + File.separator + indexKey + ".json";

        File indexFile = new File(indexPath);
        if (indexFile.exists()) {
            boolean deleted = indexFile.delete();
            if (deleted) {
                return indexKey + ".json has been successfully deleted.";
            } else {
                return "Failed to delete " + indexKey + ".json.";
            }
        } else {
            return indexKey + ".json does not exist.";
        }
    }

    private static void insertIndexToOldDocuments(File[] files, String indexKey, String indexPath){
        if (files == null) return; // if no documnets .. return no need to conitune

        for (File file : files) {
            try {
                JsonNode rootNode = new ObjectMapper().readTree(file);
                JsonNode keyValue = rootNode.get(indexKey);
                if (keyValue != null) {
                    if (keyValue.isArray()) {
                        for (final JsonNode objNode : keyValue) {
                            KeyValueLevelIndexing.addIndex(indexPath, objNode.asText(), file.getName());
                        }
                    } else {
                        KeyValueLevelIndexing.addIndex(indexPath, keyValue.asText(), file.getName());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
            }
        }
    }

}
