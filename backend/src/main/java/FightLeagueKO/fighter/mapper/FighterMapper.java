package FightLeagueKO.fighter.mapper;

import org.springframework.stereotype.Component;

import FightLeagueKO.fighter.dto.FighterDTO;
import FightLeagueKO.fighter.dto.FighterStatsDTO;
import FightLeagueKO.fighter.model.Fighter;

@Component
public class FighterMapper {

    public FighterDTO toFighterDTO(Fighter fighter) {
        return new FighterDTO(
                fighter.getId(),
                fighter.getName(),
                fighter.getDescription(),
                fighter.getRegion(),
                fighter.getArchetype(),
                fighter.getTitle(),
                fighter.getItLikes(),
                fighter.getItDislike(),
                fighter.getSlug(),
                fighter.isDeleted(),
                fighter.getHealth(),
                fighter.getRange(),
                fighter.getPower(),
                fighter.getVitality(),
                fighter.getMobility(),
                fighter.getEasyOfUse(),
                fighter.getWinCounter(),
                fighter.getLoseCounter(),
                fighter.getPlayCounter(),
                fighter.getWinRate());
    }

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
