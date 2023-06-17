package com.nussia.shareit.item;

import com.nussia.shareit.item.comment.dto.CommentDTO;
import com.nussia.shareit.item.dto.ItemDTO;
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
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient client;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable @PositiveOrZero long itemId,
                                          @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId) {
        return client.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestBody ItemDTO itemDTO, @PathVariable @PositiveOrZero long itemId,
                                             @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId) {
        return client.editItem(itemDTO, itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestBody @Valid ItemDTO itemDTO,
                                            @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId) {
        return client.addItem(itemDTO, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestParam(required = false) @PositiveOrZero Integer from,
                                                  @RequestParam(required = false) @Positive Integer size,
                                                  @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId) {
        return client.getItems(from, size, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearchQuery(@RequestParam(required = false) @PositiveOrZero Integer from,
                                                               @RequestParam(required = false) @Positive Integer size,
                                                               @RequestParam String text,
                                                               @RequestHeader("X-Sharer-User-Id")
                                                                   @PositiveOrZero long userId) {
        return client.getItemBySearchQuery(from, size, text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestBody @Valid CommentDTO commentDTO,
                                                @PathVariable @PositiveOrZero Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") @PositiveOrZero Long userId) {
        return client.postComment(commentDTO, itemId, userId);
    }
}
