package FightLeagueKO.user.service;

import java.util.Optional;
import java.util.UUID;

import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.dto.UserDTO;
import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.model.User;

public interface IUserService {

    UserDTO getUserById(UUID id);

    UserDTO createUser(CreateUserDTO createUserDTO);

    User findUserEntityById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void updateRole(UUID userId, UserRole role);
}
