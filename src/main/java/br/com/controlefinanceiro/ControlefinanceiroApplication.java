package br.com.controlefinanceiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ControlefinanceiroApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlefinanceiroApplication.class, args);
	}

}
