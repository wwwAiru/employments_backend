package ru.egar.employments.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final TokenRequestInterceptor tokenRequestInterceptor;

    @Bean
    public RestTemplate cuerpRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(tokenRequestInterceptor);
        return restTemplate;
    }
}
