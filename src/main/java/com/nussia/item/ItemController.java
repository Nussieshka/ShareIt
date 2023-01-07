package com.nussia.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService ITEM_SERVICE;

    @GetMapping("/{itemId}")
    private ResponseEntity<ItemDTO> getItem(@PathVariable Long itemId) {
        return ResponseEntity.of(ITEM_SERVICE.getItemById(itemId));
    }

    @PatchMapping("/{itemId}")
    private ResponseEntity<ItemDTO> patchItem(@RequestBody ItemDTO itemDTO, @PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.of(ITEM_SERVICE.editItem(itemDTO, itemId, userId));
    }

    @PostMapping()
    private ResponseEntity<ItemDTO> postItem(@RequestBody ItemDTO itemDTO, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.of(ITEM_SERVICE.addNewItem(itemDTO, userId));
    }

    @GetMapping()
    private List<ItemDTO> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ITEM_SERVICE.getItems(userId);
    }

    @GetMapping("/search")
    private List<ItemDTO> getItemsBySearchQuery(@RequestParam String text) {
        return ITEM_SERVICE.getItemsBySearchQuery(text);
    }
}
