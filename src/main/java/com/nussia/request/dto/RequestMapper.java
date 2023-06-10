package com.nussia.request.dto;

import com.nussia.Util;
import com.nussia.item.Item;
import com.nussia.item.dto.ItemMapper;
import com.nussia.request.Request;
import com.nussia.user.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(imports = { Util.class, LocalDateTime.class }, uses = ItemMapper.class)
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(target = "created", expression = "java(Util.localDataTimeToString(request.getCreatedAt()))")
    RequestDTO toRequestDTO(Request request);

    @Mapping(source = "requestDTO.id", target = "id")
    @Mapping(source = "items", target = "items")
    @Mapping(target = "createdAt", expression = "java(requestDTO.getCreated() == null ?" +
            " LocalDateTime.now().plusSeconds(1) : Util.stringToLocalDataTime(requestDTO.getCreated()))")
    Request toRequestEntity(RequestDTO requestDTO, List<Item> items, User user);

    @IterableMapping(elementTargetType = RequestDTO.class)
    List<RequestDTO> toRequestDTO(Iterable<Request> requests);

    default Request toRequestEntity(RequestDTO requestDTO, User user) {
        return INSTANCE.toRequestEntity(requestDTO, new ArrayList<>(), user);
    }
}
