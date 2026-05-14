package FightLeagueKO.team.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import FightLeagueKO.team.model.Team;

@NoRepositoryBean
public interface TeamRepository extends CrudRepository<Team, UUID> {
    
    List<Team> getAllActiveTeams();

    Long getAllTeamsPlayRate();
}
