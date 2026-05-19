package FightLeagueKO.combo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import FightLeagueKO.combo.model.Combo;

@NoRepositoryBean
public interface ComboRepository extends JpaRepository<Combo, UUID>, JpaSpecificationExecutor<Combo>{

}
