package com.nussia.item.memory;

import com.nussia.Util;
import com.nussia.exception.ForbiddenException;
import com.nussia.item.Item;
import com.nussia.item.ItemDTO;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository {

    private static final Map<Long, Item> items = new HashMap<>();

    public Optional<ItemDTO> addItem(ItemDTO itemDTO, Long ownerId) {
        Item item = Util.getItemFromItemDTO(itemDTO, ownerId);

        return Optional.of(Util.toItemDTO(items.computeIfAbsent(item.getItemId(), x -> item), new ArrayList<>()));
    }

    public Optional<ItemDTO> editItem(ItemDTO itemDTO, Long itemId, Long ownerId) {
        return Optional.ofNullable(items.computeIfPresent(itemId, (id, currentItem) -> {
            if (!currentItem.getOwnerId().equals(ownerId)) {
                throw new ForbiddenException("User with ID " + ownerId + " do not have permission to edit item with ID " +
                        itemId);
            }

            String name = itemDTO.getName();
            if (name != null) {
                currentItem.setName(name);
            }

            String description = itemDTO.getDescription();
            if (description != null) {
                currentItem.setDescription(description);
            }

            Boolean available = itemDTO.getAvailable();
            if (available != null) {
                currentItem.setAvailable(available);
            }

            return currentItem;

        })).flatMap(x -> Optional.of(Util.toItemDTO(x, new ArrayList<>())));
    }

    public Optional<ItemDTO> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId)).flatMap(x -> Optional.of(Util.toItemDTO(x, new ArrayList<>())));
    }

    public List<ItemDTO> getItems(Long ownerId) {
        return items.values().stream().filter(x -> x.getOwnerId().equals(ownerId))
                .map(x -> Util.toItemDTO(x, new ArrayList<>())).collect(Collectors.toList());
    }

    public List<ItemDTO> getItemsBySearchQuery(String searchQuery) {
        return items.values().stream()
                .filter(x -> (x.getName().toLowerCase().contains(searchQuery)
                        || x.getDescription().toLowerCase().contains(searchQuery))
                        && x.getAvailable())
                .map(x -> Util.toItemDTO(x, new ArrayList<>()))
                .collect(Collectors.toList());
    }

}
