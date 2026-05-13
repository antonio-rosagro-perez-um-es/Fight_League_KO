package FightLeagueKO.combo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import FightLeagueKO.combo.dto.ComboUpdateDTO;
import FightLeagueKO.combo.dto.ComboCreateDTO;
import FightLeagueKO.combo.dto.ComboFiltersDTO;
import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.combo.model.Combo;
import FightLeagueKO.combo.repository.ComboRepositoryPostgre;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ComboService implements IComboService {

    private ComboRepositoryPostgre comboRepository;

    @Autowired
    public ComboService(ComboRepositoryPostgre comboRepository) {
        this.comboRepository = comboRepository;
    }

    @Override
    public Combo getComboById(UUID id) {

        Objects.requireNonNull(id, "Parameter id for combo could not be null");

        return comboRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found with id: " + id));
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

        if (comboDTO.pointCharacter() == null) {
            throw new IllegalArgumentException("Point character cant be null");
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

        combo.setTitle(comboDTO.title());
        combo.setDeleted(false);
        combo.setPointCharacterId(comboDTO.pointCharacter());
        combo.setSecondCharacterId(comboDTO.secondCharacter());
        combo.setTextNotation(comboDTO.textNotation());
        combo.setComboDificulty(comboDTO.comboDificulty());
        combo.setFuse(comboDTO.fuse());
        combo.setMediaUrl(comboDTO.mediaUrl());
        combo.setDescription(comboDTO.description());
        combo.setMeterCost(comboDTO.metercost() != null ? comboDTO.metercost() : 0);
        combo.setDamage(comboDTO.damage() != null ? comboDTO.damage() : 0);
        combo.setCreatedAt(LocalDate.now());
        combo.setUpDateAt(LocalDate.now());
        // TODO: el combo sera marcado como oficial o no en funcion del usuario que lo
        // crea

        return comboRepository.save(combo);
    }

    @Override
    public void updateCombo(UUID id, ComboUpdateDTO comboDTO) {

        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + id));

         Optional.ofNullable(comboDTO.title())
                .ifPresent(combo::setTitle);

        Optional.ofNullable(comboDTO.pointCharacter())
                .ifPresent(combo::setPointCharacterId);

        Optional.ofNullable(comboDTO.secondCharacter())
                .ifPresent(combo::setSecondCharacterId);

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
    public void softDeleteCombo(UUID id) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + id));

        combo.setDeleted(true);

        comboRepository.save(combo);
    }

    @Override
    public void restoreCombo(UUID id) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found exception with id: " + id));

        combo.setDeleted(false);

        comboRepository.save(combo);
    }

    private Specification<Combo> buildFilters(ComboFiltersDTO filters) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.characterPointId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pointCharacterId"), filters.characterPointId()));
            }

            if (filters.characterSecondId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("secondCharacterId"), filters.characterSecondId()));
            }

            if (filters.oficial() != null) {
                predicates.add(criteriaBuilder.equal(root.get("oficial"), filters.oficial()));
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

}

