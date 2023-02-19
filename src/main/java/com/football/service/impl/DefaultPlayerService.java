package com.football.service.impl;

import com.football.dtos.PlayerRequestDTO;
import com.football.entity.Player;
import com.football.service.PlayerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultPlayerService implements PlayerService {

    private final ModelMapper modelMapper = new ModelMapper();

    public Player requestDtoToPlayer (PlayerRequestDTO playerRequestDTO) {
        return modelMapper.map(playerRequestDTO, Player.class);
    }

    public List<Player> requestDtosToPlayers (List<PlayerRequestDTO> playerRequestDTO) {
        return playerRequestDTO.stream().map(player -> requestDtoToPlayer(player)).collect(Collectors.toList());
    }
}
