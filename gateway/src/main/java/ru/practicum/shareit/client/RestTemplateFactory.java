package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RestTemplateFactory {

    @Value("${shareit-server.url}")
    private String serverUrl;

    private final Map<String, RestTemplate> restTemplateCache = new HashMap<>();

    public RestTemplate getRestTemplate(String prefix) {
        return restTemplateCache.computeIfAbsent(prefix, p -> new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + p))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }
}
