package com.nussia.item.jpa;

import com.nussia.Util;
import com.nussia.booking.BookingService;
import com.nussia.booking.UserBooking;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ForbiddenException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.item.Comment;
import com.nussia.item.Item;
import com.nussia.item.ItemDTO;
import com.nussia.item.ItemService;
import com.nussia.item.CommentDTO;
import com.nussia.user.User;
import com.nussia.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service("JpaItemService")
public class JpaItemService implements ItemService {

    private final JpaItemRepository repository;

    private final JpaCommentRepository commentRepository;

    private final UserService userService;

    private final BookingService bookingService;


    public JpaItemService(JpaItemRepository repository, JpaCommentRepository commentRepository,
                          @Qualifier("JpaUserService") UserService userService,
                          BookingService bookingService) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @Transactional
    @Override
    public List<ItemDTO> getItems(Long userId) {

        if (!userService.isUserExists(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        return getItemDTOFromItemList(repository.findAllByOwnerIdOrderByItemIdAsc(userId), userId);
    }

    @Transactional
    @Override
    public ItemDTO editItem(ItemDTO itemDTO, Long itemId, Long ownerId) {
        if (ownerId == null || itemId == null || itemDTO == null) {
            throw new BadRequestException("Invalid parameters: ownerId, itemId, or itemDTO is null.");
        }

        Item item = repository.getById(itemId);

        if (!item.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("User with ID " + ownerId + " do not have permission to edit item with ID " +
                    itemId);
        }

        String name = itemDTO.getName();
        if (name != null) {
            item.setName(name);
        }

        String description = itemDTO.getDescription();
        if (description != null) {
            item.setDescription(description);
        }

        Boolean available = itemDTO.getAvailable();
        if (available != null) {
            item.setAvailable(available);
        }

        return Util.toItemDTO(repository.save(item), getComments(itemId), getNextAndLastUserBookings(itemId));
    }

    @Override
    public ItemDTO addNewItem(ItemDTO itemDTO, Long ownerId) {
        if (ownerId == null || itemDTO == null) {
            throw new BadRequestException("Invalid parameters: ownerId, or itemDTO is null");
        }

        Long itemId = itemDTO.getId();

        if (itemId != null) {
            throw new BadRequestException("Cannot add item with itemId");
        }

        Util.validateItemDTO(itemDTO);

        if (!userService.isUserExists(ownerId)) {
            throw new ObjectNotFoundException("User", ownerId);
        }

        return Util.toItemDTO(repository.save(Util.getItemFromItemDTO(itemDTO, ownerId)), new ArrayList<>());
    }

    @Transactional
    @Override
    public ItemDTO getItemById(Long itemId, Long userId) {
        try {
            Item item = getItem(itemId);
            if (Objects.equals(item.getOwnerId(), userId)) {
                return Util.toItemDTO(item, getComments(itemId), getNextAndLastUserBookings(itemId));
            } else {
                return Util.toItemDTO(item, getComments(itemId));
            }
        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException("Item", itemId);
        }
    }

    @Override
    public List<ItemDTO> getItemsBySearchQuery(String searchQuery, Long userId) {
        if (searchQuery.isBlank()) {
            return List.of();
        }

        return getItemDTOFromItemList(repository.
                findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(searchQuery,
                        searchQuery, true), userId);
    }

    private Item getItem(Long itemId) {
        if (itemId == null) {
            throw new BadRequestException("Invalid parameter: itemId is null");
        }

        return repository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item", itemId));
    }

    private Map.Entry<UserBooking, UserBooking> getNextAndLastUserBookings(Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("Item", itemId);
        }

        return new AbstractMap.SimpleEntry<>(bookingService.getLastBooking(itemId),
                bookingService.getNextBooking(itemId));
    }

    private List<ItemDTO> getItemDTOFromItemList(Iterable<Item> items, Long userId) {
        return StreamSupport.stream(items.spliterator(), false)
                .map(x -> toItemDTOWithBookings(x, userId)).collect(Collectors.toList());
    }

    private ItemDTO toItemDTOWithBookings(Item item, Long ownerId) {
        if (Objects.equals(ownerId, item.getOwnerId())) {
            return Util.toItemDTO(item, getComments(item.getItemId()), getNextAndLastUserBookings(item.getItemId()));
        } else {
            return Util.toItemDTO(item, getComments(item.getItemId()));
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

        if (!userService.isUserExists(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        User user = Util.getUserFromUserDTO(userId, userService.getUser(userId));
        Item item = this.getItem(itemId);

        return this.commentRepository.save(Util.getCommentFromCommentDTO(commentDTO, user, item)).toCommentDTO();
    }

    private List<Comment> getComments(Long itemId) {
        if (itemId == null) {
            throw new BadRequestException("Invalid parameter: itemId is null");
        }
        return this.commentRepository.findAllByItem_ItemId(itemId);
    }
}
