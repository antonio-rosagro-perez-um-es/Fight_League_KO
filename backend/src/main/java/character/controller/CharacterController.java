package character.controller;

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

import character.dto.CharacterBannerDTO;
import character.dto.CharacterUpdateDTO;
import character.dto.NewCharacterDTO;
import character.model.Character;
import character.services.ICharacterService;

@RestController
@RequestMapping("/characters")
public class CharacterController {

    private ICharacterService charactersService;

    @Autowired
    public CharacterController(ICharacterService charactersService) {
        this.charactersService = charactersService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Character> getCharacterById(@PathVariable UUID id) {
        return ResponseEntity.ok(charactersService.getCharacterById(id));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<CharacterBannerDTO>> getAllCharacterById() {
        return ResponseEntity.ok(charactersService.getAllCharactersBanner());
    }

    @PostMapping(value = "/newCharacter")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createCharacter(@RequestBody NewCharacterDTO characterDTO)
    {
        charactersService.createCharacter(characterDTO);
    }


    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCharacter(
            @PathVariable UUID id,
            @Validated @RequestBody CharacterUpdateDTO dto) {
        charactersService.updateCharacter(id, dto);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCharacter(
            @PathVariable UUID id,
            @Validated @RequestBody CharacterUpdateDTO dto) {
        charactersService.deleteCharacter(id);
    }

}
