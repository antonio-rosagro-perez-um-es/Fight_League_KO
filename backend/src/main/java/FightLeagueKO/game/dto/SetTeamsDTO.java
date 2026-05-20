package FightLeagueKO.game.dto;

import FightLeagueKO.team.dto.CreateTeamDTO;

public record SetTeamsDTO(
    CreateTeamDTO team1,
    CreateTeamDTO team2
){}
