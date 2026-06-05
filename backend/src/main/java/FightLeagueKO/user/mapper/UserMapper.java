package FightLeagueKO.user.mapper;

import org.springframework.stereotype.Component;

import FightLeagueKO.user.dto.UserDTO;
import FightLeagueKO.user.model.User;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getScore(),
            user.getTournamentWins()
        );
    }
}
