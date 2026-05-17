package FightLeagueKO.tournament.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import FightLeagueKO.tournament.enums.TournamentStates;
import FightLeagueKO.tournament.model.Tournament;

public interface TournamentRepositoryPostgre extends TournamentRepository {

    @Query("SELECT t FROM Tournament t WHERE t.deleted = false")
    public List<Tournament> getAllActiveTournaments();

    List<Tournament> findByTournamentStateAndInscriptionCloseDateBefore(TournamentStates state, LocalDate date);

    List<Tournament> findByTournamentStateAndStartDateLessThanEqual(TournamentStates state, LocalDate date);
}
