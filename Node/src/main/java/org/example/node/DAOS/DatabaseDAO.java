package org.example.node.DAOS;

import org.example.node.PATHS;
import org.example.node.Transaction.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class DatabaseDAO {

    private final String PATH = PATHS.DATABASE_PATH;
    public Response createDB(String name) throws IOException {
        Path databasePath = Paths.get(PATH, name);
        if (Files.exists(databasePath)) {
            return new Response(Response.Status.DB_ERROR,  name + "Database already exists");
        }

        Files.createDirectories(databasePath);
        return new Response(Response.Status.SUCCESS, name + " database created successfully" );
    }


    public Response dropDB(String name) throws IOException {
        Path databasePath = Paths.get(PATH , name);
        if (!Files.exists(databasePath)) {
            return new Response(Response.Status.DB_ERROR,  name + "Database does not exist");
        }
        Files.walk(databasePath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        return new Response(Response.Status.SUCCESS, name + " database dropped successfully" );

    }

}
