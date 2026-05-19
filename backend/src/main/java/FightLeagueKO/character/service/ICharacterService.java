package FightLeagueKO.character.service;

import java.util.List;
import java.util.UUID;
import FightLeagueKO.character.model.Character;

import FightLeagueKO.character.dto.CharacterBannerDTO;
import FightLeagueKO.character.dto.CharacterDetailDTO;
import FightLeagueKO.character.dto.CharacterUpdateDTO;
import FightLeagueKO.character.dto.CreateCharacterDTO;

public interface ICharacterService {

    List<Character> getAllCharacters();

    List<CharacterBannerDTO> getAllCharactersBanner();

    Character getCharacterById(UUID CharacterId);

    CharacterDetailDTO getCharacterWithOfficialCombos(UUID CharacterId);

    Character createCharacter(CreateCharacterDTO characterDTO);

    void updateCharacter (UUID CharacterId, CharacterUpdateDTO characterDTO);

    void softDeleteCharacter (UUID CharacterId);

    void restoreCharacter (UUID CharacterId);
}