package controller_tests.com.nussia.exception;

import com.nussia.exception.*;
import com.nussia.user.UserController;
import com.nussia.user.UserService;
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
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class))).thenThrow(BadRequestException.class);
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleConflictException() throws Exception {
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class))).thenThrow(ConflictException.class);
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldHandleForbiddenException() throws Exception {
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class))).thenThrow(ForbiddenException.class);
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldHandleObjectNotFoundException() throws Exception {
        Mockito.when(service.getUser(ArgumentMatchers.any(Long.class))).thenThrow(ObjectNotFoundException.class);
        mockMvc.perform(get("/users/{userId}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
