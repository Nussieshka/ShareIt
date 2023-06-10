package controller_tests.com.nussia.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nussia.Util;
import com.nussia.request.RequestController;
import com.nussia.request.RequestService;
import com.nussia.request.dto.RequestDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import util.TestUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {

    @Mock
    private RequestService service;

    @InjectMocks
    private RequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldPostRequests() throws Exception {
        RequestDTO requestDTO = TestUtil.getTestRequestDTO();
        RequestDTO outRequestDTO = TestUtil.getTestRequestDTO();
        Long userId = 0L;
        outRequestDTO.setId(userId);
        outRequestDTO.setCreated(Util.localDataTimeToString(LocalDateTime.now()));

        Mockito.when(service.addRequest(any(RequestDTO.class), anyLong())).thenReturn(outRequestDTO);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDTO))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userId), Long.class))
                .andExpect(jsonPath("$.description", Matchers.is(requestDTO.getDescription())))
                .andExpect(jsonPath("$.created", Matchers.notNullValue(String.class)));
    }

    @Test
    void shouldGetUserRequests() throws Exception {
        Long userId = 0L;
        List<RequestDTO> outList = new ArrayList<>();

        int arraySize = 5;
        for (int i = 0; i < arraySize; i++) {
            RequestDTO requestDTO = TestUtil.getTestRequestDTO((long) i);
            requestDTO.setCreated(Util.localDataTimeToString(LocalDateTime.now()));
            outList.add(requestDTO);
        }

        Mockito.when(service.getRequestsByUserId(anyLong())).thenReturn(outList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(arraySize)))
                .andExpect(jsonPath("$[0].id", Matchers.is(0L), Long.class))
                .andExpect(jsonPath("$[0].description", Matchers.is(outList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].created", Matchers.notNullValue(String.class)));
    }

    @Test
    void shouldGetRequests() throws Exception {
        Long userId = 0L;
        List<RequestDTO> outList = new ArrayList<>();

        int arraySize = 5;
        for (int i = 0; i < arraySize; i++) {
            RequestDTO requestDTO = TestUtil.getTestRequestDTO((long) i);
            requestDTO.setCreated(Util.localDataTimeToString(LocalDateTime.now()));
            outList.add(requestDTO);
        }

        Mockito.when(service.getPaginatedRequestsByUserId(any(), any(), anyLong())).thenReturn(outList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(arraySize)))
                .andExpect(jsonPath("$[0].id", Matchers.is(0L), Long.class))
                .andExpect(jsonPath("$[0].description", Matchers.is(outList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].created", Matchers.notNullValue(String.class)));
    }

    @Test
    void shouldGetUser() throws Exception {
        Long userId = 0L;
        Long requestId = 1L;
        RequestDTO outRequestDTO = TestUtil.getTestRequestDTO(userId);
        outRequestDTO.setId(requestId);
        outRequestDTO.setCreated(Util.localDataTimeToString(LocalDateTime.now()));

        Mockito.when(service.getRequestById(requestId, userId)).thenReturn(outRequestDTO);

        mockMvc.perform(get("/requests/" + requestId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(requestId), Long.class))
                .andExpect(jsonPath("$.description", Matchers.is(outRequestDTO.getDescription())))
                .andExpect(jsonPath("$.created", Matchers.notNullValue(String.class)));
    }
}
