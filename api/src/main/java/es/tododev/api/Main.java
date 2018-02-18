package es.tododev.api;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Main {


	public static void main(String[] args) throws IOException {
		SpringApplication.run(Main.class, args);
	}
	
}
