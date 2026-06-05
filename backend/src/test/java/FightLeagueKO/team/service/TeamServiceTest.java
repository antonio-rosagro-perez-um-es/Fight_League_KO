package FightLeagueKO.team.service;

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

import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.fighter.service.FighterService;
import FightLeagueKO.team.dto.CreateTeamDTO;
import FightLeagueKO.team.mapper.TeamMapper;
import FightLeagueKO.team.model.Team;
import FightLeagueKO.team.repository.TeamRepository;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private FighterService fighterService;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamService teamService;

    @Test
    void createTeamRejectsSameFighter() {
        UUID fighterId = UUID.randomUUID();

        assertThatThrownBy(() -> teamService.createTeam(new CreateTeamDTO(fighterId, fighterId, FuseType.SIDEKICK)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different");
    }

    @Test
    void createTeamReturnsExistingTeamWhenReverseOrderExists() {
        UUID point = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        Team existing = new Team();
        existing.setId(UUID.randomUUID());

        when(teamRepository.existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(point, second,
                FuseType.SIDEKICK)).thenReturn(Optional.empty());
        when(teamRepository.existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(second, point,
                FuseType.SIDEKICK)).thenReturn(Optional.of(existing));

        Team result = teamService.createTeam(new CreateTeamDTO(point, second, FuseType.SIDEKICK));

        assertThat(result).isSameAs(existing);
    }

    @Test
    void updateTeamStatsUpdatesTeamAndBothFighters() {
        UUID teamId = UUID.randomUUID();
        UUID point = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        Team team = new Team();
        team.setPointFighterId(point);
        team.setSecondFighterId(second);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        teamService.updateTeamStats(teamId, true);

        assertThat(team.getPlayCounter()).isEqualTo(1);
        assertThat(team.getWinCounter()).isEqualTo(1);
        verify(fighterService).updateFighterStats(point, true);
        verify(fighterService).updateFighterStats(second, true);
    }
}
