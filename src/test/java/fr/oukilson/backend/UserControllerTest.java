package fr.oukilson.backend;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.oukilson.backend.controller.UserController;
import fr.oukilson.backend.dto.UserEditDTO;
import fr.oukilson.backend.dto.UserProfilDTO;
import fr.oukilson.backend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.SQLException;
import java.util.Optional;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    /**
     * init blob
     */
    private byte[] icon = "My Blob".getBytes();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    /**
     * init UserDTO for testing
     * @return UserDTO
     * @throws SQLException
     */
    private UserProfilDTO userDTO() throws SQLException {
        return new UserProfilDTO(
                "johnny"
    );
    }

    private UserEditDTO userEditDTO() throws SQLException {
        return new UserEditDTO(
                "azerty",
                "john",
                "doe",
                "johndoe@mail.com",
                "johnny"
        );
    }

    private UserEditDTO userEditDTOUpdated() throws SQLException {
        return new UserEditDTO(
                "qsdfgh",
                "frank",
                "unamed",
                "frankunamed@mail.com",
                "johnny"
        );
    }
    /**
     * test of findByNickname Road. check by user's nickname
     * @throws Exception
     */
    @Test
    public void testFindUserByNickname () throws Exception {
        UserEditDTO userEditDTO = this.userEditDTO();
        BDDMockito.given(userService.findUserByNickname("johnny"))
                .willReturn(Optional.of(userEditDTO));
        MvcResult result = this.mockMvc.perform(get("/users/johnny"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().create();
        UserProfilDTO body = json.fromJson(
                result.getResponse().getContentAsString(), UserProfilDTO.class);
        Assertions.assertEquals(body.getNickname(), this.userDTO().getNickname());
    }

    /**
     * test of wrong nickname search. status 404 expect.
     * @throws Exception
     */
    @Test
    public void testFindUserByNicknameWhereWrongNicknameOrInexistantUser () throws Exception {
        this.mockMvc.perform(get("/users/toto"))
                .andExpect(status().isNotFound());
    }

    /**
     * test to check if we get list of all users.
     * @throws Exception
     */
    @Test
    public void testFindAllUsers() throws Exception{
            this.mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
    }

    /**
     * check if user's creation is ok
     * @throws Exception
     */
    @Test
    public void testCreateUser() throws Exception{
        UserEditDTO userEditDTO = this.userEditDTO();
        Gson json = new GsonBuilder().create();
        String body = json.toJson(userEditDTO);
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    /**
     * check if firstname update works
     * @throws Exception
     */
    @Test
    public void testUpdateUsersFirstname()throws Exception{
        UserEditDTO userEditDTO = this.userEditDTO();
        UserEditDTO userEditDTOUpdated = this.userEditDTOUpdated();

        BDDMockito.given(userService.findUserByNickname("johnny"))
                .willReturn(Optional.of(userEditDTO));
        MvcResult result = this.mockMvc.perform(get("/users/johnny"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().create();
        UserEditDTO body = json.fromJson(result.getResponse().getContentAsString(), UserEditDTO.class);

        BDDMockito.when(userService.save(any(UserEditDTO.class)))
                .thenReturn(userEditDTOUpdated);
        body.setFirstName("frank");
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyToSave))
                .andReturn();
        UserEditDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), UserEditDTO.class);
        Assertions.assertEquals(finalBody.getFirstName(), userEditDTOUpdated.getFirstName());
    }

    /**
     * check if name update works
     * @throws Exception
     */
    @Test
    public void testUpdateUsersLastname()throws Exception {
        UserEditDTO userEditDTO = this.userEditDTO();
        UserEditDTO userEditDTOUpdated = this.userEditDTOUpdated();

        BDDMockito.given(userService.findUserByNickname("johnny"))
                .willReturn(Optional.of(userEditDTO));
        MvcResult result = this.mockMvc.perform(get("/users/johnny"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().create();
        UserEditDTO body = json.fromJson(result.getResponse().getContentAsString(), UserEditDTO.class);

        BDDMockito.when(userService.save(any(UserEditDTO.class)))
                .thenReturn(userEditDTOUpdated);
        body.setLastName("unamed");
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSave))
                .andReturn();
        UserEditDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), UserEditDTO.class);
        Assertions.assertEquals(finalBody.getLastName(), userEditDTOUpdated.getLastName());
    }

    /**
     * check if email update works
     * @throws Exception
     */
    @Test
    public void testUpdateUsersMail()throws Exception {
        UserEditDTO userEditDTO = this.userEditDTO();
        UserEditDTO userEditDTOUpdated = this.userEditDTOUpdated();

        BDDMockito.given(userService.findUserByNickname("johnny"))
                .willReturn(Optional.of(userEditDTO));
        MvcResult result = this.mockMvc.perform(get("/users/johnny"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().create();
        UserEditDTO body = json.fromJson(result.getResponse().getContentAsString(), UserEditDTO.class);

        BDDMockito.when(userService.save(any(UserEditDTO.class)))
                .thenReturn(userEditDTOUpdated);
        body.setEmail("frankunamed@mail.com");
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSave))
                .andReturn();
        UserEditDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), UserEditDTO.class);
        Assertions.assertEquals(finalBody.getEmail(), userEditDTOUpdated.getEmail());
    }
}
