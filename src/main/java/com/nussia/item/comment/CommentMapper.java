package com.nussia.item.comment;

import com.nussia.Util;
import com.nussia.item.Item;
import com.nussia.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(imports = { Util.class, LocalDateTime.class })
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "authorName", expression = "java(comment.getUser().getName())")
    @Mapping(target = "created", expression = "java(Util.localDataTimeToString(comment.getCreatedAt()))")
    CommentDTO toCommentDTO(Comment comment);

    @Mapping(source = "commentDTO.id", target = "id")
    @Mapping(target = "createdAt", expression = "java(commentDTO.getCreated() == null ?" +
            " LocalDateTime.now().plusSeconds(1) : Util.stringToLocalDataTime(commentDTO.getCreated()))")

    Comment toCommentEntity(CommentDTO commentDTO, User user, Item item);
}
