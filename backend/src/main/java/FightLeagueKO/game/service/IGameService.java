package FightLeagueKO.game.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.game.dto.CreateGameDTO;
import FightLeagueKO.game.dto.UpdateGameDTO;
import FightLeagueKO.game.model.Game;
import FightLeagueKO.team.dto.CreateTeamDTO;

public interface IGameService {

    Game getGameById(UUID gameId);

    List<Game> getAllGames();

    List<Game> getAllActiveGames();

    Game createGame(CreateGameDTO gameDTO);

    void updateGame(UUID gameId, UpdateGameDTO game);

    void softDeleteGame(UUID gameId);

    void restoreGame(UUID gameId);

    void setTeams(UUID gameId, CreateTeamDTO teamUser1, CreateTeamDTO teamUser2);

    void setWinner(UUID gameId, UUID userId);
}
