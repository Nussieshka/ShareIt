package com.nussia.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String name,
                                                                                              String description,
                                                                                              Boolean available);
    List<Item> findAllByOwnerIdOrderByItemIdAsc(Long ownerId);

}
