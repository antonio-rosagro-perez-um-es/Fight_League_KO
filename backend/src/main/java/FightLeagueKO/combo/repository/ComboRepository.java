package FightLeagueKO.combo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import FightLeagueKO.combo.model.Combo;

@NoRepositoryBean
public interface ComboRepository extends JpaRepository<Combo, UUID>, JpaSpecificationExecutor<Combo>{

    List<Combo> findOfficialCombosByPointFighterId(UUID fighterId);

    List<Combo> findByCreatorUserIdAndDeletedFalseOrderByCreatedAtDesc(UUID creatorUserId);

}
