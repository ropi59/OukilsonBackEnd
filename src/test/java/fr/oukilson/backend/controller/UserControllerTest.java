package fr.oukilson.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.oukilson.backend.dto.ResponseDTO;
import fr.oukilson.backend.dto.UserCreationDTO;
import fr.oukilson.backend.dto.UserDTO;
import fr.oukilson.backend.dto.UserOnListDTO;
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


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    public UserDTO userDTO(){
        return new UserDTO(1L, "email@email.com", "nickname");
    }

    public UserOnListDTO userOnListDTO(){
        return new UserOnListDTO("email2@email.com", "nickname2");
    }

    ////////// TESTS OF CREATE USER FUNCTION ////////////
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

    ////////// TESTING FUNCTION THAT ADDS USERS TO A FRIEND LIST //////////

    /**
     * tests for valid use of addusertofriendlist method
     * @throws Exception
     */
    @Test
    public void testAddUserToFriendList() throws Exception{
        // create users to add to the db & a response to return later
        UserDTO userDTO = userDTO();
        UserDTO userToAddDTO = new UserDTO(2L, "email2@email.com", "nickname2");
        ResponseDTO responseDTO = new ResponseDTO(true, "success");
        // tell the test to return a given object when calling upon the service & stores them
        when(userService.findById(1L)).thenReturn(userDTO);
        when(userService.findById(2L)).thenReturn(userToAddDTO);
        MvcResult mainUserResult = this.mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult userToAddResult = this.mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andReturn();
        // map the returns into DTOs so that we can add one to the friend list of the other
        UserDTO mainUser = objectMapper.readValue(mainUserResult.getResponse().getContentAsString(), UserDTO.class);
        UserOnListDTO userToAdd = objectMapper.readValue(userToAddResult.getResponse().getContentAsString(), UserOnListDTO.class);
        mainUser.getFriendList().add(userToAdd);
        // tell the test to return a given response when calling upon a given service
        when(userService.addUserToFriendList(anyLong(), anyLong())).thenReturn(responseDTO);
        MvcResult finalResult = this.mockMvc.perform(put("/users/add/1/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mainUser)))
                .andExpect(status().isOk())
                .andReturn();
        // maps the returned response into a ResponseDTO object & tests it for validity
        ResponseDTO finalResponse = objectMapper.readValue(finalResult.getResponse().getContentAsString(), ResponseDTO.class);
        Assertions.assertTrue(finalResponse.isSuccess());
    }

    // tests concerning finding the correct users should be done on the get route, not here? //

    @Test
    public void testAddUserToFriendList_thenMapsToService() throws Exception {
        UserDTO userDTO = userDTO();
        UserDTO userToAddDTO = new UserDTO(2L, "email2@email.com", "nickname2");
        ResponseDTO responseDTO = new ResponseDTO(true, "success");
        when(userService.findById(1L)).thenReturn(userDTO);
        when(userService.findById(2L)).thenReturn(userToAddDTO);
        MvcResult mainUserResult = this.mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult userToAddResult = this.mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andReturn();
        UserDTO mainUser = objectMapper.readValue(mainUserResult.getResponse().getContentAsString(), UserDTO.class);
        UserOnListDTO userToAdd = objectMapper.readValue(userToAddResult.getResponse().getContentAsString(), UserOnListDTO.class);
        mainUser.getFriendList().add(userToAdd);
        when(userService.addUserToFriendList(anyLong(), anyLong())).thenReturn(responseDTO);
        this.mockMvc.perform(put("/users/add/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mainUser)))
                .andExpect(status().isOk());

        ArgumentCaptor<UserDTO> userDTOArgumentCaptor = ArgumentCaptor.forClass(UserDTO.class);
        ArgumentCaptor<UserDTO> userDTOArgumentCaptor2 = ArgumentCaptor.forClass(UserDTO.class);
        verify(userService, times(1)).addUserToFriendList(userDTOArgumentCaptor.capture().getId(), userDTOArgumentCaptor2.capture().getId());
        Assertions.assertTrue(userDTOArgumentCaptor.getValue().getFriendList().contains(userDTOArgumentCaptor2.getValue()));
    }
}
