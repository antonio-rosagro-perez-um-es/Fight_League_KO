package FightLeagueKO.tournament.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.tournament.dto.CreateTournamentDTO;
import FightLeagueKO.tournament.dto.TournamentGameDTO;
import FightLeagueKO.tournament.dto.TournamentStandingDTO;
import FightLeagueKO.tournament.dto.TournamentViewDTO;
import FightLeagueKO.tournament.dto.UpdateTournamentDTO;
import FightLeagueKO.tournament.model.Tournament;

public interface ITournamentService {

    Tournament getTournamentById(UUID tournamentId);

    List<Tournament> getAllTournament();

    List<Tournament> getAllActiveTournament();

    List<TournamentViewDTO> getAllTournamentViews(UUID currentUserId);

    List<TournamentViewDTO> getAllActiveTournamentViews(UUID currentUserId);

    List<TournamentViewDTO> getOwnedTournamentViews(UUID ownerId);

    TournamentViewDTO getTournamentView(UUID tournamentId, UUID currentUserId);

    List<TournamentGameDTO> getTournamentBracket(UUID tournamentId);

    List<TournamentStandingDTO> getTournamentStandings(UUID tournamentId);

    Tournament createTournament(CreateTournamentDTO tournamentDTO);

    void updateTournament(UUID tournamentId, UpdateTournamentDTO tournamentDTO);

    void softDeleteTournament(UUID tournamentId);

    void restoreTournament(UUID tournamentId);

    void joinTournament(UUID tournamentId);

    void exitTournament(UUID tournamentId);

    void closeRegistrations(UUID tournamentId);

    void generateMatchups(UUID tournamentId);
}
