package com.nussia.shareit.request;

import com.nussia.shareit.Util;
import com.nussia.shareit.exception.BadRequestException;
import com.nussia.shareit.exception.ObjectNotFoundException;
import com.nussia.shareit.request.dto.RequestDTO;
import com.nussia.shareit.request.dto.RequestMapper;
import com.nussia.shareit.user.User;
import com.nussia.shareit.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final UserRepository userRepository;

    @Override
    public RequestDTO addRequest(RequestDTO requestDTO, Long userId) {
        if (requestDTO == null || userId == null) {
            throw new BadRequestException("Invalid parameters: requestDTO, or userId is null");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));

        return RequestMapper.INSTANCE.toRequestDTO(
                repository.save(RequestMapper.INSTANCE.toRequestEntity(requestDTO, user)));
    }

    @Transactional
    @Override
    public List<RequestDTO> getRequestsByUserId(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        } else if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        return RequestMapper.INSTANCE.toRequestDTO(repository.findAllByUser_Id(userId));
    }

    @Transactional
    @Override
    public List<RequestDTO> getPaginatedRequestsByUserId(Integer from, Integer size, Long userId) {

        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }

        return Util.getPaginatedResult(from, size,
                () -> RequestMapper.INSTANCE.toRequestDTO(repository.findByUser_IdNot(userId)),
                () -> RequestMapper.INSTANCE.toRequestDTO(repository.findByUser_IdNot(userId,
                        PageRequest.of(from / size, size)).getContent()));
    }

    @Transactional
    @Override
    public RequestDTO getRequestById(Long requestId, Long userId) {
        if (requestId == null || userId == null) {
            throw new BadRequestException("Invalid parameters: requestId or userId is null");
        } else if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        return RequestMapper.INSTANCE.toRequestDTO(repository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request", requestId)));
    }
}
