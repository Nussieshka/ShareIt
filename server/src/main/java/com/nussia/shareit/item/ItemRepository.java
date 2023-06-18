package com.nussia.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String name,
                                                                                              String description,
                                                                                              Boolean available);

    Page<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String name,
                                                                                              String description,
                                                                                              Boolean available,
                                                                                              Pageable pageable);
    List<Item> findAllByOwnerIdOrderByItemIdAsc(Long ownerId);

    Page<Item> findAllByOwnerIdOrderByItemIdAsc(Long ownerId, Pageable pageable);

}
