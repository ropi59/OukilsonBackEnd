package fr.oukilson.backend.dto;

import fr.oukilson.backend.dto.user.UserCreationDTO;
import fr.oukilson.backend.model.RegexCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserCreationDTOTest {
    @Autowired
    private RegexCollection regexCollection;

    /**
     * Test isValid when nicknamePattern is null
     */
    @DisplayName("Test isValid : ")
    @Test
    public void testIsValidNicknamePatternNull() {
        UserCreationDTO dto = new UserCreationDTO("Regis", "dfghjklhjjh", "regis@thales.fr");
        Assertions.assertThrows(NullPointerException.class,
                () -> dto.isValid(null, regexCollection.getEmailPattern()));
    }

    /**
     * Test isValid when emailPattern is null
     */
    @DisplayName("Test isValid : ")
    @Test
    public void testIsValidEmailPatternNull() {
        UserCreationDTO dto = new UserCreationDTO("Regis", "dfghjklhjjh", "regis@thales.fr");
        Assertions.assertThrows(NullPointerException.class,
                () -> dto.isValid(regexCollection.getNicknamePattern(), null));
    }

    /**
     * Test isValid when nickname is null
     */
    @DisplayName("Test isValid : ")
    @Test
    public void testIsValidNicknameNull() {
        UserCreationDTO dto = new UserCreationDTO(null, "hsdfsqdmlhvjdfvbhld", "unemail@unserveur.fr");
        Assertions.assertFalse(dto.isValid(regexCollection.getNicknamePattern(), regexCollection.getEmailPattern()));
    }

    /**
     * Test isValid when password is null
     */
    @DisplayName("Test isValid : password is null")
    @Test
    public void testIsValidPasswordNull() {
        UserCreationDTO dto = new UserCreationDTO("Jean", null, "unemail@unserveur.fr");
        Assertions.assertFalse(dto.isValid(regexCollection.getNicknamePattern(), regexCollection.getEmailPattern()));
    }

    /**
     * Test isValid when email is null
     */
    @DisplayName("Test isValid : ")
    @Test
    public void testIsValidEmailNull() {
        UserCreationDTO dto = new UserCreationDTO("Jean", "hlsdqghosufr", null);
        Assertions.assertFalse(dto.isValid(regexCollection.getNicknamePattern(), regexCollection.getEmailPattern()));
    }

    /**
     * Test isValid when password is blank
     */
    @DisplayName("Test isValid : password is blank")
    @Test
    public void testIsValidPasswordBlank() {
        UserCreationDTO dto = new UserCreationDTO("Jean", "    ", "regis@thales.fr");
        Assertions.assertFalse(dto.isValid(regexCollection.getNicknamePattern(), regexCollection.getEmailPattern()));
        dto = new UserCreationDTO("Jean", "", "regis@thales.fr");
        Assertions.assertFalse(dto.isValid(regexCollection.getNicknamePattern(), regexCollection.getEmailPattern()));
    }

    /**
     * Test isValid when email doesn't match the pattern
     */
    @DisplayName("Test isValid : email doesn't match the pattern")
    @Test
    public void testIsValidEmailInvalid() {
        UserCreationDTO dto = new UserCreationDTO("Jean", "dfghjklhjjh", "regis@.fr");
        Assertions.assertFalse(dto.isValid(regexCollection.getNicknamePattern(), regexCollection.getEmailPattern()));
    }

    /**
     * Test isValid when nickname doesn't match the pattern
     */
    @DisplayName("Test isValid : nickname doesn't match the pattern")
    @Test
    public void testIsValidNicknameInvalid() {
        UserCreationDTO dto = new UserCreationDTO("Rébecca", "dfghjklhjjh", "regis@thales.fr");
        Assertions.assertFalse(dto.isValid(regexCollection.getNicknamePattern(), regexCollection.getEmailPattern()));
    }

    /**
     * Test isValid when everything is valid
     */
    @DisplayName("Test isValid : everything is valid")
    @Test
    public void testIsValid() {
        UserCreationDTO dto = new UserCreationDTO("Regis", "dfghjklhjjh", "regis@thales.fr");
        Assertions.assertTrue(dto.isValid(regexCollection.getNicknamePattern(), regexCollection.getEmailPattern()));
    }
}
