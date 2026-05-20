package FightLeagueKO.team.dto;

import java.util.UUID;

import FightLeagueKO.combo.enums.FuseType;

public record TeamDTO(
    UUID pointFighterId,
    UUID secondFighterId,
    FuseType fuse
) {

}
