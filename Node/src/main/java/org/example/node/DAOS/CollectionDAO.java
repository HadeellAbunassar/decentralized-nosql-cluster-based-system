package org.example.node.DAOS;

import org.example.node.PATHS;
import org.example.node.Transaction.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class CollectionDAO {

    private final String PATH = PATHS.DATABASE_PATH;

    public Response createCollection(String collectionName, String dbName) {
        try {
            Path databasePath = Paths.get(PATH, dbName);
            Path collectionPath = databasePath.resolve(collectionName);
            if (Files.exists(collectionPath)) {
                return new Response(Response.Status.DB_ERROR, "Collection already exists");
            }
            Files.createDirectories(collectionPath);
            Path indexingDirectory = collectionPath.resolve("indexing");
            Files.createDirectories(indexingDirectory);
            Path indexingFile = indexingDirectory.resolve("indexing.json");
            Files.createFile(indexingFile);
            return new Response(Response.Status.SUCCESS, collectionName + " collection created successfully in database " + dbName);
        } catch (IOException e) {
            return new Response(Response.Status.IO_EXCEPTION, "IO Exception occurred ");
        }
    }

    public Response dropCollection(String collectionName, String dbName) {
        try {
            Path databasePath = Paths.get(PATH, dbName);
            Path collectionPath = databasePath.resolve(collectionName);
            if (!Files.exists(collectionPath)) {
                return new Response(Response.Status.DB_ERROR, "Collection does not exist");
            }
            Files.walk(collectionPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            return new Response(Response.Status.SUCCESS, collectionName + " collection dropped successfully from database " + dbName);
        } catch (IOException e) {
            return new Response(Response.Status.IO_EXCEPTION, "IO Exception occurred ");
        }
    }
}
