package com.football.controller;

import com.football.dtos.TeamRequestDTO;
import com.football.dtos.TeamResponseDTO;
import com.football.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

/**
 * The type Team controller.
 */
@Log4j2
@RequestMapping(value = "api/teams")
@RestController
@RequiredArgsConstructor
@Api(tags = {"team"})
public class TeamController {

    private final TeamService teamService;

    /**
     * Rest Resource that get the list of All store teams
     *
     * @param page      the page
     * @param size      the size
     * @param direction the direction
     * @param sortBy    the sort by
     * @return list of all {}
     */
    @ApiOperation(notes = "Returns a list of all store teams.",
                  value = "Get a list of all store teams.",
                  nickname = "listAll",
                  tags = {"teams"})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 200, message = "list of teams", response = TeamResponseDTO.class),
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams(
            @ApiParam(name =  "page", type = "Integer", value = "The page number", example = "0")
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,

            @ApiParam(name =  "size", type = "Integer", value = "The page size", example = "10")
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,

            @ApiParam(name =  "direction", type = "Sort.direction", value = "The sorting direction", example = "DESC")
            @RequestParam(value = "direction", defaultValue = "ASC", required = false) String direction,

            @ApiParam(name =  "sortBy", type = "String", value = "The sorting by", example = "name")
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy) {

            PageRequest pageRequest = PageRequest.of(page, size).withSort(Sort.by(Sort.Direction.fromString(direction), sortBy));
            return new ResponseEntity<>(teamService.getAllTeams(pageRequest), HttpStatus.OK);
    }


    // add an endpoint find by id

    /**
     * Deletes the team with the given ID.
     *
     * @param teamId The ID of the team to delete
     * @return Response Entity with status
     * @throws EntityNotFoundException if the team with the given ID does not exist
     */
    @ApiOperation(value = "Delete a team by ID", notes = "Deletes the team with the given ID.", tags = {"teams"})
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Team deleted successfully"),
            @ApiResponse(code = 404, message = "Team not found")})
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Object> deleteTeam(
            @ApiParam(value = "ID of the team to delete", example = "1") @PathVariable Long teamId) {
        teamService.deleteTeamById(teamId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Create user note history response entity.
     *
     * @param teamRequestDTO the team request dto
     * @return the response entity
     */
    @ApiOperation(notes = "Insert a team.",
            value = "Insert a team and return it.",
            nickname = "insertTeam",
            tags = {"teams"})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 201, message = "Team Created", response = TeamResponseDTO.class),
    })
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody TeamRequestDTO teamRequestDTO)  {

        return new ResponseEntity<>(teamService.insertTeam(teamRequestDTO), HttpStatus.CREATED);
    }

    /**

     Updates an existing team in the system.
     @param id the ID of the team to be updated
     @param teamRequestDTO the updated data for the team
     */
    @ApiOperation(value = "Update an existing team" , tags = {"teams"})
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Team updated successfully" , response = TeamResponseDTO.class),
            @ApiResponse(code = 404, message = "Team not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<TeamResponseDTO> updateTeam(
            @ApiParam(value = "Team ID", example = "1", required = true)
            @PathVariable Long id,
            @ApiParam(value = "Team data", required = true)
            @Valid @RequestBody TeamRequestDTO teamRequestDTO) {
        log.info("Updating team with ID {}", id);
        return new ResponseEntity<>(teamService.updateTeam(id, teamRequestDTO), HttpStatus.ACCEPTED);
    }
}
