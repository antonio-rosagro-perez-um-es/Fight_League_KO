package FightLeagueKO.fighter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import FightLeagueKO.fighter.dto.FighterBannerDTO;
import FightLeagueKO.fighter.model.Fighter;

@NoRepositoryBean
public interface FighterRepository extends CrudRepository<Fighter, UUID> {

    Optional<Fighter> findBySlug(String slug);

    List<FighterBannerDTO> findAllBannerFighters();

    Long getAllFightersPlayRate();

    List<Fighter> getAllActiveFightersWithPlays();
}