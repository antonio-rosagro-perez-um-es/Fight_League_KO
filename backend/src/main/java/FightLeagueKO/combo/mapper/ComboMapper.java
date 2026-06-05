package FightLeagueKO.combo.mapper;

import org.springframework.stereotype.Component;

import FightLeagueKO.combo.dto.ComboDTO;
import FightLeagueKO.combo.dto.OfficialComboDTO;
import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.combo.model.Combo;

@Component
public class ComboMapper {

    public ComboDTO toDTO(Combo combo, String pointFighterName, String pointFighterSlug,
                          String secondFighterName, String secondFighterSlug) {
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
            combo.getPointFighterId(),
            pointFighterName,
            pointFighterSlug,
            combo.getSecondFighterId(),
            secondFighterName,
            secondFighterSlug
        );
    }

    public OfficialComboDTO toOfficialDTO(Combo combo, String pointFighterName, String pointFighterSlug,
                                         String secondFighterName, String secondFighterSlug) {
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
            combo.getPointFighterId(),
            pointFighterName,
            pointFighterSlug,
            combo.getSecondFighterId(),
            secondFighterName,
            secondFighterSlug
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
