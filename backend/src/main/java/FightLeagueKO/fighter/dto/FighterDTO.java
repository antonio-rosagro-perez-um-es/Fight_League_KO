package FightLeagueKO.fighter.dto;

import java.util.UUID;

public record FighterDTO(
    UUID id,
    String name,
    String description,
    String region,
    String archetype,
    String title,
    String itLikes,
    String itDislike,
    String slug,
    boolean deleted,
    int health,
    int range,
    int power,
    int vitality,
    int mobility,
    int easyOfUse,
    int winCounter,
    int loseCounter,
    int playCounter,
    double winRate
) {
}
