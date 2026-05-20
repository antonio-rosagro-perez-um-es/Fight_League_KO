package FightLeagueKO.fighter.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import FightLeagueKO.fighter.dto.FighterBannerDTO;

public interface FighterRepositoryPostgre extends FighterRepository {

    @Query("""
            SELECT new FightLeagueKO.fighter.dto.FighterBannerDTO(
                c.id,
                c.name,
                c.slug
            )
            FROM Fighter c
            WHERE c.delete = false
            """)
    List<FighterBannerDTO> findAllBannerFighters();

    @Query("SELECT COALESCE(SUM(c.playCounter), 0) FROM Fighter c WHERE c.deleted = false")
    Long getAllFightersPlayRate();
}