package fr.oukilson.backend.dto.event;

import fr.oukilson.backend.dto.game.GameUuidDTO;
import fr.oukilson.backend.dto.location.EventUpdateLocationDTO;
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
public class EventUpdateDTOTest {

    /**
     * Create a valid EventUpdateDTO with all attributes are initialized correctly
     * @return EventUpdateDTO
     */
    private EventUpdateDTO createValidEventUpdateDTO() {
        EventUpdateDTO event = new EventUpdateDTO();
        event.setUuid("8083229a-44a4-4179-a4da-06db3022be7f");
        event.setTitle("Title");
        event.setDescription("Description");
        event.setMinPlayer(3);
        LocalDateTime now = LocalDateTime.now();
        event.setLimitDate(now.plusDays(1));
        event.setStartingDate(now.plusDays(2));
        event.setEndingDate(now.plusDays(3));
        event.setMaxPlayer(event.getMinPlayer()+1);
        event.setGame(new GameUuidDTO("Oblivion"));
        event.setLocation(new EventUpdateLocationDTO("Paris", "75000", "3 rue Michel Montaigne"));
        return event;
    }

    /**
     * Test when every attribute is set up correctly
     */
    @DisplayName("Test IsValid : when everything is ok")
    @Test
    public void testIsValid() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        Assertions.assertTrue(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when minimal player count is less than 2
     */
    @DisplayName("Test isValid : minimal player count less than 2")
    @Test
    public void testIsValidWhenMinPlayerLessThan2() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setMinPlayer(1);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when minimal player count equals 2
     */
    @DisplayName("Test isValid : minimal player count equals 2")
    @Test
    public void testIsValidWhenMinPlayerEquals2() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setMinPlayer(2);
        Assertions.assertTrue(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when minimal player count equals maximal player count
     */
    @DisplayName("Test isValid : minimal player count equals maximal player count")
    @Test
    public void testIsValidWhenMinPlayerEqualsMaxPlayer() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setMinPlayer(event.getMaxPlayer());
        Assertions.assertTrue(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when minimal player count is higher than maximal player count
     */
    @DisplayName("Test isValid : minimal player count higher than maximal player count")
    @Test
    public void testIsValidWhenMinPlayerHigherThanMaxPlayer() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setMinPlayer(1+event.getMaxPlayer());
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event's title is null
     */
    @DisplayName("Test isValid : title is null")
    @Test
    public void testIsValidWhenTitleIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setTitle(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event's description is null
     */
    @DisplayName("Test isValid : description is null")
    @Test
    public void testIsValidWhenDescriptionIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setDescription(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event's uuid is null
     */
    @DisplayName("Test isValid : uuid is null")
    @Test
    public void testIsValidWhenUuidIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setUuid(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the GameUuidDTO attribute is null
     */
    @DisplayName("Test isValid : game is null")
    @Test
    public void testIsValidWhenGameIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setGame(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the EventCreateLocationDTO attribute is null
     */
    @DisplayName("Test isValid : location is null")
    @Test
    public void testIsValidWhenLocationIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setLocation(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the attribute town of EventCreateLocationDTO is null
     */
    @DisplayName("Test isValid : location.town is null")
    @Test
    public void testIsValidWhenLocationTownIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.getLocation().setTown(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the attribute uuid of GameUuidDTO is null
     */
    @DisplayName("Test isValid : game.uuid is null")
    @Test
    public void testIsValidWhenGameUuidIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.getGame().setUuid(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event limit inscription date is null
     */
    @DisplayName("Test isValid : limit inscription date is null")
    @Test
    public void testIsValidWhenLimitDateIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setLimitDate(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event starting date is null
     */
    @DisplayName("Test isValid : starting date is null")
    @Test
    public void testIsValidWhenStartingDateIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setStartingDate(null);
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the event ending date is null
     */
    @DisplayName("Test isValid : ending date is null")
    @Test
    public void testIsValidWhenEndingDateIsNull() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setEndingDate(null);
        Assertions.assertTrue(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the limit inscription date is before the creation date
     */
    @DisplayName("Test isValid : limit inscription date is before creation date")
    @Test
    public void testIsValidWhenLimitDateIsBeforeCreationDate() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setLimitDate(LocalDateTime.now().minusYears(10));
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the starting date is before the limit inscription date
     */
    @DisplayName("Test isValid : starting date is before limit inscription date")
    @Test
    public void testIsValidWhenStartingDateIsBeforeLimitDate() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setStartingDate(event.getLimitDate().minusYears(1));
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }

    /**
     * Test when the ending date is before the starting date
     */
    @DisplayName("Test isValid : ending date is before starting date")
    @Test
    public void testIsValidWhenEndingDateIsBeforeStartingDate() {
        EventUpdateDTO event = createValidEventUpdateDTO();
        event.setEndingDate(event.getStartingDate().minusYears(1));
        Assertions.assertFalse(event.isValid(LocalDateTime.now()));
    }
}
