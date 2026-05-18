package FightLeagueKO.tournament.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTournamentDTO(
    UUID userOwner,
    String title,
    int maxPlayers,
    LocalDate starDate,
    LocalDate inscriptionCloseDate
) {}
