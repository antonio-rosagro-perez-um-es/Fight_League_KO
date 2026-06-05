package FightLeagueKO.fighter.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import FightLeagueKO.fighter.dto.FighterBannerDTO;
import FightLeagueKO.fighter.dto.FighterDTO;
import FightLeagueKO.fighter.dto.FighterStatsDTO;
import FightLeagueKO.fighter.dto.FighterUpdateDTO;
import FightLeagueKO.fighter.mapper.FighterMapper;
import FightLeagueKO.fighter.dto.CreateFighterDTO;
import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.repository.FighterRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class FighterService implements IFighterService {

    private FighterRepository fighterRepository;
    private FighterMapper fighterMapper;

    @Autowired
    public FighterService(FighterRepository fighterRepository, FighterMapper fighterMapper) {
        this.fighterRepository = fighterRepository;
        this.fighterMapper = fighterMapper;
    }

    @Override
    public List<FighterDTO> getAllFighters() {

        return StreamSupport.stream(fighterRepository.findAll().spliterator(), false)
                .map(fighterMapper::toFighterDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FighterBannerDTO> getAllFightersBanner() {

        return fighterRepository.findAllBannerFighters();
    }

    @Override
    public FighterDTO getFighterDTOById(UUID fighterId) {

        Objects.requireNonNull(fighterId, "Parameter id could not be null");

        Fighter fighter = fighterRepository.findById(fighterId)
                .orElseThrow(() -> new EntityNotFoundException("Fighter not found with id: " + fighterId));

        return fighterMapper.toFighterDTO(fighter);
    }

    @Override
    public Fighter getFighterById(UUID fighterId) {

        Objects.requireNonNull(fighterId, "Parameter id could not be null");

        return fighterRepository.findById(fighterId)
                .orElseThrow(() -> new EntityNotFoundException("Fighter not found with id: " + fighterId));

    }

    @Override
    public FighterDTO createFighter(CreateFighterDTO fighterDTO) {

        Objects.requireNonNull(fighterDTO, "Parameter fighterDTO could not be null");

        Fighter fighter = new Fighter();

        if (fighterDTO.name() == null || fighterDTO.name().isEmpty()) {
            throw new IllegalArgumentException("Fighter name could not be null or empty");
        }

        if (fighterDTO.description() == null || fighterDTO.description().isEmpty()) {
            throw new IllegalArgumentException("Fighter description could not be null or empty");
        }

        if (fighterDTO.region() == null || fighterDTO.region().isEmpty()) {
            throw new IllegalArgumentException("Fighter region could not be null or empty");
        }

        if (fighterDTO.archetype() == null || fighterDTO.archetype().isEmpty()) {
            throw new IllegalArgumentException("Fighter archetype could not be null or empty");
        }

        if (fighterDTO.title() == null || fighterDTO.title().isEmpty()) {
            throw new IllegalArgumentException("Fighter title could not be null or empty");
        }

        if (fighterDTO.itLikes() == null || fighterDTO.itLikes().isEmpty()) {
            throw new IllegalArgumentException("Fighter itLikes could not be null or empty");
        }

        if (fighterDTO.itDislike() == null || fighterDTO.itDislike().isEmpty()) {
            throw new IllegalArgumentException("Fighter itDislike could not be null or empty");
        }

        if (fighterDTO.slug() == null || fighterDTO.slug().isEmpty()) {
            throw new IllegalArgumentException("Fighter slug could not be null or empty");
        }

        if (fighterDTO.health() == null) {
            throw new IllegalArgumentException("Fighter health could not be null");
        }

        if (fighterDTO.range() == null) {
            throw new IllegalArgumentException("Fighter range could not be null");
        }

        if (fighterDTO.power() == null) {
            throw new IllegalArgumentException("Fighter power could not be null");
        }

        if (fighterDTO.vitality() == null) {
            throw new IllegalArgumentException("Fighter vitality could not be null");
        }

        if (fighterDTO.mobility() == null) {
            throw new IllegalArgumentException("Fighter mobility could not be null");
        }

        if (fighterDTO.easyOfUse() == null) {
            throw new IllegalArgumentException("Fighter easyOfUse could not be null");
        }

        fighter.setName(fighterDTO.name());
        fighter.setDescription(fighterDTO.description());
        fighter.setRegion(fighterDTO.region());
        fighter.setArchetype(fighterDTO.archetype());
        fighter.setTitle(fighterDTO.title());
        fighter.setItLikes(fighterDTO.itLikes());
        fighter.setItDislike(fighterDTO.itDislike());
        fighter.setSlug(fighterDTO.slug());
        fighter.setDeleted(false);
        fighter.setHealth(fighterDTO.health());
        fighter.setRange(fighterDTO.range());
        fighter.setPower(fighterDTO.power());
        fighter.setVitality(fighterDTO.vitality());
        fighter.setMobility(fighterDTO.mobility());
        fighter.setEasyOfUse(fighterDTO.easyOfUse());

        Fighter saved = fighterRepository.save(fighter);
        return fighterMapper.toFighterDTO(saved);
    }

    @Override
    public void updateFighter(UUID fighterId, FighterUpdateDTO fighterDTO) {

        Fighter fighter = fighterRepository.findById(fighterId)
                .orElseThrow(() -> new EntityNotFoundException("Fighter not found with id:" + fighterId));

        Optional.ofNullable(fighterDTO.name())
                .ifPresent(fighter::setName);

        Optional.ofNullable(fighterDTO.description())
                .ifPresent(fighter::setDescription);

        Optional.ofNullable(fighterDTO.region())
                .ifPresent(fighter::setRegion);

        Optional.ofNullable(fighterDTO.archetype())
                .ifPresent(fighter::setArchetype);

        Optional.ofNullable(fighterDTO.title())
                .ifPresent(fighter::setTitle);

        Optional.ofNullable(fighterDTO.itLikes())
                .ifPresent(fighter::setItLikes);

        Optional.ofNullable(fighterDTO.itDislike())
                .ifPresent(fighter::setItDislike);

        Optional.ofNullable(fighterDTO.slug())
                .ifPresent(fighter::setSlug);

        Optional.ofNullable(fighterDTO.health())
                .ifPresent(fighter::setHealth);

        Optional.ofNullable(fighterDTO.range())
                .ifPresent(fighter::setRange);

        Optional.ofNullable(fighterDTO.power())
                .ifPresent(fighter::setPower);

        Optional.ofNullable(fighterDTO.vitality())
                .ifPresent(fighter::setVitality);

        Optional.ofNullable(fighterDTO.mobility())
                .ifPresent(fighter::setMobility);

        Optional.ofNullable(fighterDTO.easyOfUse())
                .ifPresent(fighter::setEasyOfUse);

        fighterRepository.save(fighter);
    }

    @Override
    public void softDeleteFighter(UUID fighterId) {

        Fighter fighter = fighterRepository.findById(fighterId)
                .orElseThrow(() -> new EntityNotFoundException("Fighter not found with id: " + fighterId));

        fighter.setDeleted(true);

        fighterRepository.save(fighter);
    }

    @Override
    public void restoreFighter(UUID fighterId) {
        Fighter fighter = fighterRepository.findById(fighterId)
                .orElseThrow(() -> new EntityNotFoundException("Fighter not found with id: " + fighterId));

        fighter.setDeleted(false);

        fighterRepository.save(fighter);
    }

    @Override
    public void updateFighterStats(UUID fighterId, boolean isWinner) {

        Fighter fighter = fighterRepository.findById(fighterId)
                .orElseThrow(() -> new EntityNotFoundException("Fighter not found with id:" + fighterId));

        fighter.addPlayCounter();

        if (isWinner == true)
            fighter.addWinCounter();
        else
            fighter.addLoseCounter();

        fighterRepository.save(fighter);
    }

    @Override
    public void revertFighterStats(UUID fighterId, boolean wasWinner) {
        Fighter fighter = fighterRepository.findById(fighterId)
                .orElseThrow(() -> new EntityNotFoundException("Fighter not found with id:" + fighterId));

        fighter.removePlayCounter();

        if (wasWinner) {
            fighter.removeWinCounter();
        } else {
            fighter.removeLoseCounter();
        }

        fighterRepository.save(fighter);
    }

    @Override
    public FighterStatsDTO getFighterStats(UUID fighterId) {

        Fighter fighter = fighterRepository.findById(fighterId)
                .orElseThrow(() -> new EntityNotFoundException("Fighter not found with id:" + fighterId));

        long allFightersPlayRate = fighterRepository.getAllFightersPlayRate();

        double playRate = allFightersPlayRate > 0
                ? fighter.getPlayCounter() * 100.0 / allFightersPlayRate
                : 0.0;

        return fighterMapper.toFighterStatsDTO(fighter, playRate);
    }

    @Override
    public List<FighterStatsDTO> getFightersRanking() {
        List<Fighter> fighters = fighterRepository.getAllActiveFightersWithPlays();
        long allFightersPlayRate = fighterRepository.getAllFightersPlayRate();
        return fighters.stream()
                .sorted((a, b) -> Double.compare(b.getWinRate(), a.getWinRate()))
                .limit(10)
                .map(fighter -> {
                    double playRate = allFightersPlayRate > 0
                            ? fighter.getPlayCounter() * 100.0 / allFightersPlayRate
                            : 0.0;
                    return fighterMapper.toFighterStatsDTO(fighter, playRate);
                })
                .collect(Collectors.toList());
    }

}
