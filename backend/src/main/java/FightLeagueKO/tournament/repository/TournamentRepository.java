package FightLeagueKO.tournament.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import FightLeagueKO.tournament.model.Tournament;

@NoRepositoryBean
public interface TournamentRepository extends CrudRepository<Tournament, UUID>{
    
}
