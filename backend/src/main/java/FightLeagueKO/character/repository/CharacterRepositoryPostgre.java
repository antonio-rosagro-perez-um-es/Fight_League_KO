package FightLeagueKO.character.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import FightLeagueKO.character.dto.CharacterBannerDTO;

public interface CharacterRepositoryPostgre extends CharacterRepository {

    @Query("""
            SELECT new FightLeagueKO.character.dto.CharacterBannerDTO(
                c.id,
                c.name,
                c.slug
            )
            FROM Character c
            WHERE c.delete = false
            """)
    List<CharacterBannerDTO> findAllBannerCharacters();

    @Query("SELECT COALESCE(SUM(c.playCounter), 0) FROM Character c WHERE c.deleted = false")
    Long getAllCharactersPlayRate();
}