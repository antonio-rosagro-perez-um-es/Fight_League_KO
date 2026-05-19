package FightLeagueKO.fighter.dto;

import java.util.UUID;

public record FighterBannerDTO(
        UUID id,
        String name,
        String slug) {
}