package character.services;

import java.util.List;
import java.util.UUID;
import character.model.Character;

import character.dto.CharacterBannerDTO;
import character.dto.CharacterUpdateDTO;
import character.dto.NewCharacterDTO;

public interface ICharacterService {

    List<CharacterBannerDTO> getAllCharactersBanner();
    
    Character getCharacterById(UUID id);

    Character createCharacter(NewCharacterDTO characterDTO);

    void updateCharacter (UUID id, CharacterUpdateDTO characterDTO);

    void deleteCharacter (UUID id);    
}