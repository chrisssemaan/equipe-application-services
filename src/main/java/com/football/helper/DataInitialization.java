package com.football.helper;

import com.football.entity.Player;
import com.football.entity.Team;
import com.football.entity.enums.Position;
import com.football.repository.TeamRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Data initialization.
 */
@Component
@Log4j2
public class DataInitialization implements CommandLineRunner {

    private final TeamRepository teamRepository;

    /**
     * Instantiates a new Data initialization.
     *
     * @param teamRepository the team repository
     */
    public DataInitialization(TeamRepository teamRepository)
    {
        this.teamRepository = teamRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Loading data...");
        try {
            List<Team> teams = new ArrayList<>();

            // create men team of Nice
            Team team = Team.builder().name("OGC Nice MEN").acronym("We are a Nice team").budget(BigDecimal.valueOf(100000000L)).build();
            List<Player> players = new ArrayList<>();
            players.add(getPlayer(Position.FORWARD, "Terem MOFFI", team));
            players.add(getPlayer(Position.FORWARD, "Nicolas PEPE", team));
            players.add(getPlayer(Position.FORWARD, "Bilal BRAHIMI", team));
            players.add(getPlayer(Position.MIDFIELDER, "Ross BARKLEY", team));
            players.add(getPlayer(Position.MIDFIELDER, "Reda BELAHYANE", team));
            players.add(getPlayer(Position.MIDFIELDER, "Sofian DIOP", team));
            players.add(getPlayer(Position.MIDFIELDER, "Pablo ROSARIO", team));
            players.add(getPlayer(Position.DEFENDER, "Antoine MENDI", team));
            players.add(getPlayer(Position.DEFENDER, "Joe BRYAN", team));
            players.add(getPlayer(Position.DEFENDER, "DANTE", team));
            players.add(getPlayer(Position.GOALKEEPER, "Kasper SCHMEICHEL", team));
            team.setPlayers(players);
            teams.add(team);

            // create women team of Nice
            team = Team.builder().name("OGC Nice Women").acronym("Les Nicoise").budget(BigDecimal.valueOf(50000000L)).build();
            players = new ArrayList<>();
            players.add(getPlayer(Position.FORWARD, "Dialamba DIABY", team));
            players.add(getPlayer(Position.FORWARD, "Sarah PALACIN", team));
            players.add(getPlayer(Position.FORWARD, "Fatoumata BALDÉ", team));
            players.add(getPlayer(Position.MIDFIELDER, "Lina BOUZIANI", team));
            players.add(getPlayer(Position.MIDFIELDER, "Melissa GODART", team));
            players.add(getPlayer(Position.MIDFIELDER, "Soriana CONSTANT", team));
            players.add(getPlayer(Position.MIDFIELDER, "Rachel ROBERT", team));
            players.add(getPlayer(Position.DEFENDER, "Barbara BOUCHET", team));
            players.add(getPlayer(Position.DEFENDER, "Coline BOUBY", team));
            players.add(getPlayer(Position.DEFENDER, "Clara GALLI", team));
            players.add(getPlayer(Position.GOALKEEPER, "Maureen SAINT LÉGER", team));
            team.setPlayers(players);

            teams.add(team);
            teamRepository.saveAll(teams);

            log.info("Data Loaded...");

        } catch (Exception e) {
            log.error("Error Loading initial Data", e);
        }
    }

    private Player getPlayer(Position position, String playerName, Team team) {
        return Player.builder().position(position).name(playerName).team(team).build();
    }
}
