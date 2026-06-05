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
import FightLeagueKO.security.CurrentUserService;
import FightLeagueKO.team.dto.CreateTeamDTO;
import FightLeagueKO.team.service.TeamService;
import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.model.User;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class GameService implements IGameService {

    private GameRepository gameRepository;
    private TeamService teamService;
    private CurrentUserService currentUserService;

    @Autowired
    public GameService(GameRepository gameRepository, TeamService teamService, CurrentUserService currentUserService) {
        this.gameRepository = gameRepository;
        this.teamService = teamService;
        this.currentUserService = currentUserService;
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
        game.setRoundNumber(0);
        game.setBracketPosition(0);
        game.setDelete(false);

        return gameRepository.save(game);
    }

    @Override
    public Game createTournamentGame(Tournament tournament, UUID user1Id, UUID user2Id, int roundNumber, int bracketPosition) {
        Objects.requireNonNull(tournament, "Tournament could not be null");
        Objects.requireNonNull(user1Id, "User 1 id could not be null");
        Objects.requireNonNull(user2Id, "User 2 id could not be null");

        Game game = new Game();
        game.setTournament(tournament);
        game.setUser1Id(user1Id);
        game.setUser2Id(user2Id);
        game.setGameDate(LocalDate.now().isBefore(tournament.getStartDate()) ? tournament.getStartDate() : LocalDate.now());
        game.setRoundNumber(roundNumber);
        game.setBracketPosition(bracketPosition);
        game.setDelete(false);

        return gameRepository.save(game);
    }

    @Override
    public List<Game> getTournamentGames(UUID tournamentId) {
        Objects.requireNonNull(tournamentId, "Tournament id could not be null");
        return gameRepository.getTournamentGames(tournamentId);
    }

    @Override
    public List<Game> getRecentGamesByUser(UUID userId) {
        Objects.requireNonNull(userId, "User id could not be null");
        return gameRepository.getRecentGamesByUser(userId).stream()
                .limit(10)
                .collect(Collectors.toList());
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

        assertTournamentOwnerOrAdmin(game);

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

        assertTournamentOwnerOrAdmin(game);

        if (!userId.equals(game.getUser1Id()) && !userId.equals(game.getUser2Id())) {
            throw new IllegalArgumentException("Winner must be one of the game players");
        }

        if (userId.equals(game.getWinnerId())) {
            return;
        }

        if (game.getWinnerId() != null) {
            applyTeamStats(game, game.getWinnerId(), true);
        }

        applyTeamStats(game, userId, false);

        game.setWinnerId(userId);

        gameRepository.save(game);
    }

    private void applyTeamStats(Game game, UUID winnerId, boolean revert) {
        boolean user1Won = winnerId.equals(game.getUser1Id());
        updateTeamStatsIfPresent(game.getTeamUser1Id(), user1Won, revert);
        updateTeamStatsIfPresent(game.getTeamUser2Id(), !user1Won, revert);
    }

    private void updateTeamStatsIfPresent(UUID teamId, boolean isWinner, boolean revert) {
        if (teamId != null) {
            if (revert) {
                teamService.revertTeamStats(teamId, isWinner);
            } else {
                teamService.updateTeamStats(teamId, isWinner);
            }
        }
    }

    private void assertTournamentOwnerOrAdmin(Game game) {
        if (game.getTournament() == null) {
            return;
        }

        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        if (!game.getTournament().getUserOwnerId().equals(currentUser.getId())) {
            throw new SecurityException("Only the tournament owner or an admin can set game results");
        }
    }

}
