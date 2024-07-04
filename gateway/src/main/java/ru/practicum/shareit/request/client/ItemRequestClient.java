package ru.practicum.shareit.request.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.client.RestTemplateFactory;
import ru.practicum.shareit.request.dto.ItemRequestRequest;

import java.util.Map;

@Component
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(RestTemplateFactory restTemplateFactory) {
        super(restTemplateFactory.getRestTemplate(API_PREFIX));
    }

    public ResponseEntity<Object> addRequestItem(ItemRequestRequest itemRequestRequest, Long authorId) {
        return post("", authorId, itemRequestRequest);
    }

    public ResponseEntity<Object> getAllOwnerRequestItem(Integer from, Integer size, Long authorId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", authorId, parameters);
    }

    public ResponseEntity<Object> getAllRequestItem(Integer from, Integer size, Long authorId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", authorId, parameters);
    }

    public ResponseEntity<Object> getRequestItem(Long requestId, Long authorId) {
        return get("/" + requestId, authorId);
    }
}
