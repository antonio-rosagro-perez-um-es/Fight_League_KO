package character.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import character.dto.CharacterBannerDTO;

public interface CharacterRepositoryPostgre extends CharacterRepository {

    @Query("""
            SELECT new com.example.demo.dto.CharacterBannerDTO(
                c.id,
                c.name,
                c.slug
            )
            FROM Character c
            """)
    List<CharacterBannerDTO> findAllBannerCharacters();
}
