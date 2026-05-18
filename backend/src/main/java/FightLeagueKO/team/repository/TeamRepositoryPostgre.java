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

    @Query("SELECT e FROM Equipo e WHERE e.pointCharacterId = :pointId " +
       "AND e.secondCharacterId = :secondId AND e.fuse = :fuse AND e.deleted = false")
    Optional<Team> existsByPointCharacterIdAndSecondCharacterIdAndFuseAndDeletedFalse(
            @Param("pointId") UUID pointCharacterId,
            @Param("secondId") UUID secondCharacterId,
            @Param("fuse") FuseType fuse);



}
