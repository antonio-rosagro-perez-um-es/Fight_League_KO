package FightLeagueKO.tournament.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import FightLeagueKO.tournament.enums.TournamentStates;
import FightLeagueKO.tournament.model.Tournament;

@NoRepositoryBean
public interface TournamentRepository extends CrudRepository<Tournament, UUID> {

    public List<Tournament> getAllActiveTournaments();

    List<Tournament> findByTournamentStateAndInscriptionCloseDateBefore(TournamentStates state, LocalDate date);

    List<Tournament> findByTournamentStateAndStartDateLessThanEqual(TournamentStates state, LocalDate date);
}
