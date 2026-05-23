package FightLeagueKO.tournament.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.tournament.dto.CreateTournamentDTO;
import FightLeagueKO.tournament.dto.UpdateTournamentDTO;
import FightLeagueKO.tournament.model.Tournament;

public interface ITournamentService {

    Tournament getTournamentById(UUID tournamentId);

    List<Tournament> getAllTournament();

    List<Tournament> getAllActiveTournament();

    Tournament createTournament(CreateTournamentDTO tournamentDTO);

    void updateTournament(UUID tournamentId, UpdateTournamentDTO tournamentDTO);

    void softDeleteTournament(UUID tournamentId);

    void restoreTournament(UUID tournamentId);

    void joinTournament(UUID tournamentId);

    void exitTournament(UUID tournamentId);

    void closeRegistrations(UUID tournamentId);

    void generateMatchups(UUID tournamentId);
}
