package com.technocorp.DropboxDosGuri;

import com.technocorp.DropboxDosGuri.armazenamento.ArmazenamentoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ArmazenamentoProperties.class)
public class DropboxDosGuriApplication {

	public static void main(String[] args) {
		SpringApplication.run(DropboxDosGuriApplication.class, args);
	}

}
