package com.nussia.item;

import com.nussia.item.comment.Comment;
import com.nussia.item.dto.SimpleItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Boolean available;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    List<Comment> comments;

    public SimpleItemDTO toSimpleItemDTO() {
        return new SimpleItemDTO(itemId, name, description, available);
    }

}
