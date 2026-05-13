package FightLeagueKO.team.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.team.dto.TeamDTO;
import FightLeagueKO.team.model.Team;

public interface ITeamService {

    public Team getTeamById (UUID id);

    public List<Team> getAllTeams();

    public Team createTeam(TeamDTO teamDTO);

    public void updateTeam(UUID id, TeamDTO teamDTO);
    
    public void softDeleteTeam(UUID id);

    public void restoreTeam(UUID id);

}
