package FightLeagueKO.combo.mapper;

import org.springframework.stereotype.Component;

import FightLeagueKO.combo.dto.ComboDTO;
import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.combo.model.Combo;

@Component
public class ComboMapper {

    public ComboDTO toDTO(Combo combo) {
        return new ComboDTO(
            combo.getId(),
            combo.getTitle(),
            combo.getTextNotation(),
            combo.getComboDificulty().name(),
            combo.getFuse().name(),
            combo.getMediaUrl(),
            combo.getDescription(),
            combo.getMeterCost(),
            combo.getDamage()
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