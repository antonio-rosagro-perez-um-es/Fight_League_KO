package FightLeagueKO.team.controller;

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

import FightLeagueKO.team.dto.CreateTeamDTO;
import FightLeagueKO.team.dto.TeamStatsDTO;
import FightLeagueKO.team.dto.UpdateTeamDTO;
import FightLeagueKO.team.model.Team;
import FightLeagueKO.team.service.ITeamService;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private ITeamService teamService;

    @Autowired
    public TeamController(ITeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<Team> getTeamById(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.getTeamById(teamId));
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("all-teams")
    public ResponseEntity<List<Team>> getAllActiveTeams() {
        return ResponseEntity.ok(teamService.getAllActiveTeams());
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody CreateTeamDTO teamDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(teamDTO));
    }

    @PatchMapping("/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTeam(@PathVariable UUID teamId, @RequestBody UpdateTeamDTO teamDTO){
        teamService.updateTeam(teamId, teamDTO);
    }

    @PatchMapping("{teamId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable UUID teamId){
        teamService.softDeleteTeam(teamId);
    }

    @PatchMapping("{teamId}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreTeam(@PathVariable UUID teamId){
        teamService.restoreTeam(teamId);
    }

    @GetMapping("/{teamId}/stats")
    public ResponseEntity<TeamStatsDTO> getTeamStats(@PathVariable UUID teamId){
        return ResponseEntity.ok(teamService.getTeamStats(teamId));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<TeamStatsDTO>> getRankingTeams(){
        return ResponseEntity.ok(teamService.getRankingTeams());
    }


}
