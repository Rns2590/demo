package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Bean de RestClient apuntado a la API de DummyJSON. Se inyecta en
 * DummyJsonClient — es la única clase que lo usa.
 *
 * Para practicar: se puede endurecer con timeouts propios pasando un
 * ClientHttpRequestFactory a .requestFactory(...) en vez de usar el que
 * trae por defecto.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient dummyJsonRestClient(@Value("${app.dummyjson.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
