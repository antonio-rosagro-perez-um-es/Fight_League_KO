package FightLeagueKO.team.dto;

import java.util.UUID;

import FightLeagueKO.combo.enums.FuseType;

public record TeamDTO(
    UUID pointCharacterId,
    UUID secondCharacterId,
    FuseType fuse
) {

}
