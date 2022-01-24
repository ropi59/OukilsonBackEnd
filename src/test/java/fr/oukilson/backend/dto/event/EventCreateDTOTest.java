package fr.oukilson.backend.dto.event;

import fr.oukilson.backend.dto.game.GameUuidDTO;
import fr.oukilson.backend.dto.location.EventCreateLocationDTO;
import fr.oukilson.backend.dto.user.UserNameDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventCreateDTOTest {

    /**
     * Create a valid EventCreateDTO with all attributes are initialized correctly
     * @return EventCreateDTO
     */
    private EventCreateDTO createValidEventCreateDTO() {
        EventCreateDTO event = new EventCreateDTO();
        event.setTitle("Title");
        event.setDescription("Description");
        event.setMinPlayer(3);
        LocalDateTime now = LocalDateTime.now();
        event.setLimitDate(now.plusDays(1));
        event.setStartingDate(now.plusDays(2));
        event.setEndingDate(now.plusDays(3));
        event.setMaxPlayer(event.getMinPlayer()+1);
        event.setCreator(new UserNameDTO("Toto"));
        event.setGame(new GameUuidDTO("Oblivion"));
        event.setLocation(new EventCreateLocationDTO("Paris", "75000", "3 rue Michel Montaigne"));
        return event;
    }

    /**
     * Test when every attribute is set up correctly
     */
    @DisplayName("Test IsValid : when everything is ok")
    @Test
    public void testIsValid() {
        EventCreateDTO event = createValidEventCreateDTO();
        Assertions.assertTrue(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when minimal player count is less than 2
     */
    @DisplayName("Test isValid : minimal player count less than 2")
    @Test
    public void testIsValidWhenMinPlayerLessThan2() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setMinPlayer(1);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when minimal player count equals 2
     */
    @DisplayName("Test isValid : minimal player count equals 2")
    @Test
    public void testIsValidWhenMinPlayerEquals2() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setMinPlayer(2);
        Assertions.assertTrue(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when minimal player count equals maximal player count
     */
    @DisplayName("Test isValid : minimal player count equals maximal player count")
    @Test
    public void testIsValidWhenMinPlayerEqualsMaxPlayer() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setMinPlayer(event.getMaxPlayer());
        Assertions.assertTrue(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when minimal player count is higher than maximal player count
     */
    @DisplayName("Test isValid : minimal player count higher than maximal player count")
    @Test
    public void testIsValidWhenMinPlayerHigherThanMaxPlayer() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setMinPlayer(1+event.getMaxPlayer());
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event's title is null
     */
    @DisplayName("Test isValid : title is null")
    @Test
    public void testIsValidWhenTitleIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setTitle(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event's description is null
     */
    @DisplayName("Test isValid : description is null")
    @Test
    public void testIsValidWhenDescriptionIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setDescription(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the UserNameDTO attribute is null
     */
    @DisplayName("Test isValid : creator is null")
    @Test
    public void testIsValidWhenCreatorIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setCreator(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the GameUuidDTO attribute is null
     */
    @DisplayName("Test isValid : game is null")
    @Test
    public void testIsValidWhenGameIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setGame(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the EventCreateLocationDTO attribute is null
     */
    @DisplayName("Test isValid : location is null")
    @Test
    public void testIsValidWhenLocationIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setLocation(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the attribute town of EventCreateLocationDTO is null
     */
    @DisplayName("Test isValid : location.town is null")
    @Test
    public void testIsValidWhenLocationTownIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.getLocation().setTown(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the attribute nickname of UserNameDTO is null
     */
    @DisplayName("Test isValid : creator.nickname is null")
    @Test
    public void testIsValidWhenCreatorNicknameIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.getCreator().setNickname(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the attribute uuid of GameUuidDTO is null
     */
    @DisplayName("Test isValid : game.uuid is null")
    @Test
    public void testIsValidWhenGameUuidIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.getGame().setUuid(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event limit inscription date is null
     */
    @DisplayName("Test isValid : limit inscription date is null")
    @Test
    public void testIsValidWhenLimitDateIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setLimitDate(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event starting date is null
     */
    @DisplayName("Test isValid : starting date is null")
    @Test
    public void testIsValidWhenStartingDateIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setStartingDate(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event ending date is null
     */
    @DisplayName("Test isValid : ending date is null")
    @Test
    public void testIsValidWhenEndingDateIsNull() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setEndingDate(null);
        Assertions.assertTrue(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the limit inscription date is before the creation date
     */
    @DisplayName("Test isValid : limit inscription date is before creation date")
    @Test
    public void testIsValidWhenLimitDateIsBeforeCreationDate() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setLimitDate(LocalDateTime.now().minusYears(10));
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the starting date is before the limit inscription date
     */
    @DisplayName("Test isValid : starting date is before limit inscription date")
    @Test
    public void testIsValidWhenStartingDateIsBeforeLimitDate() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setStartingDate(event.getLimitDate().minusYears(1));
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the ending date is before the starting date
     */
    @DisplayName("Test isValid : ending date is before starting date")
    @Test
    public void testIsValidWhenEndingDateIsBeforeStartingDate() {
        EventCreateDTO event = createValidEventCreateDTO();
        event.setEndingDate(event.getStartingDate().minusYears(1));
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }
}
