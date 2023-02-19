package com.football.service;

import com.football.dtos.PlayerRequestDTO;
import com.football.entity.Player;

import java.util.List;

public interface PlayerService {
    public Player requestDtoToPlayer (PlayerRequestDTO playerRequestDTO);

    public List<Player> requestDtosToPlayers (List<PlayerRequestDTO> playerRequestDTO);
}
