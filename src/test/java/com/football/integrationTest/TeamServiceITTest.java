package com.football.integrationTest;

import com.football.dtos.PlayerDTO;
import com.football.dtos.TeamResponseDTO;
import com.football.dtos.handlerDTOs.PropertyApiError;
import com.football.entity.Team;
import com.football.entity.enums.Position;
import com.football.repository.TeamRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class TeamServiceITTest
{

    private final String OGC_NICE_MEN = "OGC NICE MEN";
    private final String TEREM_MOFFI = "Terem MOFFI";
    private final String ACRONYM = "We are a Nice team";

    private final String OGC_NICE_WOMEN = "OGC Nice Women";
    private final String ACRONYM_WOMEN_TEAM = "Les Nicoise";

    @Autowired
    private TeamRepository teamRepository;

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        RestAssured.port = port;
        teamRepository.deleteAll();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void Test_whenCreateTeam_validateInput_badRequest() throws JSONException
    {
        JSONObject teamJson = new JSONObject();
        teamJson.put("name", OGC_NICE_MEN);

        // must return 400, bad request
        List<?> errorResponse = given().port(port).body(teamJson.toString()).contentType(ContentType.JSON)
                .expect().contentType(ContentType.JSON).when().post("api/teams")
                .then().assertThat().statusCode(400).extract().body().as(List.class);
        assertEquals(3, errorResponse.size());
        assertTrue(errorResponse.contains("acronym: must not be blank"));
        assertTrue(errorResponse.contains("acronym: must not be null"));
        assertTrue(errorResponse.contains("budget: must not be null"));
    }

    @Test
    public void whenCreateTeam_ok() throws JSONException
    {
        JSONObject teamJson = new JSONObject().put("name", OGC_NICE_MEN).put("acronym", ACRONYM).put("budget", 100_000_000L);
        JSONArray players = new JSONArray().put(new JSONObject().put("name", TEREM_MOFFI).put("position", Position.FORWARD));
        teamJson.put("players", players);

        // must be well created
        TeamResponseDTO teamResponseDTO = given().port(port).body(teamJson.toString()).contentType(ContentType.JSON)
                .expect().contentType(ContentType.JSON).when().post("api/teams")
                .then().assertThat().statusCode(201).extract().body().as(TeamResponseDTO.class);

        assertNotNull(teamResponseDTO.getId());
        assertNotNull(teamResponseDTO.getDateCreated());
        assertEquals(OGC_NICE_MEN, teamResponseDTO.getName());
        assertEquals(ACRONYM, teamResponseDTO.getAcronym());
        assertEquals(BigDecimal.valueOf(100_000_000L), teamResponseDTO.getBudget());
        List<PlayerDTO> playersResponseDTO = teamResponseDTO.getPlayers();
        assertNotNull(playersResponseDTO);
        assertEquals(1, playersResponseDTO.size());
        assertEquals(TEREM_MOFFI, playersResponseDTO.get(0).getName());
        assertEquals(Position.FORWARD, playersResponseDTO.get(0).getPosition());

        List response = given().port(port).expect().contentType(ContentType.JSON)
                .when().get("api/teams")
                .then().assertThat().statusCode(200).extract().body().as(List.class);
        assertEquals(1, response.size());
    }

    @Test
    public void whenCreateTeam_getAllTeamWithPagination_ok() throws JSONException
    {
        JSONObject teamJson = new JSONObject().put("name", OGC_NICE_MEN).put("acronym", ACRONYM).put("budget", 100_000_000L);
        JSONArray players = new JSONArray().put(new JSONObject().put("name", TEREM_MOFFI).put("position", Position.FORWARD));
        teamJson.put("players", players);

        given().port(port).body(teamJson.toString()).contentType(ContentType.JSON)
                .expect().contentType(ContentType.JSON).when().post("api/teams")
                .then().assertThat().statusCode(201);

        teamJson = new JSONObject().put("name", OGC_NICE_WOMEN).put("acronym", ACRONYM_WOMEN_TEAM).put("budget", 50_000_000L);
        given().port(port).body(teamJson.toString()).contentType(ContentType.JSON)
                .expect().contentType(ContentType.JSON).when().post("api/teams")
                .then().assertThat().statusCode(201);

        List response = given().port(port).expect().contentType(ContentType.JSON)
                .when().get("api/teams")
                .then().assertThat().statusCode(200).extract().body().as(List.class);
        assertEquals(2, response.size());

        response = given().param("page", 0).param("size", 1).param("direction", "ASC").param("sortBy", "name")
                            .port(port).expect().contentType(ContentType.JSON)
                            .when().get("api/teams")
                            .then().assertThat().statusCode(200).extract().body().as(List.class);
        assertEquals(1, response.size());

        PropertyApiError error = given().param("page", 0).param("size", 1).param("direction", "ASC").param("sortBy", "fakeField")
                .port(port).expect().contentType(ContentType.JSON)
                .when().get("api/teams")
                .then().assertThat().statusCode(400).extract().body().as(PropertyApiError.class);
        assertEquals("fakeField", error.getProperty());
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), error.getError());
        assertEquals("No property 'fakeField' found for type 'Team'", error.getMessage());
        assertTrue(true);
    }

    @Test
    public void givenExistingTeam_whenDeleteTeam_thenTeamDeletedSuccessfully() {
        // Create a team to delete
        Team team = new Team();
        team.setName("Barcelona");
        team.setAcronym("BAR");
        team.setBudget(BigDecimal.valueOf(10000000));
        teamRepository.save(team);

        // Perform the delete request
        given().delete("/api/teams/" + team.getId())
                .then().statusCode(204);

        // Verify that the team was deleted
        assertFalse(teamRepository.findById(team.getId()).isPresent());

        given().delete("/api/teams/" + team.getId()).then().statusCode(404);
    }

    @Test
    public void updateTeamTest() throws JSONException
    {
        JSONObject teamJson = new JSONObject().put("name", OGC_NICE_MEN).put("acronym", ACRONYM).put("budget", 100_000_000L);
        JSONArray players = new JSONArray().put(new JSONObject().put("name", TEREM_MOFFI).put("position", Position.FORWARD));
        teamJson.put("players", players);

        // must be well created
        TeamResponseDTO teamResponseDTO = given().port(port).body(teamJson.toString()).contentType(ContentType.JSON)
                .expect().contentType(ContentType.JSON).when().post("api/teams")
                .then().assertThat().statusCode(201).extract().body().as(TeamResponseDTO.class);

        teamJson.put("name", "name updated");
        players = new JSONArray().put(new JSONObject().put("name", "Player Name Updated").put("position", Position.FORWARD));
        teamJson.put("players", players);

        teamResponseDTO = given().port(port).pathParam("id", teamResponseDTO.getId()).body(teamJson.toString()).contentType(ContentType.JSON)
                .expect().contentType(ContentType.JSON).when().put("api/teams/{id}")
                .then().assertThat().statusCode(202).extract().body().as(TeamResponseDTO.class);
        assertEquals("name updated" , teamResponseDTO.getName());
        assertEquals("Player Name Updated", teamResponseDTO.getPlayers().get(0).getName());

        assertTrue(true);
    }

}
