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

    public UserDTO secondUserDTO(){
        return new UserDTO(2L, "email2@email.com", "nickname2");
    }

    public ResponseDTO responseSuccess(){
        return new ResponseDTO(true, "success");
    }

    public ResponseDTO responseFail(){
        return new ResponseDTO(false, "fail");
    }
    ////////// TESTS OF CREATE USER FUNCTION ////////////
    /**
     * tests that the function creates a user and returns a 201 http code
     * @throws Exception an Exception
     */
    @Test
    public void testCreateUser() throws Exception{
        this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationDTO())))
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
        this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationDTO())))
                .andExpect(status().isCreated());
        // create a captor object to store the DTO we send to the service
        ArgumentCaptor<UserCreationDTO> userCreationDTOArgumentCaptor = ArgumentCaptor.forClass(UserCreationDTO.class);
        // checks that the service is called once, then calls it directly using the captor as argument
        verify(userService, times(1)).createUser(userCreationDTOArgumentCaptor.capture());
        // checks if the captor equals the original DTO
        Assertions.assertEquals(userCreationDTOArgumentCaptor.getValue(), userCreationDTO());
    }

    /**
     * test to check the returned value of the function
     * @throws Exception an exception
     */
    @Test
    public void testCreateUser_ThenReturnsResponseDTO() throws Exception{
        // tells the test that when the service is called it should return the responseDTO object
        when(userService.createUser(any(UserCreationDTO.class))).thenReturn(responseSuccess());
        MvcResult mvcResult = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationDTO())))
                .andExpect(status().isCreated())
                .andReturn();
        // Transforms the result of the request back into a responseDTO object & check for success
        ResponseDTO actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseDTO.class);
        Assertions.assertTrue(actualResponse.isSuccess());
    }

    ////////// TESTING FUNCTION THAT ADDS USERS TO A FRIEND LIST //////////

    /**
     * tests for valid use of addusertofriendlist method
     * @throws Exception an exception
     */
    @Test
    public void testAddUserToFriendList() throws Exception{
        // tell the test to return a given object when calling upon the service & stores them
        when(userService.findById(1L)).thenReturn(userDTO());
        when(userService.findById(2L)).thenReturn(secondUserDTO());
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
        when(userService.addUserToFriendList(anyLong(), anyLong())).thenReturn(responseSuccess());
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

    /**
     * verify that the link between the controller and the service is working as intended
     * @throws Exception an exception
     */
    @Test
    public void testAddUserToFriendList_thenMapsToService() throws Exception {
        // sets test behaviour
        when(userService.findById(1L)).thenReturn(userDTO());
        when(userService.findById(2L)).thenReturn(secondUserDTO());
        // mock finding users by their id
        MvcResult mainUserResult = this.mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult userToAddResult = this.mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andReturn();
        // parse the resulting data into DTOs so that we can add a user to the other's friend list
        UserDTO mainUser = objectMapper.readValue(mainUserResult.getResponse().getContentAsString(), UserDTO.class);
        UserOnListDTO userToAdd = objectMapper.readValue(userToAddResult.getResponse().getContentAsString(), UserOnListDTO.class);
        mainUser.getFriendList().add(userToAdd);
        // sets the expected response and checks it
        when(userService.addUserToFriendList(anyLong(), anyLong())).thenReturn(responseSuccess());
        this.mockMvc.perform(put("/users/add/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mainUser)))
                .andExpect(status().isOk());

        // tests that the service is called once and that the values match with the input parameters
        ArgumentCaptor<Long> userDTOArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> userDTOArgumentCaptor2 = ArgumentCaptor.forClass(Long.class);
        verify(userService, times(1))
                .addUserToFriendList(userDTOArgumentCaptor.capture(), userDTOArgumentCaptor2.capture());
        Assertions.assertEquals(userDTO().getId(), (long) userDTOArgumentCaptor.getValue());
        Assertions.assertEquals(secondUserDTO().getId(), (long) userDTOArgumentCaptor2.getValue());
    }

    /**
     * tests the method that removes a user from another's friend list, no exception
     * @throws Exception an exception
     */
    @Test
    public void testRemoveUserFromFriendList() throws Exception {
        // tell the test to return a given object when calling upon the service & stores them
        when(userService.findById(1L)).thenReturn(userDTO());
        when(userService.findById(2L)).thenReturn(secondUserDTO());
        MvcResult mainUserResult = this.mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult userToAddResult = this.mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andReturn();
        // map the returns into DTOs so that we can remove one from the friend list of the other
        UserDTO mainUser = objectMapper.readValue(mainUserResult.getResponse().getContentAsString(), UserDTO.class);
        UserOnListDTO userToRemove = objectMapper.readValue(userToAddResult.getResponse().getContentAsString(), UserOnListDTO.class);
        mainUser.getFriendList().remove(userToRemove);
        // tell the test to return a given response when calling upon a given service
        when(userService.removeUserFromFriendList(anyLong(), anyLong())).thenReturn(responseSuccess());
        MvcResult finalResult = this.mockMvc.perform(put("/users/remove/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mainUser)))
                .andExpect(status().isOk())
                .andReturn();
        // maps the returned response into a ResponseDTO object & tests it for validity
        ResponseDTO finalResponse = objectMapper.readValue(finalResult.getResponse().getContentAsString(), ResponseDTO.class);
        Assertions.assertTrue(finalResponse.isSuccess());
        Assertions.assertEquals("success", finalResponse.getMessage());
    }

    /**
     * tests that the function is working properly and that it calls the service adequately
     * @throws Exception an exception
     */
    @Test
    public void testRemoveUserFromFriendList_thenMapsToService() throws Exception {
        // tell the test what to expect when asked to perform certain tasks
        when(userService.findById(1L)).thenReturn(userDTO());
        when(userService.findById(2L)).thenReturn(secondUserDTO());
        // performs those tasks
        MvcResult mainUserResult = this.mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult secondUserResult = this.mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andReturn();
        // maps the returned values to their wanted types
        UserDTO mainUser = objectMapper.readValue(mainUserResult.getResponse().getContentAsString(), UserDTO.class);
        UserOnListDTO secondUser = objectMapper.readValue(secondUserResult.getResponse().getContentAsString(), UserOnListDTO.class);
        mainUser.getFriendList().remove(secondUser);
        // tell the test what to expect when asked to perform a certain task
        when(userService.removeUserFromFriendList(anyLong(), anyLong())).thenReturn(responseSuccess());
        // performs said task
        this.mockMvc.perform(put("/users/remove/1/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mainUser)))
                .andExpect(status().isOk());

        // now catch the arguments to test the fact that the service is called an appropriate number of times
        // and that the data is properly handled
        ArgumentCaptor<Long> mainUserID = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> secondUserID = ArgumentCaptor.forClass(Long.class);
        verify(userService, times(1)).removeUserFromFriendList(mainUserID.capture() ,secondUserID.capture());
        Assertions.assertEquals(userDTO().getId(), mainUserID.getValue());
        Assertions.assertEquals(secondUserDTO().getId(), secondUserID.getValue());
    }

    //////////// TESTING THE EMPTYLIST FUNCTION /////////////////

}
