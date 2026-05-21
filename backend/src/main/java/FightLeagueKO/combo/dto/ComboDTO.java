package FightLeagueKO.combo.dto;

import java.time.LocalDate;
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
    Integer damage,
    boolean oficial,
    boolean privateCombo,
    boolean deleted,
    LocalDate createdAt,
    LocalDate upDateAt,
    int likeCounter,
    int dislikeCounter,
    UUID pointFighterId,
    String pointFighterName,
    String pointFighterSlug,
    UUID secondFighterId,
    String secondFighterName,
    String secondFighterSlug
) {
}