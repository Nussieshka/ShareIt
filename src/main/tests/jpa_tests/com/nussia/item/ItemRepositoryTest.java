package jpa_tests.com.nussia.item;

import com.nussia.item.Item;
import com.nussia.item.ItemRepository;
import com.nussia.item.dto.ItemMapper;
import com.nussia.item.dto.SimpleItemDTO;
import com.nussia.user.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import util.TestPersistenceConfig;
import util.TestUtil;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@Transactional
@TestPropertySource(properties = { "db.name = test_share_it" })
@SpringJUnitConfig( { TestPersistenceConfig.class })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {

    private final ItemRepository repository;
    private final TestEntityManager entityManager;

    @Test
    void shouldFindAllByName() {
        User user = TestUtil.getTestUser(null);
        user.setId(entityManager.persistAndGetId(user, Long.class));
        SimpleItemDTO itemDTO1 = TestUtil.getTestItemDTO();
        SimpleItemDTO itemDTO2 = TestUtil.getTestItemDTO();
        itemDTO2.setName("Wireless Headphones");
        SimpleItemDTO itemDTO3 = TestUtil.getTestItemDTO();
        itemDTO3.setName("Old Radio");
        itemDTO3.setDescription("This radio doesn't support wireless headphones");

        entityManager.persist(ItemMapper.INSTANCE.toItemEntity(itemDTO1, user));
        entityManager.persist(ItemMapper.INSTANCE.toItemEntity(itemDTO2, user));
        entityManager.persist(ItemMapper.INSTANCE.toItemEntity(itemDTO3, user));

        List<Item> firstQuery = repository.
                findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                        "wireless", "wireless", true);

        List<Item> secondQuery = repository.
                findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                        "phone", "phone", true);

        assertThat(firstQuery.size(), equalTo(2));
        assertThat(secondQuery.size(), equalTo(3));
    }
}
