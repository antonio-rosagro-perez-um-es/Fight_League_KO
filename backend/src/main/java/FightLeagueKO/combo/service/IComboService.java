package FightLeagueKO.combo.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.combo.dto.ComboUpdateDTO;
import FightLeagueKO.combo.dto.ComboCreateDTO;
import FightLeagueKO.combo.dto.ComboFiltersDTO;
import FightLeagueKO.combo.model.Combo;

public interface IComboService {

    public Combo getComboById(UUID id);

    public List<Combo> searchCombos(ComboFiltersDTO filters);

    public Combo createCombo(ComboCreateDTO comboDTO);

    public void updateCombo(UUID id, ComboUpdateDTO comboDTO);

    public void softDeleteCombo(UUID id);

    public void restoreCombo(UUID id);
}
