package com.football.repository;

import com.football.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Team repository.
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long>  {

    Page<Team> findAll(final Pageable pageable);
}
