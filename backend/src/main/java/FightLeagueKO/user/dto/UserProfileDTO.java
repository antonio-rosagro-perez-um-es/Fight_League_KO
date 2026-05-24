package FightLeagueKO.user.dto;

import java.util.UUID;

import FightLeagueKO.user.enums.UserRole;

public record UserProfileDTO(
    UUID id,
    String username,
    String email,
    UserRole role,
    int score,
    int tournamentWins,
    int gamesPlayed,
    int gamesWon,
    int gamesLost
) {
}
