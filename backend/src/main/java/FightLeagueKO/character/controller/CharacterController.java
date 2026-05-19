package FightLeagueKO.character.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import FightLeagueKO.character.dto.CharacterBannerDTO;
import FightLeagueKO.character.dto.CharacterDetailDTO;
import FightLeagueKO.character.dto.CharacterUpdateDTO;
import FightLeagueKO.character.dto.CreateCharacterDTO;
import FightLeagueKO.character.model.Character;
import FightLeagueKO.character.service.ICharacterService;

@RestController
@RequestMapping("/characters")
public class CharacterController {

    private ICharacterService charactersService;

    @Autowired
    public CharacterController(ICharacterService charactersService) {
        this.charactersService = charactersService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Character> getCharacterById(@PathVariable UUID characterId) {
        return ResponseEntity.ok(charactersService.getCharacterById(characterId));
    }

    @GetMapping(value = "/{id}/official-combos")
    public ResponseEntity<CharacterDetailDTO> getCharacterWithOfficialCombos(@PathVariable UUID characterId) {
        return ResponseEntity.ok(charactersService.getCharacterWithOfficialCombos(characterId));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<CharacterBannerDTO>> getAllCharacterById() {
        return ResponseEntity.ok(charactersService.getAllCharactersBanner());
    }

    @PostMapping
    public ResponseEntity<Character> createCharacter(
            @RequestBody @Validated CreateCharacterDTO characterDTO) {

        Character created = charactersService.createCharacter(characterDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCharacter(
            @PathVariable UUID characterId,
            @Validated @RequestBody CharacterUpdateDTO characterDTO) {
        charactersService.updateCharacter(characterId, characterDTO);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteCharacter(@PathVariable UUID characterId) {
        charactersService.softDeleteCharacter(characterId);
    }

    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreCharacter(@PathVariable UUID characterId) {
        charactersService.restoreCharacter(characterId);
    }

    @GetMapping(value = "/health")
    public ResponseEntity<Void> getHealth() {
        return ResponseEntity.ok().build();
    }

}