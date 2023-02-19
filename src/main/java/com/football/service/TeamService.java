package com.football.service;

import com.football.dtos.TeamResponseDTO;
import com.football.dtos.TeamRequestDTO;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * The interface Team service.
 */
public interface TeamService {


    /**
     * Retrieves all teams.
     * @param pageable the pageable.
     * @return list of all teams.
     */
    List<TeamResponseDTO> getAllTeams(Pageable pageable);

    /**
     * Retrieves a team by ID.
     * @param id Team Id
     * @return the saved team
     */
    TeamResponseDTO getTeamById(Long id);

    /**
     * insert team.
     * @param teamRequestDTO team to be inserted.
     * @return team saved.
     */
    TeamResponseDTO insertTeam(TeamRequestDTO teamRequestDTO);

    /**
     * Deletes a team with the specified ID from the database.
     *
     * @param id the ID of the team to delete
     * @throws EntityNotFoundException if a team with the specified ID does not exist
     */
    public void deleteTeamById(Long id);

    /**
     * Update a team entity.
     *
     * @param id             The ID of the team to update.
     * @param teamRequestDTO The DTO containing the updated team data.
     * @return The updated team entity.
     * @throws EntityNotFoundException If the team with the given ID is not found.
     */
    public TeamResponseDTO updateTeam(Long id, TeamRequestDTO teamRequestDTO) throws EntityNotFoundException;

}
