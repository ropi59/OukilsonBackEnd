package fr.oukilson.backend.controller;

import fr.oukilson.backend.service.UserService;
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

    /**
     * Test addUserToFriendList when the path variables id1 is null
     */
    @DisplayName("Test addUserToFriendList : null path variable id1")
    @Test
    public void testAddUserToFriendListNullId1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/add/"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test addUserToFriendList when the path variables id2 is null
     */
    @DisplayName("Test addUserToFriendList : null path variable id2")
    @Test
    public void testAddUserToFriendListNullId2() throws Exception {
        String id1 = "Truc";
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/add/"+id1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test addUserToFriendList when the method addUserToFriendList from the service returns false
     */
    @DisplayName("Test addUserToFriendList : failed to add")
    @Test
    public void testAddUserToFriendListServiceReturnsFalse() throws Exception {
        String id1 = "Elvis";
        String id2 = "Presley";
        Mockito.when(this.service.addUserToFriendList(id1, id2)).thenReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/add/"+id1+"/"+id2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("false"));
    }

    /**
     * Test addUserToFriendList when the method addUserToFriendList from the service returns true
     */
    @DisplayName("Test addUserToFriendList : add successfully")
    @Test
    public void testAddUserToFriendListServiceReturnsTrue() throws Exception {
        String id1 = "Elvis";
        String id2 = "Presley";
        Mockito.when(this.service.addUserToFriendList(id1, id2)).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/add/"+id1+"/"+id2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }

    // Method removeUserFromFriendList

    /**
     * Test removeUserFromFriendList when the path variables id1 is null
     */
    @DisplayName("Test removeUserFromFriendList : null path variable id1")
    @Test
    public void testRemoveUserFromFriendListNullId1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/remove/"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test removeUserFromFriendList when the path variables id2 is null
     */
    @DisplayName("Test removeUserFromFriendList : null path variable id2")
    @Test
    public void testRemoveUserFromFriendListNullId2() throws Exception {
        String id1 = "Bidulle";
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/remove/"+id1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test removeUserFromFriendList when the method removeUserFromFriendList from the service returns false
     */
    @DisplayName("Test removeUserFromFriendList : failed to remove")
    @Test
    public void testRemoveUserFromFriendListServiceReturnsFalse() throws Exception {
        String id1 = "Bidulle";
        String id2 = "Machin";
        Mockito.when(this.service.removeUserFromFriendList(id1, id2)).thenReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/remove/"+id1+"/"+id2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("false"));
    }

    /**
     * Test removeUserFromFriendList when the method removeUserFromFriendList from the service returns true
     */
    @DisplayName("Test removeUserFromFriendList : remove successfully")
    @Test
    public void testRemoveUserFromFriendListServiceReturnsTrue() throws Exception {
        String id1 = "Bidulle";
        String id2 = "Machin";
        Mockito.when(this.service.removeUserFromFriendList(id1, id2)).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(route+"/remove/"+id1+"/"+id2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }

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
