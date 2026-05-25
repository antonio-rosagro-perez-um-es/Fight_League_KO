package FightLeagueKO.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.model.User;

public interface UserRepository extends CrudRepository<User, UUID>{

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRole(UserRole role);

    List<User> findTop25ByDeletedFalseOrderByScoreDescTournamentWinsDescUsernameAsc();
}
