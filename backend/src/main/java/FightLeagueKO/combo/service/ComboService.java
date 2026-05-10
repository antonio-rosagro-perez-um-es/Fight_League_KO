package FightLeagueKO.combo.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import FightLeagueKO.combo.model.Combo;
import FightLeagueKO.combo.repository.ComboRespository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ComboService implements IComboService {

    private ComboRespository comboRespository;

    @Autowired
    public ComboService(ComboRespository comboRespository) {
        this.comboRespository = comboRespository;
    }

    @Override
    public Combo getComboById(UUID id) {

        Objects.requireNonNull(id, "Parameter id for combo could not be null");

        return comboRespository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Combo not found with id: " + id));
    }

}
