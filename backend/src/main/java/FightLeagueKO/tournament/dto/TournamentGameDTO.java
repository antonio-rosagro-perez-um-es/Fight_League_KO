package FightLeagueKO.tournament.dto;

import java.time.LocalDate;
import java.util.UUID;

public record TournamentGameDTO(
    UUID id,
    int roundNumber,
    int bracketPosition,
    UUID user1Id,
    String user1Username,
    UUID user2Id,
    String user2Username,
    UUID teamUser1Id,
    UUID teamUser2Id,
    UUID winnerId,
    LocalDate gameDate
) {
}
