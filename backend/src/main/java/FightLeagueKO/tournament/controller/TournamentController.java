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
import FightLeagueKO.tournament.dto.TournamentGameDTO;
import FightLeagueKO.tournament.dto.TournamentStandingDTO;
import FightLeagueKO.tournament.dto.TournamentViewDTO;
import FightLeagueKO.tournament.dto.UpdateTournamentDTO;
import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.tournament.service.ITournamentService;
import FightLeagueKO.security.CurrentUserService;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private ITournamentService tournamentService;
    private CurrentUserService currentUserService;

    @Autowired
    public TournamentController(ITournamentService tournamentService, CurrentUserService currentUserService) {
        this.tournamentService = tournamentService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable UUID tournamentId) {

        return ResponseEntity.ok(tournamentService.getTournamentById(tournamentId));
    }

    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournament());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<TournamentViewDTO>> getAllTournamentViewsForAdmin() {
        UUID currentUserId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(tournamentService.getAllTournamentViews(currentUserId));
    }

    @GetMapping("/all-tournaments")
    public ResponseEntity<List<TournamentViewDTO>> getAllActiveTournaments() {
        return ResponseEntity.ok(tournamentService.getAllActiveTournamentViews(null));
    }

    @GetMapping("/{tournamentId}/view")
    public ResponseEntity<TournamentViewDTO> getTournamentView(@PathVariable UUID tournamentId) {
        UUID currentUserId = currentUserService.getCurrentUserIdIfAuthenticated().orElse(null);
        return ResponseEntity.ok(tournamentService.getTournamentView(tournamentId, currentUserId));
    }

    @GetMapping("/{tournamentId}/bracket")
    public ResponseEntity<List<TournamentGameDTO>> getTournamentBracket(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.getTournamentBracket(tournamentId));
    }

    @GetMapping("/{tournamentId}/standings")
    public ResponseEntity<List<TournamentStandingDTO>> getTournamentStandings(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.getTournamentStandings(tournamentId));
    }

    @GetMapping("/me/owned")
    public ResponseEntity<List<TournamentViewDTO>> getOwnedTournaments() {
        UUID currentUserId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(tournamentService.getOwnedTournamentViews(currentUserId));
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

    @PatchMapping("/{tournamentId}/generate-matchups")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void generateMatchups(@PathVariable UUID tournamentId) {
        tournamentService.generateMatchups(tournamentId);
    }

}
