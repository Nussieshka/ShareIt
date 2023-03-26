package com.nussia.item.jpa;

import com.nussia.item.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem_ItemId(Long item_itemId);
}
