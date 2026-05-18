package FightLeagueKO.user.repository;

import java.util.UUID;

import FightLeagueKO.user.model.User;

public interface IUserService {

    public User getUserById(UUID id);
}
