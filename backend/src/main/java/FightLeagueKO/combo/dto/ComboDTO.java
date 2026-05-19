package FightLeagueKO.combo.dto;

import java.util.UUID;

public record ComboDTO(
    UUID id,
    String title,
    String textNotation,
    String comboDificulty,
    String fuse,
    String mediaUrl,
    String description,
    Integer meterCost,
    Integer damage
) {
}