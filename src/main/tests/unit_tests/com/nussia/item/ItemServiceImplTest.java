package unit_tests.com.nussia.item;

import com.nussia.user.UserRepository;
import util.TestUtil;
import com.nussia.booking.BookingService;
import com.nussia.booking.dto.UserBooking;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ForbiddenException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.item.Item;
import com.nussia.item.ItemRepository;
import com.nussia.item.ItemServiceImpl;
import com.nussia.item.comment.Comment;
import com.nussia.item.comment.CommentRepository;
import com.nussia.item.comment.dto.CommentDTO;
import com.nussia.item.comment.dto.CommentMapper;
import com.nussia.item.dto.ItemDTO;
import com.nussia.item.dto.ItemMapper;
import com.nussia.request.Request;
import com.nussia.request.RequestRepository;
import com.nussia.user.User;
import com.nussia.user.UserService;
import com.nussia.user.dto.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository repository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingService bookingService;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl service;

    @Test
    void shouldNotAddItemWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.addNewItem(null, null));
    }

    @Test
    void shouldNotAddItemWithItemDTOId() {
        assertThrows(BadRequestException.class, () -> service.addNewItem(ItemMapper.INSTANCE.toItemDTO(
                TestUtil.getTestItemDTO(0L)), 0L));
    }

    @Test
    void shouldNotAddItemIfUserDoesNotExist() {
        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> service.addNewItem(ItemMapper.INSTANCE.toItemDTO(
                TestUtil.getTestItemDTO()), 0L));
    }

    @Test
    void shouldNotAddItemIfRequestDoesNotExist() {
        Long ownerId = 0L;

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(requestRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(TestUtil.getTestUser(ownerId)));

        assertThrows(ObjectNotFoundException.class, () -> service.addNewItem(ItemMapper.INSTANCE.toItemDTO(
                TestUtil.getTestItemDTOWithRequest(0L)), ownerId));
    }

    @Test
    void shouldAddItemWithRequest() {
        Long ownerId = 0L;
        Long requestId = 0L;

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(true);

        Request request = TestUtil.getTestRequest(requestId, ownerId);

        Mockito.when(requestRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(request));

        ItemDTO testItemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTOWithRequest(request.getId()));
        testItemDTO.setId(0L);

        Mockito.when(repository.save(ArgumentMatchers.any(Item.class))).thenReturn(
                TestUtil.getItemWithId(testItemDTO, request, ownerId));

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(TestUtil.getTestUser(ownerId)));

        assertThat(service.addNewItem(ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTOWithRequest(request.getId())),
                        ownerId), equalTo(testItemDTO));
    }

    @Test
    void shouldAddItem() {
        Long ownerId = 0L;

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(true);

        ItemDTO testItemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO(0L));

        Mockito.when(repository.save(ArgumentMatchers.any(Item.class))).thenReturn(TestUtil.getItemWithId(testItemDTO, ownerId));

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(TestUtil.getTestUser(ownerId)));

        assertThat(service.addNewItem(ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO()), ownerId),
                equalTo(testItemDTO));
    }

    @Test
    void shouldNotGetItemsWhenUserDoesNotExist() {
        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> service.getItems(null, null, 0L));
    }

    @Test
    void shouldGetItems() {
        Long userId = 0L;

        List<Item> list = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            list.add(TestUtil.getItemWithId(TestUtil.getTestItemDTO((long) (i + 5)), userId));
        }

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findAllByOwnerIdOrderByItemIdAsc(userId)).thenReturn(list);

        assertThat(service.getItems(null, null, userId), equalTo(list.stream()
                .map(x -> ItemMapper.INSTANCE.toItemDTO(x, new ArrayList<>())).collect(Collectors.toList())));
    }

    @Test
    void shouldNotGetItemsWithIncorrectPagingSize() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = -13;

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.getItems(from, size, userId));
    }

    @Test
    void shouldNotGetItemsWithIncorrectPagingIndex() {
        Long userId = 0L;
        Integer from = -13;
        Integer size = 2;

        Mockito.when(userService.doesUserExist(ArgumentMatchers.anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.getItems(from, size, userId));
    }

    @Test
    void shouldNotEditItemWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.editItem(null, null, null));
    }

    @Test
    void shouldNotEditItemWithIncorrectId() {
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.editItem(
                ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO()), 0L, 0L));
    }

    @Test
    void shouldNotEditNotOwnedItem() {
        Long itemOwnerId = 3L;
        Long requesterId = 2L;
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        Item item = ItemMapper.INSTANCE.toItemEntity(itemDTO, TestUtil.getTestUser(itemOwnerId));

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> service.editItem(
                ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO()), 0L, requesterId));
    }

    @Test
    void shouldEditItem() {
        Long itemOwnerId = 3L;
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        Item item = ItemMapper.INSTANCE.toItemEntity(itemDTO, TestUtil.getTestUser(itemOwnerId));
        String secondDescription = "Another description of cool headphones";

        itemDTO.setDescription(secondDescription);

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItem_ItemId(ArgumentMatchers.anyLong())).thenReturn(null);
        Mockito.when(bookingService.getLastBooking(ArgumentMatchers.anyLong())).thenReturn(null);
        Mockito.when(bookingService.getNextBooking(ArgumentMatchers.anyLong())).thenReturn(null);
        Mockito.when(repository.save(ArgumentMatchers.any(Item.class))).thenAnswer(x -> x.getArgument(0, Item.class));

        assertThat(service.editItem(itemDTO, 0L, itemOwnerId), equalTo(itemDTO));
    }

    @Test
    void shouldEditItemWithComments() {
        Long itemOwnerId = 3L;
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        Item item = ItemMapper.INSTANCE.toItemEntity(itemDTO, TestUtil.getTestUser(itemOwnerId));
        String secondDescription = "Another description of cool headphones";
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            comments.add(TestUtil.getTestComment((long) i, (long) i, item));
        }

        itemDTO.setDescription(secondDescription);
        itemDTO.setComments(comments.stream().map(CommentMapper.INSTANCE::toCommentDTO).collect(Collectors.toList()));

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItem_ItemId(ArgumentMatchers.anyLong())).thenReturn(comments);
        Mockito.when(bookingService.getLastBooking(ArgumentMatchers.anyLong())).thenReturn(null);
        Mockito.when(bookingService.getNextBooking(ArgumentMatchers.anyLong())).thenReturn(null);
        Mockito.when(repository.save(ArgumentMatchers.any(Item.class))).thenAnswer(x -> x.getArgument(0, Item.class));

        assertThat(service.editItem(itemDTO, 0L, itemOwnerId), equalTo(itemDTO));
    }

    @Test
    void shouldEditItemWithBookings() {
        Long itemOwnerId = 3L;
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        Item item = ItemMapper.INSTANCE.toItemEntity(itemDTO, TestUtil.getTestUser(itemOwnerId));
        String secondDescription = "Another description of cool headphones";

        itemDTO.setDescription(secondDescription);
        Map.Entry<UserBooking, UserBooking> bookings =
                new AbstractMap.SimpleEntry<>(TestUtil.getTestUserBooking(0L, 0L),
                        TestUtil.getTestUserBooking(1L, 1L));
        itemDTO.setLastBooking(bookings.getKey());
        itemDTO.setNextBooking(bookings.getValue());

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItem_ItemId(ArgumentMatchers.anyLong())).thenReturn(null);
        Mockito.when(bookingService.getLastBooking(ArgumentMatchers.anyLong())).thenReturn(bookings.getKey());
        Mockito.when(bookingService.getNextBooking(ArgumentMatchers.anyLong())).thenReturn(bookings.getValue());
        Mockito.when(repository.save(ArgumentMatchers.any(Item.class))).thenAnswer(x -> x.getArgument(0, Item.class));

        assertThat(service.editItem(itemDTO, 0L, itemOwnerId), equalTo(itemDTO));
    }

    @Test
    void shouldNotGetItemWhenIdIsNull() {
        assertThrows(BadRequestException.class, () -> service.getItemById(null, null));
    }

    @Test
    void shouldNotGetItemWithIncorrectId() {
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getItemById(0L, 0L));
    }

    @Test
    void shouldGetItemsWithoutBookingsWhenOwnerIdIsNotEqualToRequesterId() {
        Long itemOwnerId = 3L;
        Long requesterId = 2L;
        Long itemId = 1L;
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO(itemId));
        Item item = TestUtil.getItemWithId(itemDTO, itemOwnerId);

        Mockito.when(commentRepository.findAllByItem_ItemId(ArgumentMatchers.anyLong())).thenReturn(null);
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));

        assertThat(service.getItemById(itemId, requesterId), equalTo(itemDTO));

        Mockito.verify(bookingService, Mockito.never()).getLastBooking(ArgumentMatchers.anyLong());
        Mockito.verify(bookingService, Mockito.never()).getNextBooking(ArgumentMatchers.anyLong());

    }

    @Test
    void shouldGetItemsWithBookingsWhenOwnerIdIsEqualToRequesterId() {
        Long itemOwnerId = 3L;
        Long itemId = 1L;
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO(itemId));
        Item item = TestUtil.getItemWithId(itemDTO, itemOwnerId);

        Map.Entry<UserBooking, UserBooking> bookings =
                new AbstractMap.SimpleEntry<>(TestUtil.getTestUserBooking(0L, 0L),
                        TestUtil.getTestUserBooking(1L, 1L));
        itemDTO.setLastBooking(bookings.getKey());
        itemDTO.setNextBooking(bookings.getValue());

        Mockito.when(commentRepository.findAllByItem_ItemId(ArgumentMatchers.anyLong())).thenReturn(null);
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingService.getLastBooking(ArgumentMatchers.anyLong())).thenReturn(bookings.getKey());
        Mockito.when(bookingService.getNextBooking(ArgumentMatchers.anyLong())).thenReturn(bookings.getValue());

        assertThat(service.getItemById(itemId, itemOwnerId), equalTo(itemDTO));
    }

    @Test
    void shouldGetEmptyListIfSearchQueryIsBlank() {
        assertThat(service.getItemsBySearchQuery(null, null, "", 0L), equalTo(List.of()));
    }

    @Test
    void shouldGetItemsBySearchQuery() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(TestUtil.getItemWithId(TestUtil.getTestItemDTO((long) i), (long) i));
        }

        Mockito.when(repository.
                findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                        ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean()))
                .thenReturn(items);

        List<ItemDTO> outList = service.getItemsBySearchQuery(null, null, "head", 0L);

        assertThat(outList, equalTo(items.stream()
                .map(x -> ItemMapper.INSTANCE.toItemDTO(x, new ArrayList<>())).collect(Collectors.toList())));
    }

    @Test
    void shouldNotGetItemsBySearchQueryWithIncorrectPagingSize() {
        Integer from = 0;
        Integer size = -13;

        assertThrows(BadRequestException.class, () ->
                service.getItemsBySearchQuery(from, size, "head", 0L));
    }

    @Test
    void shouldNotGetItemsBySearchQueryWithIncorrectPagingIndex() {
        Integer from = -13;
        Integer size = 0;

        assertThrows(BadRequestException.class, () ->
                service.getItemsBySearchQuery(from, size, "head", 0L));
    }

    @Test
    void shouldNotAddCommentsWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.addNewComment(null, null, null));
    }

    @Test
    void shouldNotAddCommentWithCommentDTOId() {
        CommentDTO commentDTO = TestUtil.getTestCommentDTO();
        commentDTO.setId(1L);
        assertThrows(BadRequestException.class,
                () -> service.addNewComment(commentDTO, 0L, 0L));
    }

    @Test
    void shouldNotAddCommentFromNotBorrower() {
        Mockito.when(bookingService.isBorrowedByUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(false);
        assertThrows(BadRequestException.class,
                () -> service.addNewComment(TestUtil.getTestCommentDTO(), 0L, 0L));
    }

    @Test
    void shouldAddComments() {
        CommentDTO commentDTO = TestUtil.getTestCommentDTO();
        Long userId = 1L;
        Long itemId = 0L;
        User user = TestUtil.getTestUser(userId);
        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), 0L);
        Comment outComment = CommentMapper.INSTANCE.toCommentEntity(commentDTO, user, item);
        Mockito.when(bookingService.isBorrowedByUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(userService.getUser(Mockito.anyLong())).thenReturn(UserMapper.INSTANCE.toUserDTO(user));
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(outComment);

        assertThat(service.addNewComment(commentDTO, itemId, userId),
                equalTo(CommentMapper.INSTANCE.toCommentDTO(outComment)));
    }
}