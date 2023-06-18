package com.nussia.shareit.request;

import com.nussia.shareit.Parameters;
import com.nussia.shareit.BaseClient;
import com.nussia.shareit.request.dto.RequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addRequest(RequestDTO requestDTO, Long userId) {
        return post("", userId, requestDTO);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(Integer from, Integer size, Long userId) {
        Parameters parameters = Parameters.getInstance()
                .addParameter("from", from)
                .addParameter("size", size);

        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequest(Long requestId, Long userId) {

        return get("/" + requestId, userId);
    }
}
