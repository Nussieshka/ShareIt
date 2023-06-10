package controller_tests.com.nussia.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nussia.user.UserController;
import com.nussia.user.UserService;
import com.nussia.user.dto.UserDTO;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldPostUsers() throws Exception {
        UserDTO testUserDTO = TestUtil.getTestUserDTO();
        UserDTO outUserDTO = TestUtil.getTestUserDTO(0L);

        Mockito.when(service.createUser(any(UserDTO.class))).thenReturn(outUserDTO);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(testUserDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue(Long.class)))
                .andExpect(jsonPath("$.name", Matchers.is(testUserDTO.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(testUserDTO.getEmail())));
    }

    @Test
    void shouldPatchUsers() throws Exception {
        Long userId = 0L;
        String newEmailAddress = "new@email.com";
        UserDTO outUserDTO = new UserDTO(null, null, newEmailAddress);
        UserDTO testUserDTO = TestUtil.getTestUserDTO(userId);
        testUserDTO.setEmail(newEmailAddress);

        Mockito.when(service.editUser(any(UserDTO.class), eq(userId))).thenReturn(testUserDTO);

        mockMvc.perform(patch("/users/" + userId)
                        .content(mapper.writeValueAsString(outUserDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userId), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(testUserDTO.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(newEmailAddress)));
    }

    @Test
    void shouldGetUser() throws Exception {
        Long userId = 0L;
        UserDTO outUserDTO = TestUtil.getTestUserDTO(userId);

        Mockito.when(service.getUser(userId)).thenReturn(outUserDTO);

        mockMvc.perform(get("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userId), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(outUserDTO.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(outUserDTO.getEmail())));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        Long userId = 0L;
        UserDTO outUserDTO = TestUtil.getTestUserDTO(userId);

        Mockito.when(service.deleteUser(userId)).thenReturn(outUserDTO);

        mockMvc.perform(delete("/users/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userId), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(outUserDTO.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(outUserDTO.getEmail())));
    }

    @Test
    void shouldGetUsers() throws Exception {
        List<UserDTO> outList = new ArrayList<>();

        int arraySize = 5;
        for (int i = 0; i < arraySize; i++) {
            outList.add(TestUtil.getTestUserDTO((long) i));
        }

        Mockito.when(service.getUsers()).thenReturn(outList);
        UserDTO firstUserDTO = outList.get(0);

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(arraySize)))
                .andExpect(jsonPath("$[0].id", Matchers.is(firstUserDTO.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", Matchers.is(firstUserDTO.getName())))
                .andExpect(jsonPath("$[0].email", Matchers.is(firstUserDTO.getEmail())));
    }
}
