package FightLeagueKO.game.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import FightLeagueKO.game.model.Game;

@NoRepositoryBean
public interface GameRepository extends CrudRepository<Game, UUID>{

    public List<Game> getAllActiveGames();

}
