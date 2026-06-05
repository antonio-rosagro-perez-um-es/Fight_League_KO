package FightLeagueKO.game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import FightLeagueKO.game.dto.CreateGameDTO;
import FightLeagueKO.game.model.Game;
import FightLeagueKO.game.repository.GameRepository;
import FightLeagueKO.security.CurrentUserService;
import FightLeagueKO.team.service.TeamService;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private GameService gameService;

    @Test
    void createGameRejectsPastDate() {
        CreateGameDTO dto = new CreateGameDTO(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> gameService.createGame(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("date");
    }

    @Test
    void setWinnerRejectsUserOutsideGame() {
        UUID gameId = UUID.randomUUID();
        Game game = game(UUID.randomUUID(), UUID.randomUUID());
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        assertThatThrownBy(() -> gameService.setWinner(gameId, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("players");
    }

    @Test
    void setWinnerChangesStatsSafelyWhenWinnerChanges() {
        UUID gameId = UUID.randomUUID();
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        UUID team1 = UUID.randomUUID();
        UUID team2 = UUID.randomUUID();
        Game game = game(user1, user2);
        game.setTeamUser1Id(team1);
        game.setTeamUser2Id(team2);
        game.setWinnerId(user1);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        gameService.setWinner(gameId, user2);

        assertThat(game.getWinnerId()).isEqualTo(user2);
        verify(teamService).revertTeamStats(team1, true);
        verify(teamService).revertTeamStats(team2, false);
        verify(teamService).updateTeamStats(team1, false);
        verify(teamService).updateTeamStats(team2, true);
    }

    private Game game(UUID user1, UUID user2) {
        Game game = new Game();
        game.setUser1Id(user1);
        game.setUser2Id(user2);
        game.setGameDate(LocalDate.now());
        return game;
    }
}
