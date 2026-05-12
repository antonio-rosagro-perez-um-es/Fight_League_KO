package FightLeagueKO.combo.dto;

import java.util.UUID;

public record ComboFiltersDTO(
    UUID characterPointId,
    UUID characterSecondId,
    Boolean oficial,
    String comboDificulty,
    String fuse,
    Boolean latest
) {
}