package FightLeagueKO.combo.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.combo.dto.ComboUpdateDTO;
import FightLeagueKO.combo.dto.ComboCreateDTO;
import FightLeagueKO.combo.dto.ComboFiltersDTO;
import FightLeagueKO.combo.model.Combo;

public interface IComboService {

    public Combo getComboById(UUID comboId);

    public List<Combo> searchCombos(ComboFiltersDTO filters);

    public Combo createCombo(ComboCreateDTO comboDTO);

    public void updateCombo(UUID comboId, ComboUpdateDTO comboDTO);

    public void softDeleteCombo(UUID comboId);

    public void restoreCombo(UUID comboId);

    public void setComboPublic(UUID comboId);

    public void setComboPrivate(UUID comboId);
}
