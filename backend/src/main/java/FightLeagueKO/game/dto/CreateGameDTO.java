package FightLeagueKO.game.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CreateGameDTO(
    UUID user1,
    UUID user2,
    LocalDate gameDate
) {}
