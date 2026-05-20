package FightLeagueKO.team.mapper;

import org.springframework.stereotype.Component;

import FightLeagueKO.team.dto.TeamStatsDTO;
import FightLeagueKO.team.model.Team;

@Component
public class TeamMapper {

    public TeamStatsDTO toTeamStatsDTO(Team team, Double pickRate){
        return new TeamStatsDTO(
            team.getWinRate(),
            pickRate,
            team.getPlayCounter(),
            team.getWinCounter(),
            team.getLoseCounter()
        );
    }
}
