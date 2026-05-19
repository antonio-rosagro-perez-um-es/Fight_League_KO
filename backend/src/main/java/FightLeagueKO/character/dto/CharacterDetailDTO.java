package FightLeagueKO.character.dto;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.combo.dto.ComboDTO;

public record CharacterDetailDTO(
    UUID id,
    String name,
    String description,
    String region,
    String archetype,
    String title,
    String itLikes,
    String itDislike,
    String slug,
    int health,
    int range,
    int power,
    int vitality,
    int mobility,
    int easyOfUse,
    List<ComboDTO> officialCombos
) {
}