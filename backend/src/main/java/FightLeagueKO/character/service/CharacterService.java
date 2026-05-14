package FightLeagueKO.character.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import FightLeagueKO.character.dto.CharacterBannerDTO;
import FightLeagueKO.character.dto.CharacterUpdateDTO;
import FightLeagueKO.character.dto.NewCharacterDTO;
import FightLeagueKO.character.model.Character;
import FightLeagueKO.character.repository.CharacterRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CharacterService implements ICharacterService {

    private CharacterRepository characterRepository;

    @Autowired
    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Override
    public List<CharacterBannerDTO> getAllCharactersBanner() {

        return characterRepository.findAllBannerCharacters();
    }

    @Override
    public Character getCharacterById(UUID id) {

        Objects.requireNonNull(
                id,
                "Parameter id could not be null");

        return characterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Character not found with id: " + id));
    }

    @Override
    public Character createCharacter(NewCharacterDTO characterDTO) {

        Objects.requireNonNull(characterDTO, "Parameter characterDTO could not be null");

        Character character = new Character();

        if (characterDTO.name() == null || characterDTO.name().isEmpty()) {
            throw new IllegalArgumentException("Character name could not be null or empty");
        }

        if (characterDTO.description() == null || characterDTO.description().isEmpty()) {
            throw new IllegalArgumentException("Character description could not be null or empty");
        }

        if (characterDTO.region() == null || characterDTO.region().isEmpty()) {
            throw new IllegalArgumentException("Character region could not be null or empty");
        }

        if (characterDTO.archetype() == null || characterDTO.archetype().isEmpty()) {
            throw new IllegalArgumentException("Character archetype could not be null or empty");
        }

        if (characterDTO.title() == null || characterDTO.title().isEmpty()) {
            throw new IllegalArgumentException("Character title could not be null or empty");
        }

        if (characterDTO.itLikes() == null || characterDTO.itLikes().isEmpty()) {
            throw new IllegalArgumentException("Character itLikes could not be null or empty");
        }

        if (characterDTO.itDislike() == null || characterDTO.itDislike().isEmpty()) {
            throw new IllegalArgumentException("Character itDislike could not be null or empty");
        }

        if (characterDTO.slug() == null || characterDTO.slug().isEmpty()) {
            throw new IllegalArgumentException("Character slug could not be null or empty");
        }

        if (characterDTO.health() == null) {
            throw new IllegalArgumentException("Character health could not be null");
        }

        if (characterDTO.range() == null) {
            throw new IllegalArgumentException("Character range could not be null");
        }

        if (characterDTO.power() == null) {
            throw new IllegalArgumentException("Character power could not be null");
        }

        if (characterDTO.vitality() == null) {
            throw new IllegalArgumentException("Character vitality could not be null");
        }

        if (characterDTO.mobility() == null) {
            throw new IllegalArgumentException("Character mobility could not be null");
        }

        if (characterDTO.easyOfUse() == null) {
            throw new IllegalArgumentException("Character easyOfUse could not be null");
        }

        character.setName(characterDTO.name());
        character.setDescription(characterDTO.description());
        character.setRegion(characterDTO.region());
        character.setArchetype(characterDTO.archetype());
        character.setTitle(characterDTO.title());
        character.setItLikes(characterDTO.itLikes());
        character.setItDislike(characterDTO.itDislike());
        character.setSlug(characterDTO.slug());
        character.setDeleted(false);
        character.setHealth(characterDTO.health());
        character.setRange(characterDTO.range());
        character.setPower(characterDTO.power());
        character.setVitality(characterDTO.vitality());
        character.setMobility(characterDTO.mobility());
        character.setEasyOfUse(characterDTO.easyOfUse());

        return characterRepository.save(character);
    }

    @Override
    public void updateCharacter(UUID id, CharacterUpdateDTO characterDTO) {

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Character not found with id:" + id));

        Optional.ofNullable(characterDTO.name())
                .ifPresent(character::setName);

        Optional.ofNullable(characterDTO.description())
                .ifPresent(character::setDescription);

        Optional.ofNullable(characterDTO.region())
                .ifPresent(character::setRegion);

        Optional.ofNullable(characterDTO.archetype())
                .ifPresent(character::setArchetype);

        Optional.ofNullable(characterDTO.title())
                .ifPresent(character::setTitle);

        Optional.ofNullable(characterDTO.itLikes())
                .ifPresent(character::setItLikes);

        Optional.ofNullable(characterDTO.itDislike())
                .ifPresent(character::setItDislike);

        Optional.ofNullable(characterDTO.slug())
                .ifPresent(character::setSlug);

        Optional.ofNullable(characterDTO.health())
                .ifPresent(character::setHealth);

        Optional.ofNullable(characterDTO.range())
                .ifPresent(character::setRange);

        Optional.ofNullable(characterDTO.power())
                .ifPresent(character::setPower);

        Optional.ofNullable(characterDTO.vitality())
                .ifPresent(character::setVitality);

        Optional.ofNullable(characterDTO.mobility())
                .ifPresent(character::setMobility);

        Optional.ofNullable(characterDTO.easyOfUse())
                .ifPresent(character::setEasyOfUse);

        characterRepository.save(character);
    }

    @Override
    public void softDeleteCharacter(UUID id) {

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Character not found with id: " + id));

        character.setDeleted(true);

        characterRepository.save(character);
    }

    @Override
    public void restoreCharacter(UUID id) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Character not found with id: " + id));

        character.setDeleted(false);

        characterRepository.save(character);
    }

}