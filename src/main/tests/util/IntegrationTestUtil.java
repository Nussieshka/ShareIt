package util;

import com.nussia.booking.BookingService;
import com.nussia.booking.dto.BookingDTO;
import com.nussia.booking.dto.BookingShort;
import com.nussia.booking.model.Booking;
import com.nussia.item.Item;
import com.nussia.item.ItemService;
import com.nussia.item.dto.ItemDTO;
import com.nussia.item.dto.ItemMapper;
import com.nussia.item.dto.SimpleItemDTO;
import com.nussia.request.Request;
import com.nussia.request.RequestService;
import com.nussia.request.dto.RequestDTO;
import com.nussia.user.User;
import com.nussia.user.UserService;
import com.nussia.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.concurrent.atomic.AtomicLong;

@Transactional
@TestPropertySource(properties = { "db.name = test_share_it" })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationTestUtil {

    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final RequestService requestService;
    private final static AtomicLong counter = new AtomicLong();

    public User createUser(UserDTO userDTO) {
        UserDTO repositoryUserDTO = userService.createUser(userDTO);
        return selectUserById(repositoryUserDTO.getId());
    }

    public User createUser() {
        return createUser(TestUtil.getTestUserDTO(counter.getAndIncrement() + "@email.com"));
    }

    public User selectUserById(Long id) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT user FROM User user WHERE user.id = :id", User.class);
        return query.setParameter("id", id).getSingleResult();
    }

    public Item selectItemById(Long id) {
        TypedQuery<Item> query = entityManager.createQuery(
                "SELECT item FROM Item item WHERE item.itemId = :itemId", Item.class);
        return query.setParameter("itemId", id).getSingleResult();
    }

    public Item createItem(SimpleItemDTO itemDTO, Long ownerId) {
        ItemDTO repostitoryItemDTO = itemService.addNewItem(ItemMapper.INSTANCE.toItemDTO(itemDTO), ownerId);
        return selectItemById(repostitoryItemDTO.getId());
    }

    public Item createItem(Long ownerId) {
        return createItem(TestUtil.getTestItemDTO(), ownerId);
    }

    public Request selectRequestById(Long id) {
        TypedQuery<Request> query = entityManager.createQuery(
                "SELECT request FROM Request request WHERE request.id = :requestId", Request.class);
        return query.setParameter("requestId", id).getSingleResult();
    }

    public Request createRequest(RequestDTO requestDTO, Long userId) {
        RequestDTO repostitoryRequestDTO = requestService.addRequest(requestDTO, userId);
        return selectRequestById(repostitoryRequestDTO.getId());
    }

    public Request createRequest(Long userId) {
        return createRequest(TestUtil.getTestRequestDTO(), userId);
    }

    public Booking selectBookingById(Long id) {
        TypedQuery<Booking> query = entityManager.createQuery(
                "SELECT booking FROM Booking booking WHERE booking.bookingId = :bookingId", Booking.class);
        return query.setParameter("bookingId", id).getSingleResult();
    }

    public void insertBooking(Booking booking) {
        entityManager.persist(booking);
    }

    public Booking createBooking(BookingShort shortBooking, Long userId) {
        BookingDTO repostitoryBookingDTO = bookingService.addBooking(shortBooking, userId);
        return selectBookingById(repostitoryBookingDTO.getId());
    }

    public Booking createBooking(Long itemOwnerId, Long bookerId) {
        return createBooking(TestUtil.getTestBookingShort(createItem(itemOwnerId).getItemId()), bookerId);
    }

    public Booking createBooking(Long bookerId) {
        return createBooking(createUser().getId(), bookerId);
    }

    public Booking createBooking() {
        return createBooking(createUser().getId());
    }
}
