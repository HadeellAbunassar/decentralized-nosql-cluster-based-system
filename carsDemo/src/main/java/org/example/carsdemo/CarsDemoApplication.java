package org.example.carsdemo;

import org.example.carsdemo.Client.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarsDemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(CarsDemoApplication.class, args);
		Client client = new Client();
		client.ClientMain();

	}

}
