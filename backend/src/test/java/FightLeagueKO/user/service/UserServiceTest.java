package FightLeagueKO.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import FightLeagueKO.game.repository.GameRepository;
import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.dto.UpdateUserProfileDTO;
import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.mapper.UserMapper;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserRejectsDuplicateUsername() {
        CreateUserDTO dto = new CreateUserDTO("user", "user@mail.test", "password");
        when(userRepository.existsByUsername("user")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void createUserEncodesPasswordAndSetsDefaults() {
        CreateUserDTO dto = new CreateUserDTO("user", "user@mail.test", "password");
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserProfileRejectsExistingUsername() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setUsername("old");
        user.setEmail("old@mail.test");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("new")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUserProfile(id, new UpdateUserProfileDTO("new", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void getTopUsersRankingMapsUsersInRepositoryOrder() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("winner");
        user.setScore(10);
        user.setTournamentWins(1);
        when(userRepository.findTop25ByDeletedFalseOrderByScoreDescTournamentWinsDescUsernameAsc())
                .thenReturn(List.of(user));

        assertThat(userService.getTopUsersRanking()).hasSize(1);
    }
}
