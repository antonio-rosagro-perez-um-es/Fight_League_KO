package FightLeagueKO.character.dto;

import java.util.UUID;

public record CharacterBannerDTO(
        UUID id,
        String name,
        String slug) {
}