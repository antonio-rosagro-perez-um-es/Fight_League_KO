package FightLeagueKO.game.dto;

import FightLeagueKO.team.dto.TeamDTO;

public record SetTeamsDTO(
    TeamDTO team1,
    TeamDTO team2
){}
