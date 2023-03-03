package com.nussia.item;

import com.nussia.exception.ForbiddenException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private static final AtomicLong idCounter = new AtomicLong(1);

    private static final Map<Long, Item> items = new HashMap<>();

    @Override
    public Optional<ItemDTO> addItem(ItemDTO itemDTO, Long ownerId) {
        Item item = getItemFromItemDTO(itemDTO, ownerId);
        return Optional.ofNullable(items.computeIfAbsent(item.getItemId(), x -> item).toItemDTO());
    }

    @Override
    public Optional<ItemDTO> editItem(ItemDTO itemDTO, Long itemId, Long ownerId) {
        return Optional.ofNullable(items.computeIfPresent(itemId, (id, currentItem) -> {
            if (!currentItem.getOwnerId().equals(ownerId)) {
                throw new ForbiddenException();
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

        })).flatMap(x -> Optional.ofNullable(x.toItemDTO()));
    }

    @Override
    public Optional<ItemDTO> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId)).flatMap(x -> Optional.ofNullable(x.toItemDTO()));
    }

    @Override
    public List<ItemDTO> getItems(Long ownerId) {
        return items.values().stream().filter(x -> x.getOwnerId().equals(ownerId))
                .map(Item::toItemDTO).collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> getItemsBySearchQuery(String searchQuery) {
        return items.values().stream()
                .filter(x -> (x.getName().toLowerCase().contains(searchQuery)
                        || x.getDescription().toLowerCase().contains(searchQuery))
                && x.getAvailable())
                .map(Item::toItemDTO)
                .collect(Collectors.toList());
    }

    private Item getItemFromItemDTO(ItemDTO itemDTO, Long userId) {
        return getItemFromItemDTO(idCounter.getAndIncrement(), itemDTO, userId);
    }

    private Item getItemFromItemDTO(Long id, ItemDTO itemDTO, Long userId) {
        return new Item(id, userId, itemDTO.getName(), itemDTO.getDescription(),
                itemDTO.getAvailable(), new HashMap<>());
    }
}
