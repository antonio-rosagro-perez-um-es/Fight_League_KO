package FightLeagueKO.tournament.controller;

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

import FightLeagueKO.tournament.dto.CreateTournamentDTO;
import FightLeagueKO.tournament.dto.UpdateTournamentDTO;
import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.tournament.service.ITournamentService;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private ITournamentService tournamentService;

    @Autowired
    public TournamentController(ITournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable UUID tournamentId) {

        return ResponseEntity.ok(tournamentService.getTournamentById(tournamentId));
    }

    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournament());
    }

    @GetMapping("/all-tournaments")
    public ResponseEntity<List<Tournament>> getAllActiveTournaments() {
        return ResponseEntity.ok(tournamentService.getAllActiveTournament());
    }

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody CreateTournamentDTO tournamentDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.createTournament(tournamentDTO));
    }

    @PatchMapping("/{tournamentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTournament(@PathVariable UUID tournamentId, @RequestBody UpdateTournamentDTO tournamentDTO) {
        tournamentService.updateTournament(tournamentId, tournamentDTO);
    }

    @PatchMapping("/{tournamentId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTournament(@PathVariable UUID tournamentId) {
        tournamentService.softDeleteTournament(tournamentId);
    }

    @PatchMapping("/{tournamentId}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreTournament(@PathVariable UUID tournamentId) {
        tournamentService.restoreTournament(tournamentId);
    }

    @PatchMapping("{tournamentId}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void joinTournament(@PathVariable UUID tournamentId) {
        tournamentService.joinTournament(tournamentId);
    }

    @PatchMapping("{tournamentId}/exit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exitTournament(@PathVariable UUID tournamentId) {
        tournamentService.exitTournament(tournamentId);
    }

    @PatchMapping("/{tournamentId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeRegistrations(@PathVariable UUID tournamentId) {
        tournamentService.closeRegistrations(tournamentId);
    }

}
