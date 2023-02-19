package com.football.repository;

import com.football.entity.Player;
import com.football.entity.Team;
import com.football.entity.enums.Position;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TeamRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TeamRepository teamRepository;

    @Before
    public void setup() {

    }
    @After
    public void tearDown() {
        entityManager.clear();
    }

    @Test
    public void whenSaveTeam_findById_isOk() {
        // assert fin all return 0 records
        PageRequest pageRequest = PageRequest.of(0, 10);
        assertEquals(0, teamRepository.findAll(pageRequest).getTotalElements());

        // insert one record and assert findAll return only one
        Team menTeam = Team.builder().name("OGC Nice MEN").acronym("We are a Nice team").budget(BigDecimal.valueOf(100_000_000L)).players(new ArrayList<>()).build();
        menTeam.getPlayers().add(Player.builder().position(Position.FORWARD).name("Terem MOFFI").team(menTeam).build());
        entityManager.persist(menTeam);
        assertEquals(1, teamRepository.findAll(pageRequest).getTotalElements());

        // insert another record and assert findAll return two records
        Team womenTeam = Team.builder().name("OGC Nice Women").acronym("Les Nicoise").budget(BigDecimal.valueOf(50_000_000L)).players(new ArrayList<>()).build();
        womenTeam.getPlayers().add(Player.builder().position(Position.FORWARD).name("Dialamba DIABY").team(womenTeam).build());
        entityManager.persist(womenTeam);
        assertEquals(2, teamRepository.findAll(pageRequest).getTotalElements());

        // assert findAll with sorting by name
        List<Team> teamSorted = teamRepository.findAll(pageRequest.withSort(Sort.by(Sort.Direction.DESC, "name"))).getContent();
        assertEquals(womenTeam, teamSorted.get(0));
        assertEquals(menTeam, teamSorted.get(1));

        // assert findAll with sorting by acronym
        teamSorted = teamRepository.findAll(pageRequest.withSort(Sort.by(Sort.Direction.DESC, "acronym"))).getContent();
        assertEquals(menTeam, teamSorted.get(0));
        assertEquals(womenTeam, teamSorted.get(1));

        // assert findAll with sorting by acronym
        teamSorted = teamRepository.findAll(pageRequest.withSort(Sort.by(Sort.Direction.DESC, "budget"))).getContent();
        assertEquals(menTeam, teamSorted.get(0));
        assertEquals(womenTeam, teamSorted.get(1));

        // assert findById return the correct team
        Optional<Team> teamOptional = teamRepository.findById(womenTeam.getId());
        assertTrue(teamOptional.isPresent());
        assertEquals(womenTeam, teamOptional.get());

        // assert findById with a wrong id, return 0 records
        teamOptional = teamRepository.findById(555_555L);
        assertFalse(teamOptional.isPresent());
    }
}
