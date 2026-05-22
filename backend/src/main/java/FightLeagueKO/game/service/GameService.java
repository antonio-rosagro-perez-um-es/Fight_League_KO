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
import FightLeagueKO.team.service.TeamService;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class GameService implements IGameService {

    private GameRepository gameRepository;
    private TeamService teamService;

    @Autowired
    public GameService(GameRepository gameRepository, TeamService teamService) {
        this.gameRepository = gameRepository;
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

        game.setUser1Id(gameDTO.user1());
        game.setUser2Id(gameDTO.user2());
        game.setGameDate(gameDTO.gameDate());
        game.setDelete(false);

        return gameRepository.save(game);
    }

    @Override
    public void updateGame(UUID gameId, UpdateGameDTO gameDTO) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id " + gameId));

        Optional.ofNullable(gameDTO.user1())
                .ifPresent(game::setUser1Id);

        Optional.ofNullable(gameDTO.user2())
                .ifPresent(game::setUser2Id);

        Optional.ofNullable(gameDTO.team1())
                .ifPresent(game::setTeamUser1Id);

        Optional.ofNullable(gameDTO.team2())
                .ifPresent(game::setTeamUser2Id);

        Optional.ofNullable(gameDTO.winner())
                .ifPresent(game::setWinnerId);

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

        UUID team1Id = teamService.createTeam(teamUser1).getId();
        UUID team2Id = teamService.createTeam(teamUser2).getId();
        game.setTeamUser1Id(team1Id);
        game.setTeamUser2Id(team2Id);

        gameRepository.save(game);
    }

    @Override
    public void setWinner(UUID gameId, UUID userId) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id " + gameId));

        if (userId.equals(game.getUser1Id())) {
            teamService.updateTeamStats(game.getTeamUser1Id(), true);
            teamService.updateTeamStats(game.getTeamUser2Id(), false);
        }

        if (userId.equals(game.getUser2Id())) {
            teamService.updateTeamStats(game.getTeamUser1Id(), false);
            teamService.updateTeamStats(game.getTeamUser2Id(), true);
        }

        game.setWinnerId(userId);

        gameRepository.save(game);
    }

}
