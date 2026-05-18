package FightLeagueKO.game.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateGameDTO(
    UUID user1,
    UUID user2,
    UUID team1,
    UUID team2,
    UUID winner,
    LocalDate gamedaDate
) {}
