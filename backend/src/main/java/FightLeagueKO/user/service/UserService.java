package FightLeagueKO.user.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import FightLeagueKO.game.model.Game;
import FightLeagueKO.game.repository.GameRepository;
import FightLeagueKO.user.dto.AdminUserDTO;
import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.dto.UpdateUserProfileDTO;
import FightLeagueKO.user.dto.UserDTO;
import FightLeagueKO.user.dto.UserProfileDTO;
import FightLeagueKO.user.dto.UserRankingDTO;
import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.mapper.UserMapper;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService implements IUserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private GameRepository gameRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
            GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.gameRepository = gameRepository;
    }

    @Override
    public UserDTO getUserById(UUID userId) {
        Objects.requireNonNull(userId, "Paramenter id could not be null");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO createUser(CreateUserDTO userDTO) {

        Objects.requireNonNull(userDTO, "User data could not be null");

        if (userRepository.existsByUsername(userDTO.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(userDTO.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();

        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setRole(UserRole.REGISTERED);
        user.setDeleted(false);
        user.setScore(0);
        user.setTournamentWins(0);

        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public User findUserEntityById(UUID userId) {
        Objects.requireNonNull(userId, "Parameter id could not be null");

        return userRepository.findById(userId)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateRole(UUID userId, UserRole role) {
        User user = findUserEntityById(userId);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void awardTournamentPoints(UUID userId, int points, boolean tournamentWinner) {
        User user = findUserEntityById(userId);
        user.addScore(points);
        if (tournamentWinner) {
            user.addTournamentWin();
        }
        userRepository.save(user);
    }

    @Override
    public List<UserRankingDTO> getTopUsersRanking() {
        return userRepository.findTop25ByDeletedFalseOrderByScoreDescTournamentWinsDescUsernameAsc().stream()
                .map(user -> new UserRankingDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getScore(),
                        user.getTournamentWins()))
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileDTO getUserProfile(UUID userId) {
        return buildProfile(findUserEntityById(userId));
    }

    @Override
    public UserProfileDTO updateUserProfile(UUID userId, UpdateUserProfileDTO profileDTO) {
        Objects.requireNonNull(profileDTO, "Profile data could not be null");
        User user = findUserEntityById(userId);

        Optional.ofNullable(profileDTO.username())
                .filter(username -> !username.isBlank())
                .ifPresent(username -> {
                    if (!username.equals(user.getUsername()) && userRepository.existsByUsername(username)) {
                        throw new IllegalArgumentException("Username already exists");
                    }
                    user.setUsername(username);
                });

        Optional.ofNullable(profileDTO.email())
                .filter(email -> !email.isBlank())
                .ifPresent(email -> {
                    if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                        throw new IllegalArgumentException("Email already exists");
                    }
                    user.setEmail(email);
                });

        userRepository.save(user);
        return buildProfile(user);
    }

    @Override
    public List<AdminUserDTO> getAllUsersForAdmin() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(user -> new AdminUserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole(),
                        user.isDeleted(),
                        user.getScore(),
                        user.getTournamentWins()))
                .collect(Collectors.toList());
    }

    @Override
    public void softDeleteUser(UUID userId) {
        User user = findUserEntityIncludingDeletedById(userId);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void restoreUser(UUID userId) {
        User user = findUserEntityIncludingDeletedById(userId);
        user.setDeleted(false);
        userRepository.save(user);
    }

    private User findUserEntityIncludingDeletedById(UUID userId) {
        Objects.requireNonNull(userId, "Parameter id could not be null");
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private UserProfileDTO buildProfile(User user) {
        List<Game> games = gameRepository.getRecentGamesByUser(user.getId());
        int gamesPlayed = games.size();
        int gamesWon = (int) games.stream()
                .filter(game -> user.getId().equals(game.getWinnerId()))
                .count();

        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getScore(),
                user.getTournamentWins(),
                gamesPlayed,
                gamesWon,
                gamesPlayed - gamesWon);
    }

}
