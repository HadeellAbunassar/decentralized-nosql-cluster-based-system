package org.example.node.Indices;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;



public class KeyValueLevelIndexing {

    public static void addIndex(String indexPath, String keyValue, String documentName) throws IOException {
        JsonObject jsonObject = initializeJsonObject(new File(indexPath));
        JsonArray list = jsonObject.getAsJsonArray(keyValue);

        if (list == null) {
            list = new JsonArray();
        }

        if (!list.contains(new JsonPrimitive(documentName))) {
            list.add(new JsonPrimitive(documentName));
        }

        jsonObject.add(keyValue, list);

        saveChanges(new File(indexPath), jsonObject);
    }

    private static JsonObject initializeJsonObject(File file) throws IOException {
        JsonObject jsonObject;
        if (file.exists()) {
            jsonObject = readJsonFile(file);
        } else {
            jsonObject = new JsonObject();
        }
        return jsonObject;
    }

    public static JsonObject readJsonFile(File file) throws IOException {
        if (file.exists() && file.length() != 0) {
            try (Reader reader = new FileReader(file)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonObject()) {
                    return element.getAsJsonObject();
                } else {
                    return new JsonObject();
                }
            } catch (JsonIOException | JsonSyntaxException e) {
                System.err.println("Error reading or parsing JSON file " + file.getPath() + ": " + e.getMessage());
                return new JsonObject();
            }
        } else {
            return new JsonObject();
        }
    }


    public static void saveChanges(File file, JsonObject jsonObject) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(jsonObject, writer);
        }
    }




    public static void updateIndexForKey(String indexPath, String oldKeyValue, String newKeyValue, String fileName) throws IOException {
        JsonObject indexObject = new JsonObject();
        File indexFile = new File(indexPath);

        // Load existing or initialize new index
        if (indexFile.exists()) {
            indexObject = readJsonFile(indexFile);
        }

        // Remove document from old key value's index, if necessary
        if (indexObject.has(oldKeyValue)) {
            JsonArray oldArray = indexObject.getAsJsonArray(oldKeyValue);
            oldArray.remove(new JsonPrimitive(fileName)); // Ensure fileName is correctly identified
            if (oldArray.size() == 0) {
                indexObject.remove(oldKeyValue); // Remove key if no more references
            } else {
                indexObject.add(oldKeyValue, oldArray);
            }
        }

        // Add document to new key value's index
        JsonArray newArray = indexObject.getAsJsonArray(newKeyValue);
        if (newArray == null) {
            newArray = new JsonArray();
        }
        if (!newArray.contains(new JsonPrimitive(fileName))) {
            newArray.add(new JsonPrimitive(fileName));
        }
        indexObject.add(newKeyValue, newArray);

        // Save updated index
        saveChanges(indexFile, indexObject);
    }


    public static void deletePointerFromIndexes(String directoryPath, String documentId) throws IOException {
        File dir = new File(directoryPath);
        File[] indexFiles = dir.listFiles((d, name) -> name.endsWith(".json") && !name.startsWith("_"));

        if (indexFiles == null) return;

        for (File indexFile : indexFiles) {
            boolean modified = false;
            JsonObject jsonObject = readJsonFile(indexFile);

            // Create a list to hold keys for removal to avoid ConcurrentModificationException
            List<String> keysToRemove = new ArrayList<>();

            // Iterate over each key in the index file
            for (String key : jsonObject.keySet()) {
                JsonElement element = jsonObject.get(key);
                if (element.isJsonArray()) {
                    JsonArray jsonArray = element.getAsJsonArray();

                    // Remove the documentId from the JsonArray if present
                    JsonPrimitive documentPrimitive = new JsonPrimitive(documentId);
                    if (jsonArray.contains(documentPrimitive)) {
                        jsonArray.remove(documentPrimitive);
                        modified = true;

                        // If the jsonArray is empty after removal, mark the key for removal
                        if (jsonArray.size() == 0) {
                            keysToRemove.add(key);
                        }
                    }
                }
            }

            // Remove keys that are associated with empty JsonArrays
            for (String keyToRemove : keysToRemove) {
                jsonObject.remove(keyToRemove);
                modified = true; // Mark as modified if we remove any keys
            }

            // Save changes to the index file if any modifications were made
            if (modified) {
                saveChanges(indexFile, jsonObject);
            }
        }
    }




}
