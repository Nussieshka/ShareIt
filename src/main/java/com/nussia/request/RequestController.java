package com.nussia.request;

import com.nussia.request.dto.RequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDTO> postRequest(@RequestBody RequestDTO requestDTO,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.addRequest(requestDTO, userId));
    }

    @GetMapping
    public ResponseEntity<List<RequestDTO>> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.getRequestsByUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDTO>> getAllRequests(@RequestParam(required = false) Integer from,
                                                           @RequestParam(required = false) Integer size,
                                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.getPaginatedRequestsByUserId(from, size, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDTO> getRequest(@PathVariable Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.getRequestById(requestId, userId));
    }
}
