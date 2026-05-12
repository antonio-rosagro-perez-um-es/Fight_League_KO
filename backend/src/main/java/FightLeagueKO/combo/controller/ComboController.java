
package FightLeagueKO.combo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import FightLeagueKO.combo.dto.ComboUpdateDTO;
import FightLeagueKO.combo.dto.ComboCreateDTO;
import FightLeagueKO.combo.dto.ComboFiltersDTO;
import FightLeagueKO.combo.model.Combo;
import FightLeagueKO.combo.service.IComboService;

@RestController
@RequestMapping("/combos")
public class ComboController {

    private IComboService comboService;

    @Autowired
    public ComboController(IComboService comboService){
        this.comboService = comboService;
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<Combo> getComboById(@PathVariable UUID id){
        return ResponseEntity.ok(comboService.getComboById(id));
    }

    @PostMapping(value = "/search")
    public ResponseEntity<List<Combo>> searchCombos(@RequestBody ComboFiltersDTO filters){
        return ResponseEntity.ok(comboService.searchCombos(filters));
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Combo> createCombo(@RequestBody ComboCreateDTO comboCreateDTO){
        return ResponseEntity.ok(comboService.createCombo(comboCreateDTO));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCombo(
        @PathVariable UUID id, 
        @Validated @RequestBody  ComboUpdateDTO comboUpdateDTO){
        comboService.updateCombo(id, comboUpdateDTO);
    }


    
}
