package com.nussia.item.jpa;

import com.nussia.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String name,
                                                                                              String description,
                                                                                              Boolean available);
    List<Item> findAllByOwnerIdOrderByItemIdAsc(Long ownerId);

}
