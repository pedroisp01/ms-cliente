package com.desafio.ms_cliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MsClienteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsClienteApplication.class, args);
	}
}
