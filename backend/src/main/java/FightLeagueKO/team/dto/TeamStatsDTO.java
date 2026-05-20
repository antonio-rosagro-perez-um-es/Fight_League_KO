package FightLeagueKO.team.dto;

public record TeamStatsDTO(
    double winRate,
    double pickRate,
    int pickCounter,
    int winsCounter,
    int losesCounter
) {

}
