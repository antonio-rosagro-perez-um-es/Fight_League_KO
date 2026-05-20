package FightLeagueKO.team.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.team.model.Team;

public interface TeamRepositoryPostgre extends TeamRepository {
      @Query("SELECT t FROM Team t WHERE t.deleted = false")
      List<Team> getAllActiveTeams();

      @Query("SELECT COALESCE(SUM(t.playCounter), 0) FROM Team t WHERE t.deleted = false")
      Long getAllTeamsPlayRate();

      @Query("SELECT t FROM Team t WHERE t.pointFighter.id = :pointId " +
                  "AND t.secondFighter.id = :secondId AND t.fuse = :fuse AND t.deleted = false")
      Optional<Team> existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(
                  @Param("pointId") UUID pointFighterId,
                  @Param("secondId") UUID secondFighterId,
                  @Param("fuse") FuseType fuse);

      @Query("SELECT t FROM Team t WHERE t.deleted = false AND t.playCounter > 0")
      List<Team> getAllActiveTeamsWithPlays();

}
