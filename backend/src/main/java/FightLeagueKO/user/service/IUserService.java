package FightLeagueKO.user.service;

import java.util.UUID;

import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.dto.UserDTO;

public interface IUserService {

    public UserDTO getUserById(UUID id);

    public UserDTO createUser(CreateUserDTO createUserDTO);
}
