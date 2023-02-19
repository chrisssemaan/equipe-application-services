package com.football.controller;

import com.football.dtos.PlayerDTO;
import com.football.dtos.PlayerRequestDTO;
import com.football.dtos.TeamRequestDTO;
import com.football.dtos.TeamResponseDTO;
import com.football.entity.enums.Position;
import com.football.service.TeamService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.NestedServletException;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private TeamResponseDTO teamDTOMen;

    private TeamResponseDTO teamDTOWomen;

    @Before
    public void setup() {
        teamDTOMen = TeamResponseDTO.builder().id(1L).name("OGC Nice MEN").budget(BigDecimal.valueOf(100_000_000L)).acronym("We are a Nice team").dateCreated(LocalDateTime.now())
                            .players(List.of(PlayerDTO.builder().position(Position.FORWARD).name("Terem MOFFI").build())).build();

        teamDTOWomen = TeamResponseDTO.builder().id(2L).name("OGC Nice Women").budget(BigDecimal.valueOf(50_000_000L)).acronym("Les Nicoise").dateCreated(LocalDateTime.now())
                            .players(List.of(PlayerDTO.builder().position(Position.FORWARD).name("Dialamba DIABY").build())).build();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void whenGetAllTeams_isOk() {
        List<TeamResponseDTO> teamDTOList = List.of(teamDTOMen, teamDTOWomen);
        when(teamService.getAllTeams(any())).thenReturn(teamDTOList);
        ResponseEntity<List<TeamResponseDTO>> responseTeams = teamController.getAllTeams(0, 10, "ASC", "name");

        assertEquals(HttpStatus.OK, responseTeams.getStatusCode());
        assertNotNull(responseTeams.getBody());
        assertEquals(2, responseTeams.getBody().size());
        assertEquals(teamDTOList, responseTeams.getBody());
    }

    @Test
    public void deleteTeam_shouldDeleteTeamIfExists() {
        doNothing().when(teamService).deleteTeamById(1L);

        teamController.deleteTeam(1L);

        verify(teamService).deleteTeamById(1L);
    }

    @Test
    public void deleteTeam_shouldReturn404IfTeamDoesNotExist() {
        doThrow(EntityNotFoundException.class).when(teamService).deleteTeamById(1L);

        assertThrows(EntityNotFoundException.class, () -> teamController.deleteTeam(1L));
    }



}
