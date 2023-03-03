package com.nussia.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemById(itemId));
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
    public ResponseEntity<List<ItemDTO>> getItemsBySearchQuery(@RequestParam String text) {
        return ResponseEntity.ok(itemService.getItemsBySearchQuery(text));
    }
}
