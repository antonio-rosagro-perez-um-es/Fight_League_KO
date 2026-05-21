package FightLeagueKO.game.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import FightLeagueKO.game.dto.CreateGameDTO;
import FightLeagueKO.game.dto.UpdateGameDTO;
import FightLeagueKO.game.model.Game;
import FightLeagueKO.game.repository.GameRepository;
import FightLeagueKO.team.dto.CreateTeamDTO;
import FightLeagueKO.team.model.Team;
import FightLeagueKO.team.service.TeamService;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.service.UserService;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class GameService implements IGameService {

    private GameRepository gameRepository;
    private UserService userService;
    private TeamService teamService;

    @Autowired
    public GameService(GameRepository gameRepository, UserService userService, TeamService teamService) {
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.teamService = teamService;
    }

    @Override
    public Game getGameById(UUID gameId) {

        Objects.requireNonNull(gameId, "Parameter id for game could not be null");

        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id " + gameId));
    }

    @Override
    public List<Game> getAllGames() {

        return StreamSupport.stream(gameRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> getAllActiveGames() {
        return gameRepository.getAllActiveGames();
    }

    @Override
    public Game createGame(CreateGameDTO gameDTO) {

        Objects.requireNonNull(gameDTO, "Parameter gameDTO coul not be empty");

        Game game = new Game();

        if (gameDTO.user1() == null || gameDTO.user2() == null)
            throw new IllegalArgumentException("User id could not be null or empty");

        if (gameDTO.gameDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Invalid game date");

        User user1 = userService.getUserById(gameDTO.user1());
        User user2 = userService.getUserById(gameDTO.user2());

        game.setUser1(user1);
        game.setUser2(user2);
        game.setGameDate(gameDTO.gameDate());
        game.setDelete(false);

        return gameRepository.save(game);
    }

    @Override
    public void updateGame(UUID gameId, UpdateGameDTO gameDTO) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id " + gameId));

        Optional.ofNullable(gameDTO.user1())
                .map(userService::getUserById)
                .ifPresent(game::setUser1);

        Optional.ofNullable(gameDTO.user2())
                .map(userService::getUserById)
                .ifPresent(game::setUser2);

        Optional.ofNullable(gameDTO.team1())
                .map(teamService::getTeamById)
                .ifPresent(game::setTeamUser1);

        Optional.ofNullable(gameDTO.team2())
                .map(teamService::getTeamById)
                .ifPresent(game::setTeamUser2);

        Optional.ofNullable(gameDTO.winner())
                .map(userService::getUserById)
                .ifPresent(game::setWinner);

        gameRepository.save(game);

    }

    @Override
    public void softDeleteGame(UUID gameId) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id " + gameId));

        game.setDelete(true);

        gameRepository.save(game);
    }

    @Override
    public void restoreGame(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id " + gameId));

        game.setDelete(false);

        gameRepository.save(game);
    }

    @Override
    public void setTeams(UUID gameId, CreateTeamDTO teamUser1, CreateTeamDTO teamUser2) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id " + gameId));

        Team team1 = teamService.createTeam(teamUser1);
        Team team2 = teamService.createTeam(teamUser2);
        game.setTeamUser1(team1);
        game.setTeamUser2(team2);

        gameRepository.save(game);
    }

    @Override
    public void setWinner(UUID gameId, UUID userId) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id " + gameId));

        User winnerUser = userService.getUserById(userId);

        if (winnerUser.getId() == game.getUser1().getId()) {
            teamService.updateTeamStats(game.getTeamUser1().getId(), true);
            teamService.updateTeamStats(game.getTeamUser2().getId(), false);
        }

        if (winnerUser.getId() == game.getUser2().getId()) {
            teamService.updateTeamStats(game.getTeamUser1().getId(), false);
            teamService.updateTeamStats(game.getTeamUser2().getId(), true);
        }

        game.setWinner(winnerUser);

        gameRepository.save(game);
    }

}
