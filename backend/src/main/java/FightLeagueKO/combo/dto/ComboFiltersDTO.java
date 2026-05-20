package FightLeagueKO.combo.dto;

import java.util.UUID;

public record ComboFiltersDTO(
    UUID pointFighterId,
    UUID secondFighterId,
    Boolean oficial,
    String comboDificulty,
    String fuse,
    Boolean latest
) {
}