package FightLeagueKO.tournament.dto;

import java.util.UUID;

public record TournamentStandingDTO(
    UUID userId,
    String username,
    int placement,
    int points
) {
}
