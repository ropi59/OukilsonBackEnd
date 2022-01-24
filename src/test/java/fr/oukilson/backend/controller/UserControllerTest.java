package fr.oukilson.backend.controller;

import com.google.gson.Gson;
import fr.oukilson.backend.dto.user.UserCreationDTO;
import fr.oukilson.backend.dto.user.UserDTO;
import fr.oukilson.backend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.LinkedList;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService service;
    private final String route = "/users";

    // Method findByNickname

    /**
     * Test findByNickname when nickname is present in DB
     */
    @DisplayName("Test findByNickname : nickname found")
    @Test
    public void testFindByNicknameFound() throws Exception {
        String nickname = "Tutululu";
        UserDTO dto = new UserDTO(nickname, new LinkedList<>());
        Mockito.when(this.service.findUserByNickname(nickname)).thenReturn(dto);
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+nickname))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Gson gson = new Gson();
        UserDTO resultDTO = gson.fromJson(
                result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                UserDTO.class);
        Assertions.assertNotNull(resultDTO);
        Assertions.assertEquals(dto, resultDTO);
    }

    /**
     * Test findByNickname when nickname is not present in DB
     */
    @DisplayName("Test findByNickname : nickname not found")
    @Test
    public void testFindByNicknameNotFound() throws Exception {
        String nickname = "Tutululu";
        Mockito.when(this.service.findUserByNickname(nickname)).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+nickname))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test findByNickname when nickname is not valid
     */
    @DisplayName("Test findByNickname : invalid nickname")
    @Test
    public void testFindByNicknameInvalid() throws Exception {
        String nickname = "rené";
        Mockito.when(this.service.findUserByNickname(nickname)).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+nickname))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test findByNickname when nickname is null
     */
    @DisplayName("Test findByNickname : null nickname")
    @Test
    public void testFindByNicknameNull() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test findByNickname when there is a SQL problem
     */
    @DisplayName("Test findByNickname : SQL problem")
    @Test
    public void testFindByNicknameSQLProblem() throws Exception {
        String nickname = "Tutululu";
        Mockito.when(this.service.findUserByNickname(nickname)).thenThrow(RuntimeException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+nickname))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
  
    // Method createUser

    /**
     * Test createUser with a null body
     */
    @DisplayName("Test createUser : null body")
    @Test
    public void testCreateUserNullBody() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(route))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test createUser with a null password
     */
    @DisplayName("Test createUser : null password")
    @Test
    public void testCreateUserNullPassword() throws Exception {
        UserCreationDTO body = new UserCreationDTO("Toupie", null, "hibiscus@george.fr");
        Gson gson = new Gson();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test createUser with a null nickname
     */
    @DisplayName("Test createUser : null nickname")
    @Test
    public void testCreateUserNullNickname() throws Exception {
        UserCreationDTO body = new UserCreationDTO(null, "sdfghjklmmdj", "hibiscus@george.fr");
        Gson gson = new Gson();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test createUser with a null email
     */
    @DisplayName("Test createUser : null email")
    @Test
    public void testCreateUserNullEmail() throws Exception {
        UserCreationDTO body = new UserCreationDTO("Toupie", "sdfghjklmmdj", null);
        Gson gson = new Gson();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test createUser when the user creation on the service failed
     */
    @DisplayName("Test createUser : user creation failed")
    @Test
    public void testCreateUserUserCreationFailed() throws Exception {
        UserCreationDTO body = new UserCreationDTO("Toupie", "sdfghjklmmdj", "hibiscus@george.fr");
        Mockito.when(this.service.createUser(body)).thenReturn(null);
        Gson gson = new Gson();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test createUser when the user creation on the service is successful
     */
    @DisplayName("Test createUser : user creation successful")
    @Test
    public void testCreateUserUserCreationSuccess() throws Exception {
        UserCreationDTO body = new UserCreationDTO("Toupie", "sdfghjklmmdj", "hibiscus@george.fr");
        UserDTO userDTO = new UserDTO("Toupie", new LinkedList<>());
        Mockito.when(this.service.createUser(body)).thenReturn(userDTO);
        Gson gson = new Gson();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        UserDTO resultDTO = gson.fromJson(
                result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                UserDTO.class);
        Assertions.assertNotNull(resultDTO);
        Assertions.assertEquals(userDTO, resultDTO);
    }

    /**
     * Test createUser when the user creation on the service throw an exception
     */
    @DisplayName("Test createUser : user creation throws an exception")
    @Test
    public void testCreateUserUserCreationThrowException() throws Exception {
        UserCreationDTO body = new UserCreationDTO("Toupie", "sdfghjklmmdj", "hibiscus@george.fr");
        Mockito.when(this.service.createUser(body)).thenThrow(NullPointerException.class);
        Gson gson = new Gson();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(route)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(gson.toJson(body)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

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