package character.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import character.dto.CharacterBannerDTO;
import character.model.Character;

@NoRepositoryBean
public interface CharacterRepository extends CrudRepository<Character, UUID>{

    List<CharacterBannerDTO> findAllBannerCharacters();
}
