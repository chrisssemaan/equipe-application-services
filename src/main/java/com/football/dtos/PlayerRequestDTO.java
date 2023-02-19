package com.football.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.football.entity.enums.Position;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * The type Player request dto.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerRequestDTO
{
    @NotNull
    private String name;

    @NotNull
    private Position position;
}
