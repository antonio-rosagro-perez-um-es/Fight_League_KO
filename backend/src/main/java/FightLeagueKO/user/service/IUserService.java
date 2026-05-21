package FightLeagueKO.user.service;

import java.util.UUID;

import FightLeagueKO.user.model.User;

public interface IUserService {

    public User getUserById(UUID id);

    public User createUser();
}
