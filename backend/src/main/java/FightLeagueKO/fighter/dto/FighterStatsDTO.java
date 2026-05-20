package FightLeagueKO.fighter.dto;

public record FighterStatsDTO(

    double winRate,
    double pickRate,
    int pickCounter,
    int winsCounter,
    int losesCounter
) {}
