package FightLeagueKO.team.mapper;

import org.springframework.stereotype.Component;

import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.repository.FighterRepositoryPostgre;
import FightLeagueKO.team.dto.TeamStatsDTO;
import FightLeagueKO.team.model.Team;

@Component
public class TeamMapper {

    private final FighterRepositoryPostgre fighterRepository;

    public TeamMapper(FighterRepositoryPostgre fighterRepository) {
        this.fighterRepository = fighterRepository;
    }

    public TeamStatsDTO toTeamStatsDTO(Team team, Double pickRate){
        String pointFighterName = fighterRepository.findById(team.getPointFighterId())
                .map(Fighter::getName)
                .orElse(null);
        String secondFighterName = fighterRepository.findById(team.getSecondFighterId())
                .map(Fighter::getName)
                .orElse(null);

        return new TeamStatsDTO(
            team.getId(),
            pointFighterName,
            secondFighterName,
            team.getFuse(),
            team.getWinRate(),
            pickRate,
            team.getPlayCounter(),
            team.getWinCounter(),
            team.getLoseCounter()
        );
    }
}
