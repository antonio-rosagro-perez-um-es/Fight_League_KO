package FightLeagueKO.game.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;

import FightLeagueKO.game.model.Game;

public interface GameRepositoryPostgre extends GameRepository{
    
    @Query("SELECT g FROM Game g WHERE g.deleted = false")
    public List<Game> getAllActiveGames();

    @Override
    @Query("SELECT g FROM Game g WHERE g.deleted = false AND g.tournament.id = :tournamentId ORDER BY g.roundNumber ASC, g.bracketPosition ASC")
    public List<Game> getTournamentGames(UUID tournamentId);

    @Override
    @Query("SELECT g FROM Game g WHERE g.deleted = false AND (g.user1Id = :userId OR g.user2Id = :userId) ORDER BY g.gameDate DESC")
    public List<Game> getRecentGamesByUser(UUID userId);
}
