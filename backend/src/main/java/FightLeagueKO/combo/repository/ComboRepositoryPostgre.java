package FightLeagueKO.combo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import FightLeagueKO.combo.model.Combo;

@Repository
public interface ComboRepositoryPostgre extends ComboRepository {

    @Query("SELECT c FROM Combo c WHERE c.pointFighterId = :fighterId AND c.oficial = true AND c.deleted = false")
    List<Combo> findOfficialCombosByPointFighterId(@Param("fighterId") UUID fighterId);
    
}
