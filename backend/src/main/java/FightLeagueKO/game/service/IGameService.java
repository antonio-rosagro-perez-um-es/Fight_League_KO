package FightLeagueKO.game.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.game.dto.CreateGameDTO;
import FightLeagueKO.game.dto.UpdateGameDTO;
import FightLeagueKO.game.model.Game;
import FightLeagueKO.team.dto.TeamDTO;

public interface IGameService {

    public Game getGameById(UUID gameId);

    public List<Game> getAllGames();

    public List<Game> getAllActiveGames();

    public Game createGame(CreateGameDTO gameDTO);

    public void updateGame(UUID gameId, UpdateGameDTO game);

    public void softDeleteGame(UUID gameId);

    public void restoreGame(UUID gameId);

    public void setTeams(UUID gameId, TeamDTO teamUser1, TeamDTO teamUser2);

    public void setWinner(UUID gameId, UUID userId);
}
