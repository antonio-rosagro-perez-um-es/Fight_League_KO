package FightLeagueKO.combo.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import FightLeagueKO.combo.model.Combo;

@NoRepositoryBean
public interface ComboRespository extends CrudRepository<Combo, UUID>{

}
