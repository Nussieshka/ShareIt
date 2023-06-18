package com.nussia.shareit.item;

import com.nussia.shareit.Parameters;
import com.nussia.shareit.BaseClient;
import com.nussia.shareit.item.comment.dto.CommentDTO;
import com.nussia.shareit.item.dto.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";


    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItem(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> editItem(ItemDTO itemDTO, long itemId, long userId) {
        return patch("/" + itemId, userId, itemDTO);
    }

    public ResponseEntity<Object> addItem(ItemDTO itemDTO, long userId) {
        return post("", userId, itemDTO);
    }

    public ResponseEntity<Object> getItems(Integer from, Integer size, long userId) {
        Parameters parameters = Parameters.getInstance().addParameter("from", from).addParameter("size", size);

        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemBySearchQuery(Integer from, Integer size, String text, long userId) {
        Parameters parameters = Parameters.getInstance().addParameter("from", from).addParameter("size", size)
                .addParameter("text", text);
        return get("/search?from={from}&size={size}&text={text}", userId, parameters);
    }

    public ResponseEntity<Object> postComment(CommentDTO commentDTO, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDTO);
    }
}
