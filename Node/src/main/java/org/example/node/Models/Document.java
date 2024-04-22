package org.example.node.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Service
public class Document {

   private String DBName;
   private String CollectionName;
   private String DocumentFileName;
   private ConcurrentHashMap<String, Object> jsonData;






}
