package FightLeagueKO.user.service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.dto.UserDTO;
import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.mapper.UserMapper;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService implements IUserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO getUserById(UUID userId) {
        Objects.requireNonNull(userId, "Paramenter id could not be null");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO createUser(CreateUserDTO userDTO) {

        Objects.requireNonNull(userDTO, "User data could not be null");

        if (userRepository.existsByUsername(userDTO.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(userDTO.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();

        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setRole(UserRole.REGISTERED);
        user.setDeleted(false);

        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public User findUserEntityById(UUID userId) {
        Objects.requireNonNull(userId, "Parameter id could not be null");

        return userRepository.findById(userId)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateRole(UUID userId, UserRole role) {
        User user = findUserEntityById(userId);
        user.setRole(role);
        userRepository.save(user);
    }

}
