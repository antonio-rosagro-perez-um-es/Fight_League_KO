package FightLeagueKO.user.repository;

import java.util.UUID;

import org.springframework.stereotype.Service;

import FightLeagueKO.user.model.User;
import jakarta.transaction.Transactional;
@Service
@Transactional
public class UserService implements IUserService{

    @Override
    public User getUserById(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserById'");
    }

}
