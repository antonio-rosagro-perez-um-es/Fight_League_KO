package FightLeagueKO.character.service;

import java.util.List;
import java.util.UUID;
import FightLeagueKO.character.model.Character;

import FightLeagueKO.character.dto.CharacterBannerDTO;
import FightLeagueKO.character.dto.CharacterUpdateDTO;
import FightLeagueKO.character.dto.NewCharacterDTO;

public interface ICharacterService {

    List<CharacterBannerDTO> getAllCharactersBanner();

    Character getCharacterById(UUID id);

    Character createCharacter(NewCharacterDTO characterDTO);

    void updateCharacter (UUID id, CharacterUpdateDTO characterDTO);

    void softDeleteCharacter (UUID id);

    void restoreCharacter (UUID id);
}