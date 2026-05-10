package FightLeagueKO.combo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
} 
