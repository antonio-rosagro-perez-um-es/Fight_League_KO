package FightLeagueKO.combo.dto;

import java.util.UUID;

public record OfficialComboDTO(
    UUID id,
    String title,
    String textNotation,
    String comboDificulty,
    String fuse,
    String mediaUrl,
    String description,
    Integer meterCost,
    Integer damage,
    UUID pointFighterId,
    String pointFighterName,
    String pointFighterSlug,
    UUID secondFighterId,
    String secondFighterName,
    String secondFighterSlug
) {
}

