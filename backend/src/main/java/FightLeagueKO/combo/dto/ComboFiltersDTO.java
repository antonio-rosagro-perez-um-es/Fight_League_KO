package FightLeagueKO.combo.dto;

import java.util.UUID;

public record ComboFiltersDTO(
    UUID pointFighterId,
    UUID secondFighterId,
    String comboDificulty,
    String fuse,
    Boolean latest,
    Boolean mostLiked
) {
}