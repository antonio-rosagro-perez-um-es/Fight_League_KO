package FightLeagueKO.tournament.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.tournament.dto.CreateTournamentDTO;
import FightLeagueKO.tournament.dto.UpdateTournamentDTO;
import FightLeagueKO.tournament.model.Tournament;

public interface ITournamentService {
    
    public Tournament getTournamentById(UUID tournamentId);

    public List<Tournament> getAllTournament();

    public List<Tournament> getAllActiveTournament();

    public Tournament createTournament(CreateTournamentDTO tournamentDTO);

    public void updateTournament(UUID tournamentId, UpdateTournamentDTO tournamentDTO);

    public void softDeleteTournament(UUID tournamentId);

    public void restoreTournament(UUID tournamentId);

    public void joinTournament(UUID tournamentId, UUID userId);

    public void exitTournament(UUID tournamentId ,UUID userId);

    public void closeRegistrations(UUID tournamentId);
}
