package FightLeagueKO.tournament.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import FightLeagueKO.game.dto.CreateGameDTO;
import FightLeagueKO.game.model.Game;
import FightLeagueKO.game.repository.GameRepository;
import FightLeagueKO.game.service.GameService;
import FightLeagueKO.tournament.dto.CreateTournamentDTO;
import FightLeagueKO.tournament.dto.UpdateTournamentDTO;
import FightLeagueKO.tournament.enums.TournamentStates;
import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.tournament.repository.TournamentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TournamentService implements ITournamentService {

    private TournamentRepository tournamentRepository;
    private GameService gameService;

    public TournamentService(TournamentRepository tournamentRepository, GameService gameService) {
        this.tournamentRepository = tournamentRepository;
        this.gameService = gameService;
    }

    public Tournament getTournamentById(UUID id) {
        Objects.requireNonNull(id, "Paramenter id could not be null");

        return tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found with id: " + id));
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
    public Tournament createTournament(CreateTournamentDTO tournamentDTO) {

        Objects.requireNonNull(tournamentDTO, "TournamentDTO could not be null");

        Tournament tournament = new Tournament();

        if (tournamentDTO.title() == null || tournamentDTO.title().isEmpty())
            throw new IllegalArgumentException("Tournament title cant be empty or null");

        if (tournamentDTO.starDate().isBefore(tournamentDTO.inscriptionCloseDate()))
            throw new IllegalArgumentException("Tournament start date cant be before close inscription date");

        if (tournamentDTO.starDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Tournament start date cant be befor now");

        if (tournamentDTO.inscriptionCloseDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Tournament inscriptions close date cant be befor now");

        tournament.setUserOwnerId(tournamentDTO.userOwner());
        tournament.setTitle(tournamentDTO.title());
        tournament.setTournamentState(TournamentStates.REGISTRATION);
        tournament.setMaxPlayers(tournamentDTO.maxPlayers());
        tournament.setPlayersIds(new ArrayList<>());
        tournament.setGamesList(new ArrayList<Game>());
        tournament.setStartDate(tournamentDTO.starDate());
        tournament.setInscriptionCloseDate(tournamentDTO.inscriptionCloseDate());
        tournament.setManualClose(false);
        tournament.setWinnerId(null);
        tournament.setDeleted(false);

        return tournamentRepository.save(tournament);
    }

    @Override
    public void updateTournament(UUID tournamentId, UpdateTournamentDTO tournamentDTO) {

        Objects.requireNonNull(tournamentDTO, "Parameter could not be null");

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + tournamentId));

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

        tournament.setDeleted(true);

        tournamentRepository.save(tournament);
    }

    @Override
    public void restoreTournament(UUID tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id:" + tournamentId));

        tournament.setDeleted(false);

        tournamentRepository.save(tournament);
    }

    @Override
    public void joinTournament(UUID tournamentId, UUID userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id" + tournamentId));

        if (!tournament.getTournamentState().equals(TournamentStates.REGISTRATION))
            throw new IllegalStateException("Registration period has ended");

        if (tournament.getPlayersIds().size() < tournament.getMaxPlayers()) {

            tournament.addPlayer(userId);
        }

        if (tournament.getPlayersIds().size() >= tournament.getMaxPlayers())
            tournament.setTournamentState(TournamentStates.WAITING_START);

    }

    @Override
    public void exitTournament(UUID tournamentId, UUID userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id" + tournamentId));

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

        if (!tournament.getTournamentState().equals(TournamentStates.REGISTRATION))
            throw new IllegalStateException("Tournament already closed");

        tournament.setTournamentState(TournamentStates.WAITING_START);

        tournament.setManualClose(true);
    }

    @Override
    public void generateMatchups(UUID tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);

        if (tournament.getTournamentState() != TournamentStates.IN_PROGRESS) {
            throw new IllegalStateException("Tournament must be in progress");
        }

        List<Game> allGames = tournament.getGamesList();
        if (allGames == null) {
            allGames = new ArrayList<>();
            tournament.setGamesList(allGames);
        }

        // Check if any current round games are still pending (no winner)
        long pendingCount = allGames.stream()
                .filter(g -> g.getWinnerId() == null)
                .count();

        if (pendingCount > 0) {
            throw new IllegalStateException("All current round games must have a winner before generating next round");
        }

        if (allGames.isEmpty()) {
            // === FIRST ROUND: pair up all registered players ===
            List<UUID> playerIds = new ArrayList<>(tournament.getPlayersIds());

            if (playerIds.size() < 2) {
                throw new IllegalStateException("Need at least 2 players to generate matchups");
            }

            Collections.shuffle(playerIds);

            List<Game> newGames = new ArrayList<>();
            for (int i = 0; i < playerIds.size() - 1; i += 2) {
                CreateGameDTO gameDTO = new CreateGameDTO(
                        playerIds.get(i),
                        playerIds.get(i + 1),
                        tournament.getStartDate());
                newGames.add(gameService.createGame(gameDTO));
            }

            allGames.addAll(newGames);
            tournament.setTournamentState(TournamentStates.IN_PROGRESS);

        } else {
            // === SUBSEQUENT ROUNDS: collect winners and pair them ===
            List<UUID> winnerIds = allGames.stream()
                    .map(Game::getWinnerId)
                    .collect(Collectors.toList());

            if (winnerIds.size() == 1) {
                tournament.setWinnerId(winnerIds.get(0));
                tournament.setTournamentState(TournamentStates.FINISHED);
                tournamentRepository.save(tournament);
                return;
            }

            Collections.shuffle(winnerIds);

            List<Game> newGames = new ArrayList<>();
            for (int i = 0; i < winnerIds.size() - 1; i += 2) {
                CreateGameDTO gameDTO = new CreateGameDTO(
                        winnerIds.get(i),
                        winnerIds.get(i + 1),
                        LocalDate.now());
                newGames.add(gameService.createGame(gameDTO));
            }

            allGames.addAll(newGames);
        }

        tournamentRepository.save(tournament);
    }

}
