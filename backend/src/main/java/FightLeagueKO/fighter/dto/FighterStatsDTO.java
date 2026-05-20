package FightLeagueKO.fighter.dto;

import java.util.UUID;

public record FighterStatsDTO(
    UUID fighterId,
    String fighterName,
    double winRate,
    double playRate,
    int pickCounter,
    int winsCounter,
    int losesCounter
) {}
