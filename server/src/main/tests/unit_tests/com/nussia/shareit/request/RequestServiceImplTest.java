package unit_tests.com.nussia.shareit.request;

import util.TestUtil;
import com.nussia.shareit.exception.BadRequestException;
import com.nussia.shareit.exception.ObjectNotFoundException;
import com.nussia.shareit.request.Request;
import com.nussia.shareit.request.RequestRepository;
import com.nussia.shareit.request.RequestServiceImpl;
import com.nussia.shareit.request.dto.RequestDTO;
import com.nussia.shareit.request.dto.RequestMapper;
import com.nussia.shareit.user.UserService;
import com.nussia.shareit.user.dto.UserDTO;
import com.nussia.shareit.user.dto.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository repository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RequestServiceImpl service;

    @Test
    void shouldNotAddRequestWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.addRequest(null, null));
    }

    @Test
    void shouldNotAddRequestWithIncorrectUserId() {
        Long userId = 0L;
        RequestDTO requestDTO = TestUtil.getTestRequestDTO();

        Mockito.when(userService.getUser(ArgumentMatchers.anyLong())).thenThrow(BadRequestException.class);

        assertThrows(BadRequestException.class, () -> service.addRequest(requestDTO, userId));
    }

    @Test
    void shouldAddRequests() {
        Long userId = 0L;
        RequestDTO requestDTO = TestUtil.getTestRequestDTO();
        UserDTO userDTO = TestUtil.getTestUserDTO(userId);
        Request request = RequestMapper.INSTANCE.toRequestEntity(requestDTO, UserMapper.INSTANCE.toUserEntity(userDTO));

        Mockito.when(userService.getUser(ArgumentMatchers.anyLong())).thenReturn(userDTO);
        Mockito.when(repository.save(ArgumentMatchers.any(Request.class))).thenReturn(request);

        assertThat(service.addRequest(requestDTO, userId), equalTo(RequestMapper.INSTANCE.toRequestDTO(request)));
    }

    @Test
    void shouldNotGetRequestsByUserIdWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.getRequestsByUserId(null));
    }

    @Test
    void shouldNotGetRequestsByUserIdIfUserDoesNotExist() {
        Long userId = 0L;

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> service.getRequestsByUserId(userId));
    }

    @Test
    void shouldGetRequestsByUserId() {
        Long userId = 0L;

        List<Request> requestList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            requestList.add(TestUtil.getTestRequest((long) i, userId));
        }

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findAllByUser_Id(userId)).thenReturn(requestList);

        assertThat(service.getRequestsByUserId(userId), equalTo(RequestMapper.INSTANCE.toRequestDTO(requestList)));
    }

    @Test
    void shouldNotGetOwnerRequestsWithNullArguments() {
        assertThrows(BadRequestException.class,
                () -> service.getPaginatedRequestsByUserId(null, null, null));
    }

    @Test
    void shouldNotGetOwnerRequestsWithIncorrectPagingSize() {
        Integer from = 0;
        Integer size = -13;
        Long userId = 0L;

        assertThrows(BadRequestException.class, () ->
                service.getPaginatedRequestsByUserId(from, size, userId));
    }

    @Test
    void shouldNotGetOwnerRequestsWithIncorrectPagingIndex() {
        Integer from = -13;
        Integer size = 0;
        Long userId = 0L;

        assertThrows(BadRequestException.class, () ->
                service.getPaginatedRequestsByUserId(from, size, userId));
    }

    @Test
    void shouldGetOwnerRequests() {
        Long firstUserId = 0L;
        Long secondUserId = 1L;

        List<Request> userRequestList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TestUtil.getTestRequest(((long) i + 5), firstUserId);
            userRequestList.add(TestUtil.getTestRequest((long) i, secondUserId));
        }

        Mockito.when(repository.findByUser_IdNot(firstUserId)).thenReturn(userRequestList);

        assertThat(service.getPaginatedRequestsByUserId(null, null, firstUserId),
                equalTo(RequestMapper.INSTANCE.toRequestDTO(userRequestList)));
    }

    @Test
    void shouldNotGetRequestByIdWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.getRequestById(null, null));
    }

    @Test
    void shouldNotGetRequestByIdIfUserDoesNotExist() {
        Long requestID = 0L;
        Long userId = 1L;

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> service.getRequestById(requestID, userId));
    }

    @Test
    void shouldNotGetRequestByIdWithIncorrectRequestId() {
        Long requestID = 0L;
        Long userId = 1L;

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getRequestById(requestID, userId));
    }

    @Test
    void shouldGetRequestById() {
        Long requestID = 0L;
        Long userId = 1L;

        Request request = TestUtil.getTestRequest(requestID, userId);

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(request));

        assertThat(service.getRequestById(requestID, userId), equalTo(RequestMapper.INSTANCE.toRequestDTO(request)));
    }
}