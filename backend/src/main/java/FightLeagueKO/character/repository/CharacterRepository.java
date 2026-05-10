package FightLeagueKO.character.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import FightLeagueKO.character.dto.CharacterBannerDTO;
import FightLeagueKO.character.model.Character;

public interface CharacterRepository extends CrudRepository<Character, UUID>{

    @Query("SELECT new FightLeagueKO.character.dto.CharacterBannerDTO(c.id, c.name, c.slug) FROM Character c WHERE c.deleted = false")
    List<CharacterBannerDTO> findAllBannerCharacters();
}