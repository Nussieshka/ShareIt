package com.nussia.item;

import com.nussia.Util;
import com.nussia.user.User;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public CommentDTO toCommentDTO() {
        return new CommentDTO(id, text, user.getName(), Util.timestampToString(createdAt));
    }
}
