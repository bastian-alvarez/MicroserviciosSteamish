package com.gamestore.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Servidor Eureka para descubrimiento de servicios
 * 
 * Este servidor permite que los microservicios se registren y descubran entre sí
 * sin necesidad de conocer las URLs hardcodeadas.
 * 
 * Acceso al dashboard: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

