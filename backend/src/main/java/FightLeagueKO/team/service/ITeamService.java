package FightLeagueKO.team.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.team.dto.CreateTeamDTO;
import FightLeagueKO.team.dto.TeamStatsDTO;
import FightLeagueKO.team.dto.UpdateTeamDTO;
import FightLeagueKO.team.model.Team;

public interface ITeamService {

    Team getTeamById(UUID teamId);

    List<Team> getAllTeams();

    List<Team> getAllActiveTeams();

    Team createTeam(CreateTeamDTO teamDTO);

    void updateTeam(UUID teamId, UpdateTeamDTO teamDTO);

    void softDeleteTeam(UUID teamId);

    void restoreTeam(UUID teamId);

    void updateTeamStats(UUID teamId, boolean result);

    void revertTeamStats(UUID teamId, boolean wasWinner);

    TeamStatsDTO getTeamStats(UUID teamId);

    List<TeamStatsDTO> getRankingTeams();

}
