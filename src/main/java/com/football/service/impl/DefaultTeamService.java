package com.football.service.impl;

import com.football.dtos.TeamResponseDTO;
import com.football.dtos.TeamRequestDTO;
import com.football.entity.Team;
import com.football.repository.TeamRepository;
import com.football.service.PlayerService;
import com.football.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * The type Default team service.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class DefaultTeamService implements TeamService {

    private final String TEAM_DOES_NOT_EXIST = "Team with id [%s] does not exist.";

    private final ModelMapper modelMapper = new ModelMapper();

    private final TeamRepository teamRepository;

    private final PlayerService playerService;

    /**
     * Retrieves all teams.
     * @param pageable the pageable.
     * @return list of all tedams.
     */
    @Override
    public List<TeamResponseDTO> getAllTeams(Pageable pageable) {
        log.info("Getting all Teams");

        List<TeamResponseDTO> teamDTOS = teamRepository.findAll(pageable).getContent()
                .stream().map(this::toResponseDTO).collect(Collectors.toList());

        log.trace("All returned Teams : {}", teamDTOS);
        log.info("Teams retrieved successfully with size [{}] ", teamDTOS.size());
        return teamDTOS;
    }

    /**
     * Retrieves a team by ID.
     * @param id Team Id
     * @return the saved team
     */
    @Override
    public TeamResponseDTO getTeamById(Long id) {
        log.info("Finding team by id [{}]", id);
        return toResponseDTO(findById(id));
    }

    /**
     * insert team.
     * @param teamRequestDTO team to be inserted.
     * @return team saved.
     */
    @Override
    public TeamResponseDTO insertTeam(TeamRequestDTO teamRequestDTO) {
        log.info("Inserting team");
        log.trace("Inserting team: {}", teamRequestDTO);
        Team team = teamRepository.save(requestDtoToTeam(teamRequestDTO));
        log.info("Teams inserted successfully with id {} ", team.getId());
        return toResponseDTO(team);
    }

    private TeamResponseDTO toResponseDTO(Team team) {
        return modelMapper.map(team, TeamResponseDTO.class);
    }

    private Team requestDtoToTeam (TeamRequestDTO teamRequestDTO) {
        return modelMapper.map(teamRequestDTO, Team.class);
    }

    /**
     * Finds a team by id.
     *
     * @param id the id
     * @return the team
     */
    private Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Team with id [{}], Not Found", id);
                    throw new EntityNotFoundException(format(TEAM_DOES_NOT_EXIST, id));
                });
    }

    /**
     * Deletes a team with the specified ID from the database.
     *
     * @param id the ID of the team to delete
     * @throws EntityNotFoundException if a team with the specified ID does not exist
     */
    public void deleteTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team with ID " + id + " not found"));
        teamRepository.delete(team);

        log.info("Deleted team with ID {}", id);
    }

    /**
     * Update a team entity.
     *
     * @param id             The ID of the team to update.
     * @param teamRequestDTO The DTO containing the updated team data.
     * @return The updated team entity.
     * @throws EntityNotFoundException If the team with the given ID is not found.
     */
    public TeamResponseDTO updateTeam(Long id, TeamRequestDTO teamRequestDTO) throws EntityNotFoundException {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isPresent()) {
            Team team = optionalTeam.get();
            team.setName(teamRequestDTO.getName());
            team.setAcronym(teamRequestDTO.getAcronym());
            team.setBudget(teamRequestDTO.getBudget());
            if (teamRequestDTO.getPlayers() != null) {
                team.setPlayers(playerService.requestDtosToPlayers(teamRequestDTO.getPlayers()));
            } else {
                team.setPlayers(Collections.emptyList());
            }
            return toResponseDTO(teamRepository.save(team));
        } else {
            throw new EntityNotFoundException(String.format("Team with ID %d not found", id));
        }
    }
}
