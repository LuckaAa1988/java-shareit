package ru.practicum.shareit.item.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.client.RestTemplateFactory;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemCreate;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(RestTemplateFactory restTemplateFactory) {
        super(restTemplateFactory.getRestTemplate(API_PREFIX));
    }

    public ResponseEntity<Object> createItem(ItemCreate itemCreate, Long userId) {
        return post("", userId, itemCreate);
    }

    public ResponseEntity<Object> updateItem(ItemCreate itemCreate, Long userId, Long itemId) {
        return patch("/" + itemId, userId, itemCreate);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItems(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(CommentRequest commentRequest, Long userId, Long itemId) {
        return post("/" + itemId + "/comment", userId, commentRequest);
    }

    public ResponseEntity<Object> searchItems(String text, Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }
}
