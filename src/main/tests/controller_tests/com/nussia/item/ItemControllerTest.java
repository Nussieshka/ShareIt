package controller_tests.com.nussia.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nussia.Util;
import com.nussia.item.ItemController;
import com.nussia.item.ItemService;
import com.nussia.item.comment.dto.CommentDTO;
import com.nussia.item.dto.ItemDTO;
import com.nussia.item.dto.ItemMapper;
import com.nussia.item.dto.SimpleItemDTO;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService service;

    @InjectMocks
    private ItemController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldGetItem() throws Exception {
        Long itemId = 0L;
        Long userId = 1L;
        ItemDTO outItemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO(itemId));

        Mockito.when(service.getItemById(itemId, userId)).thenReturn(outItemDTO);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemId), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(outItemDTO.getName())))
                .andExpect(jsonPath("$.description", Matchers.is(outItemDTO.getDescription())))
                .andExpect(jsonPath("$.available", Matchers.is(outItemDTO.getAvailable())))
                .andExpect(jsonPath("$.requestId", Matchers.is(outItemDTO.getRequestId())));
    }

    @Test
    void shouldPatchItem() throws Exception {
        Long itemId = 0L;
        Long userId = 1L;
        String newName = "Updated Item";
        ItemDTO outItemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO(itemId));
        ItemDTO inputItemDTO = ItemMapper.INSTANCE.toItemDTO(new SimpleItemDTO(
                null, newName, null, null, null));
        outItemDTO.setName(newName);

        Mockito.when(service.editItem(inputItemDTO, itemId, userId)).thenReturn(outItemDTO);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(inputItemDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemId), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(newName)))
                .andExpect(jsonPath("$.description", Matchers.is(outItemDTO.getDescription())))
                .andExpect(jsonPath("$.available", Matchers.is(outItemDTO.getAvailable())))
                .andExpect(jsonPath("$.requestId", Matchers.is(outItemDTO.getRequestId())));
    }

    @Test
    void shouldPostItem() throws Exception {
        Long userId = 1L;
        ItemDTO inputItemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        ItemDTO outItemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO(0L));

        Mockito.when(service.addNewItem(inputItemDTO, userId)).thenReturn(outItemDTO);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(inputItemDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue(Long.class)))
                .andExpect(jsonPath("$.name", Matchers.is(outItemDTO.getName())))
                .andExpect(jsonPath("$.description", Matchers.is(outItemDTO.getDescription())))
                .andExpect(jsonPath("$.available", Matchers.is(outItemDTO.getAvailable())))
                .andExpect(jsonPath("$.requestId", Matchers.is(outItemDTO.getRequestId())));
    }

    @Test
    void shouldGetItems() throws Exception {
        Long userId = 1L;
        List<ItemDTO> outList = new ArrayList<>();

        int arraySize = 5;
        for (int i = 0; i < arraySize; i++) {
            outList.add(ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO((long) i)));
        }

        Mockito.when(service.getItems(null, null, userId)).thenReturn(outList);
        ItemDTO firstItemDTO = outList.get(0);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(arraySize)))
                .andExpect(jsonPath("$[0].id", Matchers.is(firstItemDTO.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", Matchers.is(firstItemDTO.getName())))
                .andExpect(jsonPath("$[0].description", Matchers.is(firstItemDTO.getDescription())))
                .andExpect(jsonPath("$[0].available", Matchers.is(firstItemDTO.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", Matchers.is(firstItemDTO.getRequestId())));
    }

    @Test
    void shouldGetItemsBySearchQuery() throws Exception {
        Long userId = 1L;
        String searchQuery = "test";
        List<ItemDTO> outList = new ArrayList<>();

        int arraySize = 3;
        for (int i = 0; i < arraySize; i++) {
            outList.add(ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO((long) i)));
        }

        Mockito.when(service.getItemsBySearchQuery(null, null, searchQuery, userId)).thenReturn(outList);
        ItemDTO firstItemDTO = outList.get(0);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchQuery)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(arraySize)))
                .andExpect(jsonPath("$[0].id", Matchers.is(firstItemDTO.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", Matchers.is(firstItemDTO.getName())))
                .andExpect(jsonPath("$[0].description", Matchers.is(firstItemDTO.getDescription())))
                .andExpect(jsonPath("$[0].available", Matchers.is(firstItemDTO.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", Matchers.is(firstItemDTO.getRequestId())));
    }

    @Test
    void shouldPostComment() throws Exception {
        Long itemId = 0L;
        Long userId = 1L;
        String authorName = "Author Name";
        CommentDTO outCommentDTO = TestUtil.getTestCommentDTO();
        outCommentDTO.setCreated(Util.localDataTimeToString(LocalDateTime.now()));
        outCommentDTO.setAuthorName(authorName);
        outCommentDTO.setId(0L);
        CommentDTO inputCommentDTO = TestUtil.getTestCommentDTO();

        Mockito.when(service.addNewComment(inputCommentDTO, itemId, userId)).thenReturn(outCommentDTO);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(inputCommentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue(Long.class)))
                .andExpect(jsonPath("$.text", Matchers.is(outCommentDTO.getText())))
                .andExpect(jsonPath("$.authorName", Matchers.is(authorName)))
                .andExpect(jsonPath("$.created", Matchers.notNullValue(String.class)));
    }
}
