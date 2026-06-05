package FightLeagueKO.combo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import FightLeagueKO.combo.dto.ComboCreateDTO;
import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.combo.enums.VoteType;
import FightLeagueKO.combo.mapper.ComboMapper;
import FightLeagueKO.combo.model.Combo;
import FightLeagueKO.combo.model.ComboVote;
import FightLeagueKO.combo.repository.ComboRepository;
import FightLeagueKO.combo.repository.ComboVoteRepository;
import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.service.FighterService;
import FightLeagueKO.security.CurrentUserService;
import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.model.User;

@ExtendWith(MockitoExtension.class)
class ComboServiceTest {

    @Mock
    private ComboRepository comboRepository;

    @Mock
    private ComboMapper comboMapper;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private FighterService fighterService;

    @Mock
    private ComboVoteRepository comboVoteRepository;

    @InjectMocks
    private ComboService comboService;

    @Test
    void createComboMakesRegisteredUserComboPrivateAndNotOfficial() {
        User user = user(UserRole.REGISTERED);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(comboRepository.save(any(Combo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fighterService.getFighterById(any(UUID.class))).thenReturn(fighter("Ahri"));

        comboService.createCombo(validCreateDTO());

        verify(comboRepository).save(any(Combo.class));
    }

    @Test
    void createComboRejectsMissingNotation() {
        when(currentUserService.getCurrentUser()).thenReturn(user(UserRole.REGISTERED));
        ComboCreateDTO dto = new ComboCreateDTO("title", UUID.randomUUID(), null, " ", ComboDificulty.BEGINNER,
                FuseType.SIDEKICK, "media", "description", 0, 100);

        assertThatThrownBy(() -> comboService.createCombo(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Text notation");
    }

    @Test
    void voteComboChangesLikeToDislikeCounters() {
        UUID comboId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Combo combo = new Combo();
        combo.setLikeCounter(1);
        ComboVote existingVote = new ComboVote(comboId, userId, VoteType.LIKE);
        User user = user(UserRole.REGISTERED);
        user.setId(userId);

        when(comboRepository.findById(comboId)).thenReturn(Optional.of(combo));
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(comboVoteRepository.findByComboIdAndUserId(comboId, userId)).thenReturn(Optional.of(existingVote));

        comboService.voteCombo(comboId, VoteType.DISLIKE);

        assertThat(combo.getLikeCounter()).isZero();
        assertThat(combo.getDislikeCounter()).isEqualTo(1);
        verify(comboVoteRepository).save(existingVote);
        verify(comboRepository).save(combo);
    }

    private ComboCreateDTO validCreateDTO() {
        return new ComboCreateDTO("title", UUID.randomUUID(), null, "236P", ComboDificulty.BEGINNER,
                FuseType.SIDEKICK, "media", "description", 0, 100);
    }

    private User user(UserRole role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(role);
        return user;
    }

    private Fighter fighter(String name) {
        Fighter fighter = new Fighter();
        fighter.setName(name);
        return fighter;
    }
}
