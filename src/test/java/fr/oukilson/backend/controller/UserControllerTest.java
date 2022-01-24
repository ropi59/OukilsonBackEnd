package fr.oukilson.backend.controller;

import com.google.gson.Gson;
import fr.oukilson.backend.dto.user.UserDTO;
import fr.oukilson.backend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
}
