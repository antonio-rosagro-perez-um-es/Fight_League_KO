package FightLeagueKO.user.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import FightLeagueKO.user.model.User;

public interface UserRepository extends CrudRepository<User, UUID>{

}
