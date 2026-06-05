package FightLeagueKO.tournament.dto;

import java.time.LocalDate;

public record CreateTournamentDTO(
    String title,
    int maxPlayers,
    LocalDate starDate,
    LocalDate inscriptionCloseDate
) {}
