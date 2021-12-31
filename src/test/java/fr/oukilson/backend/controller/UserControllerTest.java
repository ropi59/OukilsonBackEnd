package fr.oukilson.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.oukilson.backend.dto.ResponseDTO;
import fr.oukilson.backend.dto.UserCreationDTO;
import com.google.gson.Gson;
import fr.oukilson.backend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    public UserCreationDTO userCreationDTO(){
        return new UserCreationDTO("password", "email@email.com", "nickname");
    }

    /**
     * tests that the function creates a user and returns a 201 http code
     * @throws Exception an Exception
     */
    @Test
    public void testCreateUser() throws Exception{
        // creates a user to add
        UserCreationDTO userCreationDTO = userCreationDTO();
        this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationDTO)))
                .andExpect(status().isCreated());
    }

    /**
     * tests that the function returns a 400 code if one of the necessary inputs is null.
     * @throws Exception An exception
     */
    @Test
    public void testCreateUser_badEmail_thenReturns400() throws Exception{
        UserCreationDTO userCreationDTO = new UserCreationDTO("password", null, "jean");
        this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * tests that the function returns a 400 code if one of the necessary inputs is null.
     * @throws Exception An exception
     */
    @Test
    public void testCreateUser_badPassword_thenReturns400() throws Exception{
        UserCreationDTO userCreationDTO = new UserCreationDTO(null, "email@email.com", "jean");
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * tests that the function returns a 400 code if one of the necessary inputs is null.
     * @throws Exception An exception
     */
    @Test
    public void testCreateUser_badNickname_thenReturns400() throws Exception{
        UserCreationDTO userCreationDTO = new UserCreationDTO("password", "email@email.com", null);
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * tests that the function calls the service once and tests the return against the input
     * @throws Exception An exception
     */
    @Test
    public void testCreateUser_thenMapsToService() throws Exception{
        // create a user to save & to test against later
        UserCreationDTO userCreationDTO = userCreationDTO();
        this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationDTO)))
                .andExpect(status().isCreated());
        // create a captor object to store the DTO we send to the service
        ArgumentCaptor<UserCreationDTO> userCreationDTOArgumentCaptor = ArgumentCaptor.forClass(UserCreationDTO.class);
        // checks that the service is called once, then calls it directly using the captor as argument
        verify(userService, times(1)).createUser(userCreationDTOArgumentCaptor.capture());
        // checks if the captor equals the original DTO
        Assertions.assertEquals(userCreationDTOArgumentCaptor.getValue(), userCreationDTO);
    }

    /**
     * test to check the returned value of the function
     * @throws Exception an exception
     */
    @Test
    public void testCreateUser_ThenReturnsResponseDTO() throws Exception{
        // creates DTOs to save &/or to test against later
        UserCreationDTO userCreationDTO = userCreationDTO();
        ResponseDTO responseDTO = new ResponseDTO(true, "success");
        // tells the test that when the service is called it should return the responseDTO object
        when(userService.createUser(any(UserCreationDTO.class))).thenReturn(responseDTO);
        MvcResult mvcResult = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        // Transforms the result of the request back into a responseDTO object & check for success
        ResponseDTO actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseDTO.class);
        Assertions.assertTrue(actualResponse.isSuccess());
    }
}
