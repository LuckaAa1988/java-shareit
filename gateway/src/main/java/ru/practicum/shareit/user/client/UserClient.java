package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.client.RestTemplateFactory;
import ru.practicum.shareit.user.dto.UserRequest;

import java.util.Map;

@Component
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(RestTemplateFactory restTemplateFactory) {
        super(restTemplateFactory.getRestTemplate(API_PREFIX));
    }

    public ResponseEntity<Object> getUser(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> createUser(UserRequest userRequest) {
        return post("", userRequest);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserRequest userRequest) {
        return patch("/" + userId, userRequest);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId);
    }

    public ResponseEntity<Object> findAll(Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}",0L, parameters);
    }
}
