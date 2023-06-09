package com.nussia.request;

import com.nussia.Util;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.request.dto.RequestDTO;
import com.nussia.request.dto.RequestMapper;
import com.nussia.user.User;
import com.nussia.user.UserService;
import com.nussia.user.dto.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final UserService userService;

    @Override
    public RequestDTO addRequest(RequestDTO requestDTO, Long userId) {
        if (requestDTO == null || userId == null) {
            throw new BadRequestException("Invalid parameters: requestDTO, or userId is null");
        }

        Util.validateRequestDTO(requestDTO);

        User user = UserMapper.INSTANCE.toUserEntity(userService.getUser(userId));

        return RequestMapper.INSTANCE.toRequestDTO(
                repository.save(RequestMapper.INSTANCE.toRequestEntity(requestDTO, user)));
    }

    @Transactional
    @Override
    public List<RequestDTO> getRequestsByUserId(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        } else if (!userService.doesUserExist(userId)) {
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
        } else if (!userService.doesUserExist(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        return RequestMapper.INSTANCE.toRequestDTO(repository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request", requestId)));
    }
}
