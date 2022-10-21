package ru.egar.employments.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    @Autowired
    public void addInterceptor(TokenRequestInterceptor tri,
                               RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(tri);
    }

}
