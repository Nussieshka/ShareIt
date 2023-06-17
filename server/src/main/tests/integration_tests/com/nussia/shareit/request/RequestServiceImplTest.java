package integration_tests.com.nussia.shareit.request;

import com.nussia.shareit.Util;
import com.nussia.shareit.booking.BookingServiceImpl;
import com.nussia.shareit.item.ItemServiceImpl;
import com.nussia.shareit.request.Request;
import com.nussia.shareit.request.RequestService;
import com.nussia.shareit.request.RequestServiceImpl;
import com.nussia.shareit.request.dto.RequestDTO;
import com.nussia.shareit.request.dto.RequestMapper;
import com.nussia.shareit.user.User;
import com.nussia.shareit.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import util.IntegrationTestUtil;
import util.TestPersistenceConfig;
import util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@TestPropertySource(properties = { "db.name = test_share_it" })
@SpringJUnitConfig( { TestPersistenceConfig.class, UserServiceImpl.class, ItemServiceImpl.class,
        BookingServiceImpl.class, RequestServiceImpl.class, IntegrationTestUtil.class })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTest {

    private final RequestService requestService;

    private final IntegrationTestUtil util;

    @Test
    void shouldAddRequests() {
        RequestDTO requestDTO = TestUtil.getTestRequestDTO();
        User user = util.createUser();
        Request request = util.createRequest(requestDTO, user.getId());

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDTO.getDescription()));
        assertThat(request.getCreatedAt(), notNullValue());
        assertThat(request.getItems().size(), equalTo(0));
        assertThat(request.getUser(), equalTo(user));
    }

    @Test
    void shouldGetRequestsById() {
        User user = util.createUser();
        Long userId = user.getId();
        Request request = util.createRequest(userId);
        RequestDTO repositoryRequest = requestService.getRequestById(request.getId(), userId);

        assertThat(request.getDescription(), equalTo(repositoryRequest.getDescription()));
        assertThat(Util.localDataTimeToString(request.getCreatedAt()), equalTo(repositoryRequest.getCreated()));
        assertThat(request.getItems().size(), equalTo(repositoryRequest.getItems().size()));
        assertThat(request.getUser(), equalTo(user));
    }

    @Test
    void shouldGetOwnerRequests() {
        User firstUser = util.createUser();
        Long firstUserId = firstUser.getId();
        User secondUser = util.createUser();
        Long secondUserId = secondUser.getId();

        List<RequestDTO> secondUserRequestList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            util.createRequest(firstUserId);
            secondUserRequestList.add(RequestMapper.INSTANCE.toRequestDTO(util.createRequest(secondUserId)));
        }

        assertThat(secondUserRequestList,
                equalTo(requestService.getPaginatedRequestsByUserId(null, null, firstUserId)));
    }

    @Test
    void shouldGetRequests() {
        User user = util.createUser();
        Long userId = user.getId();

        List<RequestDTO> requestList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            requestList.add(RequestMapper.INSTANCE.toRequestDTO(util.createRequest(userId)));
        }

        assertThat(requestList,
                equalTo(requestService.getRequestsByUserId(userId)));
    }
}
