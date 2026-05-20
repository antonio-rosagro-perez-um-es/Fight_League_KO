package FightLeagueKO.combo.service;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.combo.dto.ComboUpdateDTO;
import FightLeagueKO.combo.dto.ComboCreateDTO;
import FightLeagueKO.combo.dto.ComboFiltersDTO;
import FightLeagueKO.combo.model.Combo;

public interface IComboService {

    Combo getComboById(UUID comboId);

    List<Combo> getAllCombo();

    List<Combo> searchCombos(ComboFiltersDTO filters);

    Combo createCombo(ComboCreateDTO comboDTO);

    void updateCombo(UUID comboId, ComboUpdateDTO comboDTO);

    void softDeleteCombo(UUID comboId);

    void restoreCombo(UUID comboId);

    void setComboPublic(UUID comboId);

    void setComboPrivate(UUID comboId);

    void addLikeCombo(UUID comboId);

    void addDislikeCombo(UUID comboId);

    void removeLikeCombo(UUID comboId);

    void removeDislikeCombo(UUID comboId);
}
