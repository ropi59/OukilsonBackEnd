package fr.oukilson.backend.controller;

import fr.oukilson.backend.dto.ResponseDTO;
import fr.oukilson.backend.dto.UserCreationDTO;
import com.google.gson.Gson;
import fr.oukilson.backend.service.UserService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    public UserCreationDTO userCreationDTO(){
        return new UserCreationDTO("password", "email@email.com", "nickname");
    }

    public Gson gson(){
        return new Gson();
    }
    @Test
    public void testCreateUser() throws Exception{
        UserCreationDTO userCreationDTO = userCreationDTO();
        ResponseDTO responseDTO = new ResponseDTO(true, "success");
        given(userService.createUser(userCreationDTO)).willReturn(responseDTO);
        this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson().toJson(userCreationDTO)))
                .andExpect(status().isCreated());
    }
}
