
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
    public ResponseEntity<Combo> getComboById(@PathVariable UUID comboId){
        return ResponseEntity.ok(comboService.getComboById(comboId));
    }

    @PostMapping(value = "/search")
    public ResponseEntity<List<Combo>> searchCombos(@RequestBody ComboFiltersDTO filters){
        return ResponseEntity.ok(comboService.searchCombos(filters));
    }

    @PostMapping
    public ResponseEntity<Combo> createCombo(@RequestBody ComboCreateDTO comboCreateDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(comboService.createCombo(comboCreateDTO));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCombo(
        @PathVariable UUID id, 
        @RequestBody  ComboUpdateDTO comboUpdateDTO){
        comboService.updateCombo(id, comboUpdateDTO);
    }


    @PatchMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteComb(@PathVariable UUID comboId)
    {
        comboService.softDeleteCombo(comboId);
    }

    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreCombo(@PathVariable UUID comboId)
    {
        comboService.restoreCombo(comboId);
    }

    @PatchMapping("{id}/public")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setComboPublic(@PathVariable UUID comboId){
        comboService.setComboPublic(comboId);
    }

    @PatchMapping("{id}/private")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setComboPrivate(@PathVariable UUID comboId){
        comboService.setComboPrivate(comboId);
    }

}
