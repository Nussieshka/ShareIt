package com.nussia.shareit.item;

import com.nussia.shareit.Util;
import com.nussia.shareit.booking.BookingRepository;
import com.nussia.shareit.booking.dto.BookingMapper;
import com.nussia.shareit.booking.dto.UserBooking;
import com.nussia.shareit.booking.model.BookingStatus;
import com.nussia.shareit.exception.BadRequestException;
import com.nussia.shareit.exception.ForbiddenException;
import com.nussia.shareit.exception.ObjectNotFoundException;
import com.nussia.shareit.item.comment.Comment;
import com.nussia.shareit.item.comment.dto.CommentDTO;
import com.nussia.shareit.item.comment.dto.CommentMapper;
import com.nussia.shareit.item.comment.CommentRepository;
import com.nussia.shareit.item.dto.ItemDTO;
import com.nussia.shareit.item.dto.ItemMapper;
import com.nussia.shareit.request.Request;
import com.nussia.shareit.request.RequestRepository;
import com.nussia.shareit.user.User;
import com.nussia.shareit.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service("JpaItemService")
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final RequestRepository requestRepository;

    public ItemServiceImpl(ItemRepository repository, CommentRepository commentRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository, RequestRepository requestRepository) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional
    @Override
    public List<ItemDTO> getItems(Integer from, Integer size, Long userId) {

        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }

        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        return Util.getPaginatedResult(from, size,
                () -> getItemDTOFromItemList(repository.findAllByOwnerIdOrderByItemIdAsc(userId), userId),
                () -> getItemDTOFromItemList(
                        repository.findAllByOwnerIdOrderByItemIdAsc(userId,
                                PageRequest.of(from / size, size)), userId));
    }

    @Transactional
    @Override
    public ItemDTO editItem(ItemDTO itemDTO, Long itemId, Long ownerId) {
        if (ownerId == null || itemId == null || itemDTO == null) {
            throw new BadRequestException("Invalid parameters: ownerId, itemId, or itemDTO is null.");
        }

        Item item = repository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item", itemId));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("User with ID " + ownerId + " do not have permission to edit item with ID " +
                    itemId);
        }

        Util.updateItemEntityFromDTO(item, itemDTO);

        return ItemMapper.INSTANCE.toItemDTO(repository.save(item), getComments(itemId),
                getNextAndLastUserBookings(itemId));
    }

    @Override
    public ItemDTO addNewItem(ItemDTO itemDTO, Long ownerId) {
        if (ownerId == null || itemDTO == null) {
            throw new BadRequestException("Invalid parameters: ownerId, or itemDTO is null");
        }

        if (itemDTO.getId() != null) {
            throw new BadRequestException("Cannot add item with itemId");
        }

        if (!userRepository.existsById(ownerId)) {
            throw new ObjectNotFoundException("User", ownerId);
        }

        User owner = getUser(ownerId);

        Long requestId = itemDTO.getRequestId();
        if (requestId == null) {
            return ItemMapper.INSTANCE.toItemDTO(repository.save(ItemMapper.INSTANCE.toItemEntity(itemDTO, owner)),
                    new ArrayList<>());
        }

        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException("Request", requestId));

        return ItemMapper.INSTANCE.toItemDTO(repository.save(ItemMapper.INSTANCE.toItemEntity(itemDTO, request, owner)),
                new ArrayList<>());
    }

    @Transactional
    @Override
    public ItemDTO getItemById(Long itemId, Long userId) {
        Item item = getItem(itemId);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            return ItemMapper.INSTANCE.toItemDTO(item, getComments(itemId), getNextAndLastUserBookings(itemId));
        } else {
            return ItemMapper.INSTANCE.toItemDTO(item, getComments(itemId));
        }
    }

    @Transactional
    @Override
    public List<ItemDTO> getItemsBySearchQuery(Integer from, Integer size, String searchQuery, Long userId) {
        if (searchQuery.isBlank()) {
            return List.of();
        }

        return Util.getPaginatedResult(from, size,
                () -> getItemDTOFromItemList(repository.
                        findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(searchQuery,
                                searchQuery, true), userId),
                () -> getItemDTOFromItemList(repository.
                        findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(searchQuery,
                                searchQuery, true, PageRequest.of(from / size, size)), userId));

    }

    private Item getItem(Long itemId) {
        if (itemId == null) {
            throw new BadRequestException("Invalid parameter: itemId is null");
        }

        return repository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item", itemId));
    }

    private Map.Entry<UserBooking, UserBooking> getNextAndLastUserBookings(Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("Item ID is null");
        }

        return new AbstractMap.SimpleEntry<>(getLastBooking(itemId), getNextBooking(itemId));
    }

    public List<ItemDTO> getItemDTOFromItemList(Iterable<Item> items, Long userId) {
        return StreamSupport.stream(items.spliterator(), false)
                .map(x -> toItemDTOWithBookings(x, userId)).collect(Collectors.toList());
    }

    private ItemDTO toItemDTOWithBookings(Item item, Long ownerId) {
        if (Objects.equals(ownerId, item.getOwner().getId())) {
            return ItemMapper.INSTANCE.toItemDTO(item, getComments(item.getItemId()),
                    getNextAndLastUserBookings(item.getItemId()));
        } else {
            return ItemMapper.INSTANCE.toItemDTO(item, getComments(item.getItemId()));
        }
    }

    @Transactional
    @Override
    public CommentDTO addNewComment(CommentDTO commentDTO, Long itemId, Long userId) {
        if (commentDTO == null || itemId == null || userId == null) {
            throw new BadRequestException("Invalid parameters: commentDTO, itemId, or userId is null");
        }

        Long commentId = commentDTO.getId();

        if (commentId != null) {
            throw new BadRequestException("Cannot add comment with ID");
        } else if (!bookingRepository.existsByBorrowingUser_IdAndItem_ItemIdAndBookingStatusEqualsAndEndDateBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new BadRequestException("This user is not a booker");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));
        Item item = this.getItem(itemId);

        return CommentMapper.INSTANCE.toCommentDTO(this.commentRepository.save(
                CommentMapper.INSTANCE.toCommentEntity(commentDTO, user, item)));
    }

    private List<Comment> getComments(Long itemId) {
        if (itemId == null) {
            throw new BadRequestException("Invalid parameter: itemId is null");
        }
        return this.commentRepository.findAllByItem_ItemId(itemId);
    }

    private User getUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));
    }

    private UserBooking getLastBooking(Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("Item ID is null");
        }

        LocalDateTime now = LocalDateTime.now();

        return bookingRepository.findFirstByItem_ItemIdAndStartDateBeforeAndBookingStatusEqualsOrderByEndDateDesc(
                itemId, now, BookingStatus.APPROVED).map(BookingMapper.INSTANCE::toUserBooking).orElse(null);
    }

    private UserBooking getNextBooking(Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("Item ID is null");
        }

        LocalDateTime now = LocalDateTime.now();

        return bookingRepository.findFirstByItem_ItemIdAndStartDateAfterAndBookingStatusEqualsOrderByStartDateAsc(
                itemId, now, BookingStatus.APPROVED).map(BookingMapper.INSTANCE::toUserBooking).orElse(null);
    }
}
