package FightLeagueKO.combo.dto;

import java.util.UUID;

import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;

public record ComboUpdateDTO(
    UUID pointCharacter,
    UUID secondCharacter,
    String textNotation,
    ComboDificulty comboDificulty,
    FuseType fuse,
    String mediaUrl,
    String description,
    Integer metercost,
    Integer damage
) {

}
