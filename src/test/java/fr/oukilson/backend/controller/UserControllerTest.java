package fr.oukilson.backend.controller;

import fr.oukilson.backend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService service;
    private final String route = "/users";

    // Method createUser

    // Method addUserToFriendList


    // Method removeUserFromFriendList


    // Method emptyFriendList

    /**
     * Test emptyFriendList when called with a null string
     */
    @DisplayName("Test emptyFriendList : null nickname")
    @Test
    public void testEmptyFriendListNullNickname() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/empty/"))
                        .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test emptyFriendList when emptyFriendList from the service return false
     */
    @DisplayName("Test emptyFriendList : failed to empty friend list")
    @Test
    public void testEmptyFriendListServiceReturnFalse() throws Exception {
        String nickname = "toto";
        Mockito.when(this.service.emptyFriendList(nickname)).thenReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/empty/"+nickname))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("false"));
    }

    /**
     * Test emptyFriendList when emptyFriendList from the service return true
     */
    @DisplayName("Test emptyFriendList : empty friend list successfully")
    @Test
    public void testEmptyFriendListServiceReturnTrue() throws Exception {
        String nickname = "Gandalf";
        Mockito.when(this.service.emptyFriendList(nickname)).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/empty/"+nickname))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }
}
