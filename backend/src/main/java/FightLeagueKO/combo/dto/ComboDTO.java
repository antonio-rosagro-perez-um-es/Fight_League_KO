package FightLeagueKO.combo.dto;

import java.util.Date;
import java.util.UUID;

import FightLeagueKO.combo.enums.FuseType;

public record ComboDTO(
    
    UUID id,
    Character poinCharacter,
    Character seconCharacter,
    String textNotation,
    String comboDificulty,
    FuseType fuse,
    String imageUrl,
    String description,
    Date createdAt, 
    Date updateAt,
    int meterCost,
    int damage
) {
} 
