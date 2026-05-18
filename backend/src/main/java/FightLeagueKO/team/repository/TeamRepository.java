package FightLeagueKO.team.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.team.model.Team;

@NoRepositoryBean
public interface TeamRepository extends CrudRepository<Team, UUID> {

    List<Team> getAllActiveTeams();

    Long getAllTeamsPlayRate();

    Optional<Team> existsByPointCharacterIdAndSecondCharacterIdAndFuseAndDeletedFalse(
            @Param("pointId") UUID pointCharacterId,
            @Param("secondId") UUID secondCharacterId,
            @Param("fuse") FuseType fuse);

}
