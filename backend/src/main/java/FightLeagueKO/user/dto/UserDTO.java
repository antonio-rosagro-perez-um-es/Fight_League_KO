package FightLeagueKO.user.dto;

import java.util.UUID;

import FightLeagueKO.user.enums.UserRole;

public record UserDTO(
    UUID id,
    String username,
    String email,
    UserRole role
) {
}
