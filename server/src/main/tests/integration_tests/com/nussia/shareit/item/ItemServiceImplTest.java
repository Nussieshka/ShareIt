package integration_tests.com.nussia.shareit.item;

import com.nussia.shareit.booking.BookingServiceImpl;
import com.nussia.shareit.booking.model.BookingStatus;
import com.nussia.shareit.item.Item;
import com.nussia.shareit.item.ItemService;
import com.nussia.shareit.item.ItemServiceImpl;
import com.nussia.shareit.item.comment.dto.CommentDTO;
import com.nussia.shareit.item.dto.ItemDTO;
import com.nussia.shareit.item.dto.ItemMapper;
import com.nussia.shareit.item.dto.SimpleItemDTO;
import com.nussia.shareit.request.RequestServiceImpl;
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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@TestPropertySource(properties = { "db.name = test_share_it" })
@SpringJUnitConfig( { TestPersistenceConfig.class, UserServiceImpl.class, ItemServiceImpl.class,
        BookingServiceImpl.class, RequestServiceImpl.class, IntegrationTestUtil.class })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final ItemService itemService;

    private final IntegrationTestUtil util;

    @Test
    void shouldAddItems() {
        SimpleItemDTO itemDTO = TestUtil.getTestItemDTO();
        Long ownerId = util.createUser().getId();
        Item item = util.createItem(itemDTO, ownerId);

        assertThat(item.getItemId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDTO.getName()));
        assertThat(item.getDescription(), equalTo(itemDTO.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDTO.getAvailable()));
        assertThat(item.getOwner().getId(), equalTo(ownerId));
    }

    @Test
    void shouldEditItems() {
        Long ownerId =  util.createUser().getId();
        Item item = util.createItem(ownerId);

        SimpleItemDTO itemDTO = TestUtil.getTestItemDTO();
        itemDTO.setName("Cool Headphones");
        itemDTO.setDescription("Another description of cool headphones");
        itemDTO.setAvailable(false);

        Long itemId = item.getItemId();
        itemService.editItem(ItemMapper.INSTANCE.toItemDTO(itemDTO), itemId, ownerId);
        Item editedItem = util.selectItemById(itemId);

        assertThat(editedItem.getItemId(), equalTo(itemId));
        assertThat(editedItem.getName(), equalTo(itemDTO.getName()));
        assertThat(editedItem.getDescription(), equalTo(itemDTO.getDescription()));
        assertThat(editedItem.getAvailable(), equalTo(itemDTO.getAvailable()));
        assertThat(editedItem.getOwner().getId(), equalTo(ownerId));
    }

    @Test
    void shouldGetItems() {
        User user = util.createUser();
        Long ownerId = user.getId();

        for (int i = 0; i < 3; i++) {
            util.createItem(ownerId);
        }

        assertThat(itemService.getItems(null, null, ownerId).size(), equalTo(3));
    }

    @Test
    void shouldGetItemById() {
        Long ownerId =  util.createUser().getId();
        Item item = util.createItem(ownerId);
        ItemDTO repositoryItem = itemService.getItemById(item.getItemId(), ownerId);

        assertThat(item.getName(), equalTo(repositoryItem.getName()));
        assertThat(item.getDescription(), equalTo(repositoryItem.getDescription()));
        assertThat(item.getAvailable(), equalTo(repositoryItem.getAvailable()));
        assertThat(item.getOwner().getId(), equalTo(ownerId));
    }

    @Test
    void shouldGetItemBySearchQuery() {
        Long ownerId = util.createUser().getId();
        SimpleItemDTO itemDTO1 = TestUtil.getTestItemDTO();
        SimpleItemDTO itemDTO2 = TestUtil.getTestItemDTO();
        itemDTO2.setName("Wireless Headphones");
        SimpleItemDTO itemDTO3 = TestUtil.getTestItemDTO();
        itemDTO3.setName("Old Radio");
        itemDTO3.setDescription("This radio doesn't support wireless headphones");

        util.createItem(itemDTO1, ownerId);
        util.createItem(itemDTO2, ownerId);
        util.createItem(itemDTO3, ownerId);

        List<ItemDTO> firstQuery = itemService.getItemsBySearchQuery(
                null, null, "wireless", ownerId);

        List<ItemDTO> secondQuery = itemService.getItemsBySearchQuery(
                null, null, "phone", ownerId);

        assertThat(firstQuery.size(), equalTo(2));
        assertThat(secondQuery.size(), equalTo(3));
    }

    @Test
    void shouldAddComments() {
        Item item = util.createItem(util.createUser().getId());
        Long itemId = item.getItemId();
        Long borrowerId = util.createUser().getId();
        util.insertBooking(TestUtil.getTestBooking(null, borrowerId,
                item, BookingStatus.APPROVED, -123, -122));

        CommentDTO comment = TestUtil.getTestCommentDTO();
        itemService.addNewComment(comment, itemId, borrowerId);

        List<CommentDTO> comments = itemService.getItemById(itemId, item.getOwner().getId()).getComments();

        assertThat(comments.size(), equalTo(1));
        assertThat(comments.get(0).getText(), equalTo(comment.getText()));
    }

}
