package FightLeagueKO.team.dto;

import java.util.UUID;

import FightLeagueKO.combo.enums.FuseType;

public record UpdateTeamDTO(
        UUID pointFighterId,
        UUID secondFighterId,
        FuseType fuse) {
}
