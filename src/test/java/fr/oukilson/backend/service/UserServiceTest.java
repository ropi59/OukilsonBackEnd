package fr.oukilson.backend.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserServiceTest {

    @Test
    @DisplayName("testing email checking method")
    public void emailIsValidAssertTrue(){
        String[] emails = {"jeanpierre@email.com", ".j@e.c", "jeanpierre@email.com.", "jeanpierre@yahoo.com"};
        Dotenv dotenv = Dotenv.load();
        Pattern pattern = Pattern.compile("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        String string = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        System.out.println(string.length());
        System.out.println(Pattern.compile(dotenv.get("EMAIL_REGEX")));
//        Assertions.assertTrue(Pattern.compile(dotenv.get("EMAIL_REGEX")).matcher(emails[0]).find());
//        Assertions.assertFalse(Pattern.compile(dotenv.get("EMAIL_REGEX")).matcher(emails[1]).find());
//        Assertions.assertFalse(Pattern.compile(dotenv.get("EMAIL_REGEX")).matcher(emails[2]).find());
//        Assertions.assertTrue(pattern.matcher("hello@example.com").find());
    }

    @Test
    @DisplayName("testing username validation method")
    public void nicknameIsValid(){
        String[] nicknames = {"titi", "t", "titi2", "..azd"};
        Dotenv dotenv = Dotenv.load();
//        Assertions.assertTrue(Pattern.compile(dotenv.get("NICKNAME_REGEX")).matcher(nicknames[0]).find());
//        Assertions.assertFalse(Pattern.compile(dotenv.get("NICKNAME_REGEX")).matcher(nicknames[1]).find());
//        Assertions.assertTrue(Pattern.compile(dotenv.get("NICKNAME_REGEX")).matcher(nicknames[2]).find());
        Assertions.assertFalse(Pattern.compile(dotenv.get("NICKNAME_REGEX")).matcher(nicknames[3]).find());
    }
}
