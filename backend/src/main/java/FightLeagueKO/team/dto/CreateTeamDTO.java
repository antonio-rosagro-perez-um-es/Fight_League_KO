package FightLeagueKO.team.dto;

import java.util.UUID;

import FightLeagueKO.combo.enums.FuseType;

public record CreateTeamDTO(
        UUID pointFighterId,
        UUID secondFighterId,
        FuseType fuse) {
}
