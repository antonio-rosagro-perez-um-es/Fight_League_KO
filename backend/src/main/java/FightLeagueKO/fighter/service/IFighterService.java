package FightLeagueKO.fighter.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.fighter.dto.CreateFighterDTO;
import FightLeagueKO.fighter.dto.FighterBannerDTO;
import FightLeagueKO.fighter.dto.FighterDTO;
import FightLeagueKO.fighter.dto.FighterStatsDTO;
import FightLeagueKO.fighter.dto.FighterUpdateDTO;
import FightLeagueKO.fighter.model.Fighter;

public interface IFighterService {

    Fighter getFighterById(UUID fighterId);

    List<FighterDTO> getAllFighters();

    List<FighterBannerDTO> getAllFightersBanner();

    FighterDTO getFighterDTOById(UUID fighterId);

    FighterDTO createFighter(CreateFighterDTO fighterDTO);

    void updateFighter (UUID fighterId, FighterUpdateDTO fighterDTO);

    void softDeleteFighter (UUID fighterId);

    void restoreFighter (UUID fighterId);

    void updateFighterStats(UUID fighterId, boolean isWinner);

    FighterStatsDTO getFighterStats(UUID fighterId);

    List<FighterStatsDTO> getFightersRanking();
}