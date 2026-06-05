package FightLeagueKO.tournament.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import FightLeagueKO.game.model.Game;
import FightLeagueKO.game.service.GameService;
import FightLeagueKO.security.CurrentUserService;
import FightLeagueKO.tournament.dto.CreateTournamentDTO;
import FightLeagueKO.tournament.dto.TournamentGameDTO;
import FightLeagueKO.tournament.dto.TournamentStandingDTO;
import FightLeagueKO.tournament.dto.TournamentViewDTO;
import FightLeagueKO.tournament.dto.UpdateTournamentDTO;
import FightLeagueKO.tournament.enums.TournamentStates;
import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.tournament.repository.TournamentRepository;
import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TournamentService implements ITournamentService {

    private TournamentRepository tournamentRepository;
    private GameService gameService;
    private CurrentUserService currentUserService;
    private UserService userService;

    public TournamentService(TournamentRepository tournamentRepository, GameService gameService,
            CurrentUserService currentUserService, UserService userService) {
        this.tournamentRepository = tournamentRepository;
        this.gameService = gameService;
        this.currentUserService = currentUserService;
        this.userService = userService;
    }

    public Tournament getTournamentById(@PathVariable UUID tournamentId) {
        Objects.requireNonNull(tournamentId);

        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found with id: " + tournamentId));
    }

    @Override
    public List<Tournament> getAllTournament() {

        return StreamSupport.stream(tournamentRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tournament> getAllActiveTournament() {

        return tournamentRepository.getAllActiveTournaments();
    }

    @Override
    public List<TournamentViewDTO> getAllTournamentViews(UUID currentUserId) {
        return getAllTournament().stream()
                .map(tournament -> toTournamentViewDTO(tournament, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public List<TournamentViewDTO> getAllActiveTournamentViews(UUID currentUserId) {
        return tournamentRepository.getAllActiveTournaments().stream()
                .map(tournament -> toTournamentViewDTO(tournament, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public List<TournamentViewDTO> getOwnedTournamentViews(UUID ownerId) {
        Objects.requireNonNull(ownerId, "Owner id could not be null");
        return tournamentRepository.getAllActiveTournaments().stream()
                .filter(tournament -> ownerId.equals(tournament.getUserOwnerId()))
                .map(tournament -> toTournamentViewDTO(tournament, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public TournamentViewDTO getTournamentView(UUID tournamentId, UUID currentUserId) {
        return toTournamentViewDTO(getTournamentById(tournamentId), currentUserId);
    }

    @Override
    public List<TournamentGameDTO> getTournamentBracket(UUID tournamentId) {
        getTournamentById(tournamentId);
        return gameService.getTournamentGames(tournamentId).stream()
                .map(this::toTournamentGameDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TournamentStandingDTO> getTournamentStandings(UUID tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        Map<UUID, Integer> placements = calculatePlacements(tournament, gameService.getTournamentGames(tournamentId));

        return placements.entrySet().stream()
                .map(entry -> new TournamentStandingDTO(
                        entry.getKey(),
                        userService.findUserEntityById(entry.getKey()).getUsername(),
                        entry.getValue(),
                        entry.getValue() <= 10 ? 11 - entry.getValue() : 0))
                .collect(Collectors.toList());
    }

    @Override
    public Tournament createTournament(CreateTournamentDTO tournamentDTO) {

        Objects.requireNonNull(tournamentDTO, "TournamentDTO could not be null");
        User currentUser = currentUserService.getCurrentUser();

        Tournament tournament = new Tournament();

        if (tournamentDTO.title() == null || tournamentDTO.title().isEmpty())
            throw new IllegalArgumentException("Tournament title cant be empty or null");

        if (tournamentDTO.starDate().isBefore(tournamentDTO.inscriptionCloseDate()))
            throw new IllegalArgumentException("Tournament start date cant be before close inscription date");

        if (tournamentDTO.starDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Tournament start date cant be befor now");

        if (tournamentDTO.inscriptionCloseDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Tournament inscriptions close date cant be befor now");

        tournament.setUserOwnerId(currentUser.getId());
        tournament.setTitle(tournamentDTO.title());
        tournament.setTournamentState(TournamentStates.REGISTRATION);
        tournament.setMaxPlayers(tournamentDTO.maxPlayers());
        tournament.setPlayersIds(new ArrayList<>());
        tournament.setGamesList(new ArrayList<Game>());
        tournament.setStartDate(tournamentDTO.starDate());
        tournament.setInscriptionCloseDate(tournamentDTO.inscriptionCloseDate());
        tournament.setManualClose(false);
        tournament.setWinnerId(null);
        tournament.setScored(false);
        tournament.setScoredAt(null);
        tournament.setDeleted(false);

        Tournament saved = tournamentRepository.save(tournament);

        if (currentUser.getRole() == UserRole.REGISTERED) {
            userService.updateRole(currentUser.getId(), UserRole.ORGANIZER);
        }

        return saved;
    }

    @Override
    public void updateTournament(UUID tournamentId, UpdateTournamentDTO tournamentDTO) {

        Objects.requireNonNull(tournamentDTO, "Parameter could not be null");

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + tournamentId));

        assertOwnerOrAdmin(tournament);

        if (tournament.getTournamentState() != TournamentStates.REGISTRATION)
            throw new IllegalStateException("Cant modify a tournament after registration is closed");

        Optional.ofNullable(tournamentDTO.maxPlayers())
                .ifPresent(tournament::setMaxPlayers);

        Optional.ofNullable(tournamentDTO.inscriptionCloseDate())
                .filter(date -> !date.isBefore(LocalDate.now()))
                .ifPresent(tournament::setInscriptionCloseDate);

        Optional.ofNullable(tournamentDTO.startDate())
                .filter(date -> !date.isBefore(LocalDate.now()))
                .filter(date -> date.isBefore(tournament.getInscriptionCloseDate()))
                .ifPresent(tournament::setStartDate);

        tournamentRepository.save(tournament);
    }

    @Override
    public void softDeleteTournament(UUID tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id:" + tournamentId));

        assertOwnerOrAdmin(tournament);

        tournament.setDeleted(true);

        tournamentRepository.save(tournament);
    }

    @Override
    public void restoreTournament(UUID tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id:" + tournamentId));

        assertAdmin();

        tournament.setDeleted(false);

        tournamentRepository.save(tournament);
    }

    @Override
    public void joinTournament(UUID tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id" + tournamentId));
        UUID userId = currentUserService.getCurrentUserId();

        if (tournament.getUserOwnerId().equals(userId)) {
            throw new IllegalStateException("Organizer cannot join their own tournament");
        }

        if (!tournament.getTournamentState().equals(TournamentStates.REGISTRATION))
            throw new IllegalStateException("Registration period has ended");

        if (tournament.getPlayersIds().size() < tournament.getMaxPlayers()) {

            tournament.addPlayer(userId);
        }

        if (tournament.getPlayersIds().size() >= tournament.getMaxPlayers())
            tournament.setTournamentState(TournamentStates.WAITING_START);

    }

    @Override
    public void exitTournament(UUID tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id" + tournamentId));
        UUID userId = currentUserService.getCurrentUserId();

        if (!tournament.getTournamentState().equals(TournamentStates.REGISTRATION))
            throw new IllegalStateException("Can't leave a tournamen when registration is closed");

        tournament.removePlayer(userId);

        if (!tournament.isManualClose())
            tournament.setTournamentState(TournamentStates.REGISTRATION);
    }

    @Override
    public void closeRegistrations(UUID tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id" + tournamentId));

        assertOwnerOrAdmin(tournament);

        if (!tournament.getTournamentState().equals(TournamentStates.REGISTRATION))
            throw new IllegalStateException("Tournament already closed");

        tournament.setTournamentState(TournamentStates.WAITING_START);

        tournament.setManualClose(true);
    }

    @Override
    public void generateMatchups(UUID tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);

        assertOwnerOrAdmin(tournament);

        List<Game> allGames = gameService.getTournamentGames(tournamentId);
        if (allGames.isEmpty()) {
            generateFirstRound(tournament);
        } else {
            generateNextRoundOrFinish(tournament, allGames);
        }
        tournamentRepository.save(tournament);
    }

    private void generateFirstRound(Tournament tournament) {
        if (tournament.getTournamentState() != TournamentStates.WAITING_START) {
            throw new IllegalStateException("Tournament registrations must be closed before generating matchups");
        }

        List<UUID> playerIds = new ArrayList<>(tournament.getPlayersIds());
        if (playerIds.size() < 2) {
            throw new IllegalStateException("Need at least 2 players to generate matchups");
        }
        if (playerIds.size() % 2 != 0) {
            throw new IllegalStateException("Tournament brackets currently require an even number of players");
        }

        Collections.shuffle(playerIds);
        List<Game> newGames = new ArrayList<>();
        for (int i = 0; i < playerIds.size(); i += 2) {
            newGames.add(gameService.createTournamentGame(tournament, playerIds.get(i), playerIds.get(i + 1), 1,
                    (i / 2) + 1));
        }

        tournament.setGamesList(newGames);
        tournament.setTournamentState(TournamentStates.IN_PROGRESS);
    }

    private void generateNextRoundOrFinish(Tournament tournament, List<Game> allGames) {
        if (tournament.getTournamentState() != TournamentStates.IN_PROGRESS) {
            throw new IllegalStateException("Tournament must be in progress");
        }

        int latestRound = allGames.stream()
                .mapToInt(Game::getRoundNumber)
                .max()
                .orElseThrow();

        List<Game> latestRoundGames = allGames.stream()
                .filter(game -> game.getRoundNumber() == latestRound)
                .collect(Collectors.toList());

        if (latestRoundGames.stream().anyMatch(game -> game.getWinnerId() == null)) {
            throw new IllegalStateException("All current round games must have a winner before generating next round");
        }

        List<UUID> winnerIds = latestRoundGames.stream()
                .map(Game::getWinnerId)
                .collect(Collectors.toList());

        if (winnerIds.size() == 1) {
            tournament.setWinnerId(winnerIds.get(0));
            tournament.setTournamentState(TournamentStates.FINISHED);
            scoreTournament(tournament);
            return;
        }

        if (winnerIds.size() % 2 != 0) {
            throw new IllegalStateException("Next tournament round requires an even number of winners");
        }

        Collections.shuffle(winnerIds);
        List<Game> newGames = new ArrayList<>();
        int nextRound = latestRound + 1;
        for (int i = 0; i < winnerIds.size(); i += 2) {
            newGames.add(gameService.createTournamentGame(tournament, winnerIds.get(i), winnerIds.get(i + 1), nextRound,
                    (i / 2) + 1));
        }

        List<Game> tournamentGames = tournament.getGamesList() == null
                ? new ArrayList<>()
                : tournament.getGamesList();
        tournamentGames.addAll(newGames);
        tournament.setGamesList(tournamentGames);
    }

    private void scoreTournament(Tournament tournament) {
        if (tournament.isScored()) {
            return;
        }

        Map<UUID, Integer> placements = calculatePlacements(tournament, gameService.getTournamentGames(tournament.getId()));

        placements.forEach((userId, rank) -> {
            int points = rank <= 10 ? 11 - rank : 0;
            userService.awardTournamentPoints(userId, points, rank == 1);
        });

        tournament.setScored(true);
        tournament.setScoredAt(LocalDateTime.now());
    }

    private Map<UUID, Integer> calculatePlacements(Tournament tournament, List<Game> games) {
        Map<UUID, Integer> placements = new LinkedHashMap<>();

        if (tournament.getWinnerId() != null) {
            placements.put(tournament.getWinnerId(), 1);
        }

        int latestRound = games.stream()
                .mapToInt(Game::getRoundNumber)
                .max()
                .orElse(0);
        int placement = 2;

        for (int round = latestRound; round >= 1; round--) {
            final int currentRound = round;
            List<UUID> roundLosers = games.stream()
                    .filter(game -> game.getRoundNumber() == currentRound)
                    .map(this::getLoserId)
                    .filter(Objects::nonNull)
                    .filter(userId -> !placements.containsKey(userId))
                    .collect(Collectors.toList());

            for (UUID loserId : roundLosers) {
                placements.put(loserId, placement++);
            }
        }

        for (UUID playerId : tournament.getPlayersIds()) {
            if (!placements.containsKey(playerId)) {
                placements.put(playerId, placement++);
            }
        }

        return placements;
    }

    private UUID getLoserId(Game game) {
        if (game.getWinnerId() == null) {
            return null;
        }
        if (game.getWinnerId().equals(game.getUser1Id())) {
            return game.getUser2Id();
        }
        if (game.getWinnerId().equals(game.getUser2Id())) {
            return game.getUser1Id();
        }
        return null;
    }

    private TournamentViewDTO toTournamentViewDTO(Tournament tournament, UUID currentUserId) {
        int playerCount = tournament.getPlayersIds() == null ? 0 : tournament.getPlayersIds().size();
        boolean joinedByCurrentUser = currentUserId != null
                && tournament.getPlayersIds() != null
                && tournament.getPlayersIds().contains(currentUserId);
        boolean ownedByCurrentUser = currentUserId != null && tournament.getUserOwnerId().equals(currentUserId);

        return new TournamentViewDTO(
                tournament.getId(),
                tournament.getUserOwnerId(),
                tournament.getTitle(),
                tournament.getTournamentState(),
                tournament.getMaxPlayers(),
                playerCount,
                Math.max(tournament.getMaxPlayers() - playerCount, 0),
                tournament.getStartDate(),
                tournament.getInscriptionCloseDate(),
                tournament.getWinnerId(),
                tournament.isDeleted(),
                tournament.isScored(),
                joinedByCurrentUser,
                ownedByCurrentUser);
    }

    private TournamentGameDTO toTournamentGameDTO(Game game) {
        return new TournamentGameDTO(
                game.getId(),
                game.getRoundNumber(),
                game.getBracketPosition(),
                game.getUser1Id(),
                userService.findUserEntityById(game.getUser1Id()).getUsername(),
                game.getUser2Id(),
                userService.findUserEntityById(game.getUser2Id()).getUsername(),
                game.getTeamUser1Id(),
                game.getTeamUser2Id(),
                game.getWinnerId(),
                game.getGameDate());
    }

    private void assertOwnerOrAdmin(Tournament tournament) {
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        if (!tournament.getUserOwnerId().equals(currentUser.getId())) {
            throw new SecurityException("Only the tournament owner or an admin can perform this action");
        }
    }

    private void assertAdmin() {
        if (currentUserService.getCurrentUser().getRole() != UserRole.ADMIN) {
            throw new SecurityException("Only admins can perform this action");
        }
    }

}
