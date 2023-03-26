package com.nussia.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(@Qualifier("JpaItemService") ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDTO> patchItem(@RequestBody ItemDTO itemDTO, @PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.editItem(itemDTO, itemId, userId));
    }

    @PostMapping()
    public ResponseEntity<ItemDTO> postItem(@RequestBody ItemDTO itemDTO, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.addNewItem(itemDTO, userId));
    }

    @GetMapping()
    public ResponseEntity<List<ItemDTO>> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> getItemsBySearchQuery(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItemsBySearchQuery(text, userId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDTO> postComment(@RequestBody CommentDTO commentDTO, @PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.addNewComment(commentDTO, itemId, userId));
    }
}
