package FightLeagueKO.game.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import FightLeagueKO.game.dto.CreateGameDTO;
import FightLeagueKO.game.dto.SetTeamsDTO;
import FightLeagueKO.game.dto.UpdateGameDTO;
import FightLeagueKO.game.model.Game;
import FightLeagueKO.game.service.GameService;
import FightLeagueKO.game.service.IGameService;

@RestController
@RequestMapping("/games")
public class GameController {

    private IGameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGameById(@PathVariable UUID gameId) {
        return ResponseEntity.ok(gameService.getGameById(gameId));
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @GetMapping("/all-games")
    public ResponseEntity<List<Game>> getAllActiveGames() {
        return ResponseEntity.ok(gameService.getAllActiveGames());
    }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody CreateGameDTO gameDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(gameDTO));
    }

    @PatchMapping("/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGame(@PathVariable UUID gameId, @RequestBody UpdateGameDTO gameDTO) {
        gameService.updateGame(gameId, gameDTO);
    }

    @PatchMapping("/{gameId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable UUID gameId) {
        gameService.softDeleteGame(gameId);
    }

    @PatchMapping("/{gameId}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreGame(@PathVariable UUID gameId) {
        gameService.restoreGame(gameId);
    }

    @PatchMapping("/{gameId}/teams")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setTeams(@PathVariable UUID gameId, @RequestBody SetTeamsDTO setTeamsDTO) {
        gameService.setTeams(gameId, setTeamsDTO.team1(), setTeamsDTO.team2());
    }

    @PatchMapping("/{gameId}/winner/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setWinner(@PathVariable UUID gameId, @PathVariable UUID userId) {
        gameService.setWinner(gameId, userId);
    }

}
