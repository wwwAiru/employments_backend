package com.egar.employments.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.egartech.sdk.api.AuthorizationRequestInterceptor;

import java.util.List;

@Configuration
public class Config {

    @Bean
    public RestTemplate restTemplate(@Autowired AuthorizationRequestInterceptor interceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(interceptor));
        return restTemplate;
    }
}

