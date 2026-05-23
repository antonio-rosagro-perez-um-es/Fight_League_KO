package FightLeagueKO.combo.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import FightLeagueKO.combo.dto.ComboDTO;
import FightLeagueKO.combo.dto.OfficialComboDTO;
import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.combo.model.Combo;
import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.repository.FighterRepositoryPostgre;

@Component
public class ComboMapper {

    private final FighterRepositoryPostgre fighterRepository;

    public ComboMapper(FighterRepositoryPostgre fighterRepository) {
        this.fighterRepository = fighterRepository;
    }

    private String resolveFighterName(UUID fighterId) {
        if (fighterId == null) return null;
        return fighterRepository.findById(fighterId)
                .map(Fighter::getName)
                .orElse(null);
    }

    private String resolveFighterSlug(UUID fighterId) {
        if (fighterId == null) return null;
        return fighterRepository.findById(fighterId)
                .map(Fighter::getSlug)
                .orElse(null);
    }

    public ComboDTO toDTO(Combo combo) {
        UUID pointFighterId = combo.getPointFighterId();
        UUID secondFighterId = combo.getSecondFighterId();

        return new ComboDTO(
            combo.getId(),
            combo.getTitle(),
            combo.getTextNotation(),
            combo.getComboDificulty().name(),
            combo.getFuse().name(),
            combo.getMediaUrl(),
            combo.getDescription(),
            combo.getMeterCost(),
            combo.getDamage(),
            combo.isOficial(),
            combo.isPrivateCombo(),
            combo.isDeleted(),
            combo.getCreatedAt(),
            combo.getUpDateAt(),
            combo.getLikeCounter(),
            combo.getDislikeCounter(),
            combo.getCreatorUserId(),
            pointFighterId,
            resolveFighterName(pointFighterId),
            resolveFighterSlug(pointFighterId),
            secondFighterId,
            resolveFighterName(secondFighterId),
            resolveFighterSlug(secondFighterId)
        );
    }

        public OfficialComboDTO toOficialDTO(Combo combo) {
        UUID pointFighterId = combo.getPointFighterId();
        UUID secondFighterId = combo.getSecondFighterId();

        return new OfficialComboDTO(
            combo.getId(),
            combo.getTitle(),
            combo.getTextNotation(),
            combo.getComboDificulty().name(),
            combo.getFuse().name(),
            combo.getMediaUrl(),
            combo.getDescription(),
            combo.getMeterCost(),
            combo.getDamage(),
            pointFighterId,
            resolveFighterName(pointFighterId),
            resolveFighterSlug(pointFighterId),
            secondFighterId,
            resolveFighterName(secondFighterId),
            resolveFighterSlug(secondFighterId)
        );
    }

    public Combo toEntity(ComboDTO dto) {
        Combo combo = new Combo();
        if (dto.id() != null) {
            combo.setId(dto.id());
        }
        combo.setTitle(dto.title());
        combo.setTextNotation(dto.textNotation());
        combo.setMediaUrl(dto.mediaUrl());
        combo.setDescription(dto.description());
        combo.setMeterCost(dto.meterCost());
        combo.setDamage(dto.damage());
        if (dto.comboDificulty() != null) {
            combo.setComboDificulty(ComboDificulty.valueOf(dto.comboDificulty()));
        }
        if (dto.fuse() != null) {
            combo.setFuse(FuseType.valueOf(dto.fuse()));
        }
        return combo;
    }
}
