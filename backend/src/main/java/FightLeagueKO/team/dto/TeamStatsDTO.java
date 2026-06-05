package FightLeagueKO.team.dto;

import java.util.UUID;

import FightLeagueKO.combo.enums.FuseType;

public record TeamStatsDTO(
    UUID idTeam,
    String pointFighterName,
    String secondFighterName,
    FuseType fuse,
    double winRate,
    double playRate,
    int pickCounter,
    int winsCounter,
    int losesCounter
) {

}
