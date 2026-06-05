package FightLeagueKO.user.dto;

import java.util.UUID;

public record UserRankingDTO(
    UUID id,
    String username,
    int score,
    int tournamentWins
) {
}
