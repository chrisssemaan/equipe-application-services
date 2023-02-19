package com.football.service;

import com.football.dtos.PlayerDTO;
import com.football.dtos.PlayerRequestDTO;
import com.football.dtos.TeamRequestDTO;
import com.football.dtos.TeamResponseDTO;
import com.football.entity.Player;
import com.football.entity.Team;
import com.football.entity.enums.Position;
import com.football.repository.TeamRepository;
import com.football.service.impl.DefaultTeamService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerService playerService;
    @InjectMocks
    private DefaultTeamService teamService;


    private Team teamMen;

    private TeamResponseDTO teamDTOMen;
    private Long teamId;

    @Before
    public void setup() {
        teamId = 1L;
        teamMen = Team.builder().name("OGC Nice MEN").acronym("We are a Nice team")
                .budget(BigDecimal.valueOf(100000000L)).players(new ArrayList<>()).dateCreated(LocalDateTime.now()).build();
        teamMen.getPlayers().add(Player.builder().position(Position.FORWARD).name("Terem MOFFI").team(teamMen).build());

        teamDTOMen = TeamResponseDTO.builder().id(teamMen.getId()).name(teamMen.getName()).budget(teamMen.getBudget())
                .acronym(teamMen.getAcronym()).dateCreated(teamMen.getDateCreated())
                .players(List.of(PlayerDTO.builder().position(Position.FORWARD).name("Terem MOFFI").build()))
                .build();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void whenGetAllTeams_isOk() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(teamRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(new ArrayList<>()));
        assertEquals(0, teamService.getAllTeams(pageRequest).size());
        verify(teamRepository, Mockito.times(1)).findAll(pageRequest);

        // assert data will be retrieved and mapped properly
        Team womenTeam = Team.builder().name("OGC Nice Women").acronym("Les Nicoise")
                .budget(BigDecimal.valueOf(50000000L)).players(new ArrayList<>()).dateCreated(LocalDateTime.now()).build();
        womenTeam.getPlayers().add(Player.builder().position(Position.FORWARD).name("Dialamba DIABY").team(teamMen).build());
        TeamResponseDTO teamDTO2 = TeamResponseDTO.builder().id(womenTeam.getId()).name(womenTeam.getName()).budget(womenTeam.getBudget())
                .acronym(womenTeam.getAcronym()).dateCreated(womenTeam.getDateCreated())
                .players(List.of(PlayerDTO.builder().position(Position.FORWARD).name("Dialamba DIABY").build())).build();
        List<Team> teams = new ArrayList<>(List.of(teamMen, womenTeam));
        List<TeamResponseDTO> expectedTeams = new ArrayList<>(List.of(teamDTOMen, teamDTO2));

        when(teamRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(teams));
        List<TeamResponseDTO> result = teamService.getAllTeams(pageRequest);
        assertEquals(2, result.size());
        assertEquals(expectedTeams, result);
        verify(teamRepository, Mockito.times(2)).findAll(pageRequest);

    }

    @Test
    public void whenGetTeamById_NotFound_throwException() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows("abc", EntityNotFoundException.class, () -> teamService.getTeamById(teamId));
        String TEAM_DOES_NOT_EXIST = "Team with id [%s] does not exist.";
        assertEquals(format(TEAM_DOES_NOT_EXIST, teamId), exception.getMessage());
    }

    @Test
    public void whenGetTeamById_isOk() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(teamMen));
        TeamResponseDTO result = teamService.getTeamById(teamId);
        assertEquals(teamDTOMen, result);
    }

    @Test
    public void testInsertTeam() {
        // Create a mock TeamRequestDTO
        TeamRequestDTO teamRequestDTO = TeamRequestDTO.builder().name("OGC Nice MEN")
                .acronym("OGC").budget(BigDecimal.valueOf(100000000L)).build();
        PlayerRequestDTO playerRequestDTO = PlayerRequestDTO.builder().name("Cristiano").position(Position.FORWARD).build();
        teamRequestDTO.setPlayers(List.of(playerRequestDTO));

        // Create a mock Team object
        Team team = Team.builder().name("OGC Nice MEN").acronym("OGC")
                .budget(BigDecimal.valueOf(100000000L)).build();
        Player player = Player.builder().name("Cristiano").position(Position.FORWARD).build();
        team.setPlayers(List.of(player));

        // Mock the repository
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        // Call the service method
        TeamResponseDTO teamResponseDTO = teamService.insertTeam(teamRequestDTO);

        // Verify the results
        assertNotNull(teamResponseDTO);
        assertEquals(team.getId(), teamResponseDTO.getId());
        assertEquals(team.getName(), teamResponseDTO.getName());
        assertEquals(team.getAcronym(), teamResponseDTO.getAcronym());
        assertEquals(team.getBudget(), teamResponseDTO.getBudget());
        assertEquals(team.getPlayers().size(), teamResponseDTO.getPlayers().size());
    }

    @Test
    public void deleteTeam_shouldDeleteTeamIfExists() {
        Team team = Team.builder()
                .id(1L)
                .name("Test Team")
                .acronym("TT")
                .budget(BigDecimal.valueOf(1000000))
                .build();
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        teamService.deleteTeamById(1L);

        verify(teamRepository).delete(team);
    }

    @Test
    public void deleteTeam_shouldThrowExceptionIfTeamDoesNotExist() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, (ThrowingRunnable) () -> teamService.deleteTeamById(1L));
    }

    @Test
    public void testUpdateTeam() throws EntityNotFoundException {
        // create a test team
        TeamRequestDTO teamRequestDTO = TeamRequestDTO.builder().name("OGC Nice MEN")
                .acronym("OGC").budget(BigDecimal.valueOf(100000000L)).build();
        PlayerRequestDTO playerRequestDTO = PlayerRequestDTO.builder().name("Cristiano").position(Position.FORWARD).build();
        teamRequestDTO.setPlayers(List.of(playerRequestDTO));

        // Create a mock Team object
        Team team = Team.builder().id(1L).name("OGC Nice MEN").acronym("OGC")
                .budget(BigDecimal.valueOf(100000000L)).build();
        Player player = Player.builder().name("Cristiano").position(Position.FORWARD).build();
        team.setPlayers(List.of(player));

        // Mock the repository
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        TeamResponseDTO teamResponseDTO = teamService.insertTeam(teamRequestDTO);

        // create a test team request DTO
        teamRequestDTO = TeamRequestDTO.builder().name("Updated Test Team")
                .acronym("UTT").budget(new BigDecimal("2000000")).build();
        List<PlayerRequestDTO> playerRequestDTOs = new ArrayList<>();
        playerRequestDTOs.add(new PlayerRequestDTO("John Doe", Position.FORWARD));
        teamRequestDTO.setPlayers(playerRequestDTOs);

        // update the test team
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        List<Player> players = List.of(Player.builder().name("John Doe").position(Position.FORWARD).build());
        when(playerService.requestDtosToPlayers(any(List.class))).thenReturn(players);
        TeamResponseDTO updatedTeam = teamService.updateTeam(teamResponseDTO.getId(), teamRequestDTO);

        // assert that the team was updated correctly
        assertEquals("Updated Test Team", updatedTeam.getName());
        assertEquals("UTT", updatedTeam.getAcronym());
        assertEquals(new BigDecimal("2000000"), updatedTeam.getBudget());
        assertEquals(1, updatedTeam.getPlayers().size());
        assertEquals("John Doe", updatedTeam.getPlayers().get(0).getName());
        assertEquals(Position.FORWARD, updatedTeam.getPlayers().get(0).getPosition());
    }

}
