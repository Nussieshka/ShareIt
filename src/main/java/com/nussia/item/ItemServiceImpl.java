package com.nussia.item;

import com.nussia.Util;
import com.nussia.booking.BookingService;
import com.nussia.booking.dto.UserBooking;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ForbiddenException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.item.comment.Comment;
import com.nussia.item.comment.dto.CommentDTO;
import com.nussia.item.comment.dto.CommentMapper;
import com.nussia.item.comment.CommentRepository;
import com.nussia.item.dto.ItemDTO;
import com.nussia.item.dto.ItemMapper;
import com.nussia.request.Request;
import com.nussia.request.RequestRepository;
import com.nussia.user.User;
import com.nussia.user.dto.UserMapper;
import com.nussia.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service("JpaItemService")
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final RequestRepository requestRepository;

    public ItemServiceImpl(ItemRepository repository, CommentRepository commentRepository,
                           @Qualifier("JpaUserService") UserService userService,
                           BookingService bookingService, RequestRepository requestRepository) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.bookingService = bookingService;
        this.requestRepository = requestRepository;
    }

    @Transactional
    @Override
    public List<ItemDTO> getItems(Integer from, Integer size, Long userId) {

        if (!userService.doesUserExist(userId)) {
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

        if (!item.getOwnerId().equals(ownerId)) {
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

        Util.validateItemDTO(itemDTO);

        if (!userService.doesUserExist(ownerId)) {
            throw new ObjectNotFoundException("User", ownerId);
        }

        Long requestId = itemDTO.getRequestId();
        if (requestId == null) {
            return ItemMapper.INSTANCE.toItemDTO(repository.save(ItemMapper.INSTANCE.toItemEntity(itemDTO, ownerId)),
                    new ArrayList<>());
        }

        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException("Request", requestId));

        return ItemMapper.INSTANCE.toItemDTO(repository.save(ItemMapper.INSTANCE.toItemEntity(itemDTO, request, ownerId)),
                new ArrayList<>());
    }

    @Transactional
    @Override
    public ItemDTO getItemById(Long itemId, Long userId) {
        Item item = getItem(itemId);
        if (Objects.equals(item.getOwnerId(), userId)) {
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

        return new AbstractMap.SimpleEntry<>(bookingService.getLastBooking(itemId),
                bookingService.getNextBooking(itemId));
    }

    public List<ItemDTO> getItemDTOFromItemList(Iterable<Item> items, Long userId) {
        return StreamSupport.stream(items.spliterator(), false)
                .map(x -> toItemDTOWithBookings(x, userId)).collect(Collectors.toList());
    }

    private ItemDTO toItemDTOWithBookings(Item item, Long ownerId) {
        if (Objects.equals(ownerId, item.getOwnerId())) {
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
        } else if (!bookingService.isBorrowedByUser(userId, itemId)) {
            throw new BadRequestException("This user is not a booker");
        }

        Util.validateCommentDTO(commentDTO);

        User user = UserMapper.INSTANCE.toUserEntity(userService.getUser(userId));
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
}
