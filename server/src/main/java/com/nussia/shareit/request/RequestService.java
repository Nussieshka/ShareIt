package com.nussia.shareit.request;

import com.nussia.shareit.request.dto.RequestDTO;

import java.util.List;

public interface RequestService {

    RequestDTO addRequest(RequestDTO requestDTO, Long userId);

    List<RequestDTO> getRequestsByUserId(Long userId);

    List<RequestDTO> getPaginatedRequestsByUserId(Integer from, Integer size, Long userId);

    RequestDTO getRequestById(Long requestId, Long userId);

}
