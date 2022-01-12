package fr.oukilson.backend;

import fr.oukilson.backend.controller.GameController;
import fr.oukilson.backend.dtos.GameDTO;
import fr.oukilson.backend.dtos.GameUuidDTO;
import fr.oukilson.backend.services.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@WebMvcTest(controllers = GameController.class)
public class OukilsonBackendApplicationTests {

	/**
	@Test
	void contextLoads() {
	}
	*/


	// MockMvc is injected
	// MockMvc permits to simulate a component
	@Autowired
	private MockMvc mockMvc;

	// The service is mocked
	// The service is copied
	@MockBean
	private GameService service;

	/**
	 * A 404 page is return if a game is not delivered by the route
	 * @throws Exception
	 */
	@Test
	public void testFindByUuidWhereWrongUuidOrNonexistentGame() throws Exception {
		// A request is executed on /game/uuid/248efeb9-3d1a-4905-89b3-7289ac47c682
		// because of the perform method of mockMvc
		this.mockMvc.perform(get("/uuid/248efeb9-3d1a-4905-89b3-7289ac47c682"))
				.andExpect(status().isNotFound()); // The status must be 404
	}

	/**
	 * The route which must be return an existing game with its uuid is verified
	 * @throws Exception
	 */
	@Test
	public void testFindOneGameByUuid() throws Exception {
		/**
		 * START
		 * The service is mocked to return a gameUuidDTO
		 * The existence of a game is simulated in the DB
		 */
		// A gameUuidDTO is created
		GameUuidDTO gameUuidDTO = this.gameUuidDTO();
		// The given DBMockito method is called to mock the service
		// The parameter of this method is the service method which is mocked
		BDDMockito.given(service.findByUuid("fecc2fb7-8af2-4f80-907e-bcac17739749"))
				.willReturn(Optional.of(gameUuidDTO)); // DBMockito is called if this method is called
		                                               // Its response is the parameter
		/**
		 * END
		 */
		/// The route which returns the game is tested
		MvcResult result = this.mockMvc.perform(get("/game/uuid/fecc2fb7-8af2-4f80-907e-bcac17739749"))
				.andExpect(status().isOk())
				.andReturn();
		// The mockMvc result is in the result variable
		// A Gson object is initialized to transform the object in JSON
		Gson json = new GsonBuilder().create();
		// The JSON body is transformed into GameUuidDTO
		GameUuidDTO body = json.fromJson(
				// The content of MyResult is return,
				// The response is delivered with : "getResponse"
				// The body content of response is delivered in String: "getContentAsString"
				result.getResponse().getContentAsString(),
				// Gson transforms the body object in GameUuidDTO
				GameUuidDTO.class
		);
		// Tests
		Assertions.assertEquals(body.getUuid(), this.gameUuidDTO().getUuid());
		Assertions.assertEquals(body.getName(), this.gameUuidDTO().getName());
	}


	/**
	 * A 404 page is return if the information of a game is not delivered by the route
	 * @throws Exception
	 */
	@Test
	public void testDisplayByUuidWhereWrongUuidOrNonexistentGame() throws Exception {
		// A request is executed on /game/display/248efeb9-3d1a-4905-89b3-7289ac47c682
		// because of the perform method of mockMvc
		this.mockMvc.perform(get("/display/248efeb9-3d1a-4905-89b3-7289ac47c682"))
				.andExpect(status().isNotFound()); // The status must be 404
	}


