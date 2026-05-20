package FightLeagueKO.fighter.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import FightLeagueKO.combo.dto.ComboDTO;
import FightLeagueKO.fighter.dto.FighterDetailDTO;
import FightLeagueKO.fighter.dto.FighterStatsDTO;
import FightLeagueKO.fighter.model.Fighter;

@Component
public class FighterMapper {

    public FighterStatsDTO toFighterStatsDTO(Fighter fighter, double playRate) {

        return new FighterStatsDTO(
                fighter.getWinRate(),
                playRate,
                fighter.getPlayCounter(),
                fighter.getWinCounter(),
                fighter.getLoseCounter());
    }

    public FighterDetailDTO toFighterDetailDTO(Fighter fighter, List<ComboDTO> comboDTOs) {
        return new FighterDetailDTO(
                fighter.getId(),
                fighter.getName(),
                fighter.getDescription(),
                fighter.getRegion(),
                fighter.getArchetype(),
                fighter.getTitle(),
                fighter.getItLikes(),
                fighter.getItDislike(),
                fighter.getSlug(),
                fighter.getHealth(),
                fighter.getRange(),
                fighter.getPower(),
                fighter.getVitality(),
                fighter.getMobility(),
                fighter.getEasyOfUse(),
                comboDTOs);
    }

}
