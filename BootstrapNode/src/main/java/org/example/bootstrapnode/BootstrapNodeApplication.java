package org.example.bootstrapnode;

import org.example.bootstrapnode.Controllers.BootstrapIndexController;
import org.example.bootstrapnode.Services.StartClusterService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class BootstrapNodeApplication {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(BootstrapNodeApplication.class, args);
	}


}
