package ru.egar.employments.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenRequestInterceptor implements ClientHttpRequestInterceptor {

    private final HttpServletRequest httpServletRequest;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && !token.isEmpty()) {
            request.getHeaders().add("Authorization", token);
        }
        return execution.execute(request, body);
    }
}
