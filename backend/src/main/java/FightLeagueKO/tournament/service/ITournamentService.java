package FightLeagueKO.tournament.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.tournament.model.Tournament;

public interface ITournamentService {
    
    public Tournament getTournamentById(UUID tournamentId);

    public List<Tournament> getAllTournament(UUID tournamentId);

    public List<Tournament> getAllActiveTournament();

    public Tournament createTournament(CreateTournamentDTO tournamentDTO);

    public Tournament updateTournament(UpdateTorunamentDTO tournamentDTO);

    public void softDeleteTournament(UUID tournamentId);

    public void restoreTournament(UUID tournamentId);

    public 

}
