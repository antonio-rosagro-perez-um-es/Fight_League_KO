package FightLeagueKO.tournament.dto;

import java.time.LocalDate;

public record UpdateTournamentDTO(

    int maxPlayers,
    LocalDate startDate,
    LocalDate inscriptionCloseDate
) {}
