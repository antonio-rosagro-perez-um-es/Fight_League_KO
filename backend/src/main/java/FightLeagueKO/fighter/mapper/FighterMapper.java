package FightLeagueKO.fighter.mapper;

import org.springframework.stereotype.Component;

import FightLeagueKO.fighter.dto.FighterStatsDTO;
import FightLeagueKO.fighter.model.Fighter;

@Component
public class FighterMapper {

    public FighterStatsDTO toFighterStatsDTO(Fighter fighter, double playRate) {

        return new FighterStatsDTO(
                fighter.getId(),
                fighter.getName(),
                fighter.getWinRate(),
                playRate,
                fighter.getPlayCounter(),
                fighter.getWinCounter(),
                fighter.getLoseCounter());
    }

}
