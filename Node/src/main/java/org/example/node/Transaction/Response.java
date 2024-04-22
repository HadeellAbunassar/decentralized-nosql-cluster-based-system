package org.example.node.Transaction;


import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Getter
public class Response {
    public enum Status {
        SUCCESS,
        BAD_REQUEST,
        NOT_FOUND,
        INVALID_QUERY,
        UNKNOWN_ERROR,
        IO_EXCEPTION,

        DB_ERROR,
    }

    private Status status;
    private String message;
    ConcurrentHashMap<String, Object> document;

    List<ConcurrentHashMap<String, Object>> documents;


    public Response(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Response(Status status,List<ConcurrentHashMap<String, Object>> documents){
        this.status = status;
        this.documents = documents;
    }

    public Response(Status status,ConcurrentHashMap<String, Object> document){
        this.status = status;
        this.document = document;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

