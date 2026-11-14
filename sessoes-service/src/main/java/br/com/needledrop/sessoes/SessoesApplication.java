package br.com.needledrop.sessoes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SessoesApplication {
    public static void main(String[] args) {
        SpringApplication.run(SessoesApplication.class, args);
    }
}
