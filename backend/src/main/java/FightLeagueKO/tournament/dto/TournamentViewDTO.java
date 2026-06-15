package FightLeagueKO.tournament.dto;

import java.time.LocalDate;
import java.util.UUID;

import FightLeagueKO.tournament.enums.TournamentStates;

public record TournamentViewDTO(
    UUID id,
    UUID ownerId,
    String ownerUsername,
    String title,
    TournamentStates state,
    int maxPlayers,
    int playerCount,
    int remainingSlots,
    LocalDate startDate,
    LocalDate inscriptionCloseDate,
    UUID winnerId,
    boolean deleted,
    boolean scored,
    boolean joinedByCurrentUser,
    boolean ownedByCurrentUser
) {
}
