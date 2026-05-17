package FightLeagueKO.tournament.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import FightLeagueKO.tournament.model.Tournament;

public interface TournamentRepositoryPostgre extends TournamentRepository{

    @Query("SELECT t FROM Tournament t WHERE t.deleted = false")
    public List<Tournament> getAllActiveTournaments();
}
