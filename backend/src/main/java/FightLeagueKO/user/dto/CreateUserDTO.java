package FightLeagueKO.user.dto;

public record CreateUserDTO(
    String username,
    String email,
    String password
) {
}
