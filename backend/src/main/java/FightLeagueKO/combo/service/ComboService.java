package FightLeagueKO.combo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import FightLeagueKO.combo.dto.ComboUpdateDTO;
import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.service.FighterService;
import FightLeagueKO.combo.dto.ComboCreateDTO;
import FightLeagueKO.combo.dto.ComboFiltersDTO;
import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.combo.model.Combo;
import FightLeagueKO.combo.repository.ComboRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ComboService implements IComboService {

    private ComboRepository comboRepository;
    private FighterService fighterService;

    @Autowired
    public ComboService(ComboRepository comboRepository, FighterService fighterService) {
        this.comboRepository = comboRepository;
        this.fighterService = fighterService;
    }

    @Override
    public Combo getComboById(UUID comboId) {

        Objects.requireNonNull(comboId, "Parameter comboId for combo could not be null");

        return comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found with Id: " + comboId));
    }

    @Override
    public List<Combo> getAllCombo(){
         return StreamSupport.stream(comboRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Combo> searchCombos(ComboFiltersDTO filters) {

        if (filters == null) {
            return comboRepository.findAll();
        }

        Sort sort = filters.latest() == null
                ? Sort.unsorted()
                : Sort.by(filters.latest() ? Sort.Direction.DESC : Sort.Direction.ASC, "createdAt");

        return comboRepository.findAll(buildFilters(filters), sort);
    }

    @Override
    public Combo createCombo(ComboCreateDTO comboDTO) {

        Objects.requireNonNull(comboDTO, "Parameters could not be null");

        Combo combo = new Combo();

        if (comboDTO.title() == null || comboDTO.title().isEmpty()) {
            throw new IllegalArgumentException("Title cant be null");
        }

        if (comboDTO.pointFighter() == null) {
            throw new IllegalArgumentException("Point fighter cant be null");
        }

        if (comboDTO.textNotation() == null || comboDTO.textNotation().trim().isEmpty()) {
            throw new IllegalArgumentException("Text notation cant be null or empty");
        }

        if (comboDTO.comboDificulty() == null) {
            throw new IllegalArgumentException("Combo difficulty cant be null");
        }

        if (comboDTO.fuse() == null) {
            throw new IllegalArgumentException("Fuse cant be null");
        }

        if (comboDTO.mediaUrl() == null || comboDTO.mediaUrl().isEmpty()) {
            throw new IllegalArgumentException("Media URL cant be null or empty");
        }

        if (comboDTO.description() == null || comboDTO.description().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cant be null or empty");
        }

        Fighter secondFighter = new Fighter();
        if (comboDTO.secondFighter() != null)
            secondFighter = fighterService.getFighterById(comboDTO.secondFighter());

        Fighter pointFighter = fighterService.getFighterById(comboDTO.pointFighter());

        combo.setTitle(comboDTO.title());
        combo.setDeleted(false);
        combo.setPointFighter(pointFighter);
        combo.setSecondFighter(secondFighter);
        combo.setTextNotation(comboDTO.textNotation());
        combo.setComboDificulty(comboDTO.comboDificulty());
        combo.setFuse(comboDTO.fuse());
        combo.setMediaUrl(comboDTO.mediaUrl());
        combo.setDescription(comboDTO.description());
        combo.setMeterCost(comboDTO.metercost() != null ? comboDTO.metercost() : 0);
        combo.setDamage(comboDTO.damage() != null ? comboDTO.damage() : 0);
        combo.setCreatedAt(LocalDate.now());
        combo.setUpDateAt(LocalDate.now());
        combo.setPrivateCombo(true);
        // TODO: el combo sera marcado como oficial o no en funcion del usuario que lo
        combo.setLikeCounter(0);
        combo.setDislikeCounter(0);
        

        return comboRepository.save(combo);
    }

    @Override
    public void updateCombo(UUID comboId, ComboUpdateDTO comboDTO) {

        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with Id: " + comboId));

        Optional.ofNullable(comboDTO.title())
                .ifPresent(combo::setTitle);

        Optional.ofNullable(comboDTO.pointFighter())
                .map(fighterService::getFighterById)
                .ifPresent(combo::setPointFighter);

        Optional.ofNullable(comboDTO.secondFighter())
                .map(fighterService::getFighterById)
                .ifPresent(combo::setSecondFighter);

        Optional.ofNullable(comboDTO.textNotation())
                .ifPresent(combo::setTextNotation);

        Optional.ofNullable(comboDTO.comboDificulty())
                .ifPresent(combo::setComboDificulty);

        Optional.ofNullable(comboDTO.fuse())
                .ifPresent(combo::setFuse);

        Optional.ofNullable(comboDTO.mediaUrl())
                .ifPresent(combo::setMediaUrl);

        Optional.ofNullable(comboDTO.description())
                .ifPresent(combo::setDescription);

        Optional.ofNullable(comboDTO.metercost())
                .ifPresent(combo::setMeterCost);

        Optional.ofNullable(comboDTO.damage())
                .ifPresent(combo::setDamage);

        combo.setUpDateAt(LocalDate.now());

        comboRepository.save(combo);
    }

    @Override
    public void softDeleteCombo(UUID comboId) {
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + comboId));

        combo.setDeleted(true);

        comboRepository.save(combo);
    }

    @Override
    public void restoreCombo(UUID comboId) {
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + comboId));

        combo.setDeleted(false);

        comboRepository.save(combo);
    }

    private Specification<Combo> buildFilters(ComboFiltersDTO filters) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("oficial"), false));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("privateCombo"), false));

            if (filters.pointFighterId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pointFighterId"), filters.pointFighterId()));
            }

            if (filters.secondFighterId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("secondFighterId"), filters.secondFighterId()));
            }

            if (hasText(filters.comboDificulty())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("comboDificulty"),
                        ComboDificulty.valueOf(filters.comboDificulty().trim().toUpperCase())));
            }

            if (hasText(filters.fuse())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("fuse"),
                        FuseType.valueOf(filters.fuse().trim().toUpperCase())));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Override
    public void setComboPublic(UUID comboId) {
         Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + comboId));

        combo.setPrivateCombo(false);

        comboRepository.save(combo);
    }

    @Override
    public void setComboPrivate(UUID comboId) {
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + comboId));

        combo.setPrivateCombo(true);

        comboRepository.save(combo);
    }

    @Override
    public void addLikeCombo(UUID comboId) {
       Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + comboId));

        combo.addLikeCombo();

        comboRepository.save(combo);
    }

    @Override
    public void addDislikeCombo(UUID comboId) {
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + comboId));

        combo.addDislikeCombo();

        comboRepository.save(combo);
    }

    @Override
    public void removeLikeCombo(UUID comboId) {
       Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + comboId));

        combo.removeLikeCombo();

        comboRepository.save(combo);
    }

    @Override
    public void removeDislikeCombo(UUID comboId) {
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + comboId));

        combo.removeDislikeCombo();

        comboRepository.save(combo);
    }

}
