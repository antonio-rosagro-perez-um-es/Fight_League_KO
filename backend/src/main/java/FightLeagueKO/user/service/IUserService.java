package FightLeagueKO.user.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import FightLeagueKO.user.dto.AdminUserDTO;
import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.dto.UpdateUserProfileDTO;
import FightLeagueKO.user.dto.UserDTO;
import FightLeagueKO.user.dto.UserProfileDTO;
import FightLeagueKO.user.dto.UserRankingDTO;
import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.model.User;

public interface IUserService {

    UserDTO getUserById(UUID id);

    UserDTO createUser(CreateUserDTO createUserDTO);

    User findUserEntityById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void updateRole(UUID userId, UserRole role);

    void awardTournamentPoints(UUID userId, int points, boolean tournamentWinner);

    List<UserRankingDTO> getTopUsersRanking();

    UserProfileDTO getUserProfile(UUID userId);

    UserProfileDTO updateUserProfile(UUID userId, UpdateUserProfileDTO profileDTO);

    List<AdminUserDTO> getAllUsersForAdmin();

    void softDeleteUser(UUID userId);

    void restoreUser(UUID userId);
}
