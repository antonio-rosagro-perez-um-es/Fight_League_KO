package FightLeagueKO.fighter.service;

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

import FightLeagueKO.fighter.dto.CreateFighterDTO;
import FightLeagueKO.fighter.mapper.FighterMapper;
import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.repository.FighterRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class FighterServiceTest {

    @Mock
    private FighterRepository fighterRepository;

    @Mock
    private FighterMapper fighterMapper;

    @InjectMocks
    private FighterService fighterService;

    @Test
    void createFighterRejectsMissingName() {
        CreateFighterDTO dto = new CreateFighterDTO(null, "desc", "region", "arch", "title", "likes", "dislikes",
                "slug", 1, 2, 3, 4, 5, 6);

        assertThatThrownBy(() -> fighterService.createFighter(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void getFighterByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(fighterRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fighterService.getFighterById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void updateFighterStatsAddsPlayAndWinCounters() {
        UUID id = UUID.randomUUID();
        Fighter fighter = new Fighter();
        when(fighterRepository.findById(id)).thenReturn(Optional.of(fighter));
        when(fighterRepository.save(any(Fighter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        fighterService.updateFighterStats(id, true);

        assertThat(fighter.getPlayCounter()).isEqualTo(1);
        assertThat(fighter.getWinCounter()).isEqualTo(1);
        assertThat(fighter.getLoseCounter()).isZero();
        verify(fighterRepository).save(fighter);
    }
}
