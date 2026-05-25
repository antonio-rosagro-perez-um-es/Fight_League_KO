package FightLeagueKO.user.dto;

import java.util.UUID;

import FightLeagueKO.user.enums.UserRole;

public record AdminUserDTO(
    UUID id,
    String username,
    String email,
    UserRole role,
    boolean deleted,
    int score,
    int tournamentWins
) {
}
