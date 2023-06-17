package com.nussia.shareit.request;

import com.nussia.shareit.request.dto.RequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestBody @Valid RequestDTO requestDTO,
                                                  @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId) {
        return client.addRequest(requestDTO, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return client.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@PositiveOrZero @RequestParam(required = false) Integer from,
                                                 @Positive @RequestParam(required = false) Integer size,
                                                 @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId) {
        return client.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable @PositiveOrZero Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id")  @PositiveOrZero long userId) {
        return client.getRequest(requestId, userId);
    }
}
