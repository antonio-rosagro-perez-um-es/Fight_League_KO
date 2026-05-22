package FightLeagueKO.user.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService implements IUserService{

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(UUID userId) {
         Objects.requireNonNull(userId, "Paramenter id could not be null");

        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Override
    public User createUser(CreateUserDTO userDTO){

        User user = new User();

        user.setName(userDTO.userName());

        return userRepository.save(user);
    }

}
