package FightLeagueKO.tournament.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.tournament.service.ITournamentService;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {


    private ITournamentService tournamentService;

    public TournamentController( ITournamentService tournamentService){
        this.tournamentService = tournamentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(UUID id){

        return ResponseEntity.ok(tournamentService.getTournamentById(null))
    }



}
