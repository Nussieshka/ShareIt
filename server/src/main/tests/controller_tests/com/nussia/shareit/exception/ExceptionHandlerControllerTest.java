package controller_tests.com.nussia.shareit.exception;

import com.nussia.shareit.exception.*;
import com.nussia.shareit.user.UserController;
import com.nussia.shareit.user.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ExceptionHandlerControllerTest {

    @Mock
    private UserService service;

    @InjectMocks
    private UserController testController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(testController)
                .setControllerAdvice(ExceptionHandlerController.class)
                .build();
    }

    @Test
    void shouldHandleBadRequestException() throws Exception {
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class))).thenThrow(new BadRequestException());
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleConflictException() throws Exception {
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class))).thenThrow(new ConflictException());
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldHandleForbiddenException() throws Exception {
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class))).thenThrow(new ForbiddenException());
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldHandleObjectNotFoundException() throws Exception {
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class))).thenThrow(new ObjectNotFoundException());
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleBadRequestExceptionWithMessage() throws Exception {
        String creativeMessage = "a creative message";
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class)))
                .thenThrow(new BadRequestException(creativeMessage));
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.is(creativeMessage)));
    }

    @Test
    void shouldHandleConflictExceptionWithMessage() throws Exception {
        String creativeMessage = "a creative message";
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class)))
                .thenThrow(new ConflictException(creativeMessage));
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", Matchers.is(creativeMessage)));
    }

    @Test
    void shouldHandleForbiddenExceptionWithMessage() throws Exception {
        String creativeMessage = "a creative message";
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class)))
                .thenThrow(new ForbiddenException(creativeMessage));
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", Matchers.is(creativeMessage)));
    }

    @Test
    void shouldHandleObjectNotFoundExceptionWithMessage() throws Exception {
        String creativeMessage = "a creative message";
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class)))
                .thenThrow(new ObjectNotFoundException(creativeMessage));
        mockMvc.perform(get("/users/{userId}", 0L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", Matchers.is(creativeMessage)));
    }
}
