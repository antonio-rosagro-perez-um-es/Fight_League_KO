package FightLeagueKO.fighter.service;

import java.util.List;
import java.util.UUID;
import FightLeagueKO.fighter.model.Fighter;

import FightLeagueKO.fighter.dto.FighterBannerDTO;
import FightLeagueKO.fighter.dto.FighterDetailDTO;
import FightLeagueKO.fighter.dto.FighterStatsDTO;
import FightLeagueKO.fighter.dto.FighterUpdateDTO;
import FightLeagueKO.fighter.dto.CreateFighterDTO;

public interface IFighterService {

    List<Fighter> getAllFighters();

    List<FighterBannerDTO> getAllFightersBanner();

    Fighter getFighterById(UUID fighterId);

    FighterDetailDTO getFighterWithOfficialCombos(UUID fighterId);

    Fighter createFighter(CreateFighterDTO fighterDTO);

    void updateFighter (UUID fighterId, FighterUpdateDTO fighterDTO);

    void softDeleteFighter (UUID fighterId);

    void restoreFighter (UUID fighterId);

    void updateFighterStats(UUID fighterId, boolean isWinner);

    FighterStatsDTO getFighterStats(UUID fighterId);
}