	/**
	 * The route which must be return the information of an existing game with its uuid is verified
	 * @throws Exception
	 */
	@Test
	public void testDisplayByUuid() throws Exception {
		/**
		 * START
		 * The service is mocked to return a gameUuidDTO
		 * The existence of a game is simulated in the DB
		 */
		// A gameUuidDTO is created
		GameDTO gameDTO = this.gameDTO();
		// The given DBMockito method is called to mock the service
		// The parameter of this method is the service method which is mocked
		BDDMockito.given(service.displayByUuid("fecc2fb7-8af2-4f80-907e-bcac17739749"))
				.willReturn(Optional.of(gameDTO)); // DBMockito is called if this method is called
		// Its response is the parameter
		/**
		 * END
		 */
		/// The route which returns the game is tested
		MvcResult result = this.mockMvc.perform(get("/game/display/fecc2fb7-8af2-4f80-907e-bcac17739749"))
				.andExpect(status().isOk())
				.andReturn();
		// The mockMvc result is in the result variable
		// A Gson object is initialized to transform the object in JSON
		Gson json = new GsonBuilder().create();
		// The JSON body is transformed into GameUuidDTO
		GameDTO body = json.fromJson(
				// The content of MyResult is return,
				// The response is delivered with : "getResponse"
				// The body content of response is delivered in String: "getContentAsString"
				result.getResponse().getContentAsString(),
				// Gson transforms the body object in GameUuidDTO
				GameDTO.class
		);
		// Tests
		Assertions.assertEquals(body.getUuid(), this.gameDTO().getUuid());
		Assertions.assertEquals(body.getName(), this.gameDTO().getName());
		Assertions.assertEquals(body.getMinPlayer(), this.gameDTO().getMinPlayer());
		Assertions.assertEquals(body.getMaxPlayer(), this.gameDTO().getMaxPlayer());
		Assertions.assertEquals(body.getMinTime(), this.gameDTO().getMinTime());
		Assertions.assertEquals(body.getMaxTime(), this.gameDTO().getMaxTime());
		Assertions.assertEquals(body.getMinAge(), this.gameDTO().getMinAge());
	}

	/**
	 * The route which must return an existing game with its name is verified
	 * @throws Exception
	 */
	@Test
	public void testFindOneGameByName() throws Exception {
		/**
		 * START
		 * The service is mocked to return a gameUuidDTO
		 * The existence of a game is simulated in the DB
		 */
		// A List<gameUuidDTO> is created
		List<GameUuidDTO> gameUuidDTOs = this.gameUuidDTOs();
		// The given DBMockito method is called to mock the service
		// The parameter of this method is the service method which is mocked
		BDDMockito.given(service.findByName("7 Wonders"))
				.willReturn(gameUuidDTOs); // DBMockito is called if this method is called
		// Its response is the parameter
		/**
		 * END
		 */
		/// The route which returns the game is tested
		MvcResult result = this.mockMvc.perform(get("/game/name/7 Wonders"))
				.andExpect(status().isOk())
				.andReturn();
		// The mockMvc result is in the result variable
		// A Gson object is initialized to transform the object in JSON
		Gson json = new GsonBuilder().create();
		// The JSON body is transformed into GameUuidDTO
		GameUuidDTO body = json.fromJson(
				// The content of MyResult is return,
				// The response is delivered with : "getResponse"
				// The body content of response is delivered in String: "getContentAsString"
				result.getResponse().getContentAsString(),
				// Gson transforms the body object in GameUuidDTO
				GameUuidDTO.class
		);
		// Tests
		Assertions.assertEquals(body.getUuid(), this.gameUuidDTO().getUuid());
		Assertions.assertEquals(body.getName(), this.gameUuidDTO().getName());
	}



	// Methods to create gameDTO and gameUuidDTO
	private GameDTO gameDTO() {
		return new GameDTO(
				"fecc2fb7-8af2-4f80-907e-bcac17739749",
				"7 Wonders",
				3,
				7,
				30,
				30,
				10,
				"Antoine Bauza"
		);
	}

	private GameUuidDTO gameUuidDTO() {
		return new GameUuidDTO(
				"fecc2fb7-8af2-4f80-907e-bcac17739749",
				"7 Wonders"
		);
	}

	private List<GameUuidDTO> gameUuidDTOs() {
		List<GameUuidDTO> gameUuidDTOs = new LinkedList<GameUuidDTO>();
		GameUuidDTO gameUuidDTO = new GameUuidDTO(
				"fecc2fb7-8af2-4f80-907e-bcac17739749",
				"7 Wonders"
		);
		gameUuidDTOs.add(gameUuidDTO);
		return(gameUuidDTOs);
	}
}

