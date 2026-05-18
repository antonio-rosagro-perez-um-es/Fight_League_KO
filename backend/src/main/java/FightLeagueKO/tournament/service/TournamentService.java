package FightLeagueKO.tournament.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import FightLeagueKO.game.model.Game;
import FightLeagueKO.tournament.dto.CreateTournamentDTO;
import FightLeagueKO.tournament.dto.UpdateTournamentDTO;
import FightLeagueKO.tournament.enums.TournamentStates;
import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.tournament.repository.TournamentRepository;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.repository.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TournamentService implements ITournamentService {

    private TournamentRepository tournamentRepository;
    private UserService userService;

    public TournamentService(TournamentRepository tournamentRepository, UserService userService) {
        this.tournamentRepository = tournamentRepository;
        this.userService = userService;
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

        User user = userService.getUserById(tournamentDTO.userOwner());

        if (tournamentDTO.title() == null || tournamentDTO.title().isEmpty())
            throw new IllegalArgumentException("Tournament title cant be empty or null");

        if (tournamentDTO.starDate().isAfter(tournamentDTO.inscriptionCloseDate()))
            throw new IllegalArgumentException("Tournament start date cant be before close inscription date");

        if (tournamentDTO.starDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Tournament start date cant be befor now");

        if (tournamentDTO.inscriptionCloseDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Tournament inscriptions close date cant be befor now");

        tournament.setUserOwner(user);
        tournament.setTitle(tournamentDTO.title());
        tournament.setTournamentState(TournamentStates.REGISTRATION);
        tournament.setMaxPlayers(tournamentDTO.maxPlayers());
        tournament.setPlayersList(new ArrayList<User>());
        tournament.setGamesList(new ArrayList<Game>());
        tournament.setStartDate(tournamentDTO.starDate());
        tournament.setInscriptionCloseDate(tournamentDTO.inscriptionCloseDate());
        tournament.setManualClose(false);
        tournament.setWinner(null);
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

        if (tournament.getPlayersList().size() < tournament.getMaxPlayers()) {

            User user = userService.getUserById(userId);

            tournament.addPlayer(user);
        }

        if (tournament.getPlayersList().size() >= tournament.getMaxPlayers())
            tournament.setTournamentState(TournamentStates.WAITING_START);

    }

    @Override
    public void exitTournament(UUID tournamentId, UUID userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id" + tournamentId));

        if (!tournament.getTournamentState().equals(TournamentStates.REGISTRATION))
            throw new IllegalStateException("Can't leave a tournamen when registration is closed");

        User user = userService.getUserById(userId);

        tournament.removePlayer(user);

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

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id" + tournamentId));

        List<User> playUsers = tournament.getPlayersList();
        int numPlayers = playUsers.size();

        if (numPlayers < 2)
            throw new IllegalStateException("Need at least 2 players");

        int numMatches = numPlayers / 2;

    }

}
