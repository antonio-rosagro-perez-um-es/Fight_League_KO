package FightLeagueKO.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import FightLeagueKO.game.model.Game;

public interface GameRepositoryPostgre extends GameRepository{
    
    @Query("SELEC g FROM Game g WHERE g.deleted = false")
    public List<Game> getAllActiveGames();
}
