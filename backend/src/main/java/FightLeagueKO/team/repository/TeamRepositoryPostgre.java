package FightLeagueKO.team.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import FightLeagueKO.team.model.Team;

public interface TeamRepositoryPostgre extends TeamRepository {
    @Query("SELECT t FROM Team t WHERE t.deleted = false")
    List<Team> getAllActiveTeams();

    @Query("SELECT COALESCE(SUM(t.playCounter), 0) FROM Team t WHERE t.deleted = false")
    Long getAllTeamsPlayRate();

}
