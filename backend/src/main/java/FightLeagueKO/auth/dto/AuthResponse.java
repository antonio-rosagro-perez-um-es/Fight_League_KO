package FightLeagueKO.auth.dto;

import FightLeagueKO.user.dto.UserDTO;

public record AuthResponse(
    String token,
    UserDTO user
) {
}
