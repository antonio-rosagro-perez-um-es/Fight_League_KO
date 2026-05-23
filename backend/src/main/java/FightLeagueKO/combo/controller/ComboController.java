
package FightLeagueKO.combo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import FightLeagueKO.combo.dto.ComboCreateDTO;
import FightLeagueKO.combo.dto.ComboDTO;
import FightLeagueKO.combo.dto.ComboFiltersDTO;
import FightLeagueKO.combo.dto.ComboUpdateDTO;
import FightLeagueKO.combo.dto.OfficialComboDTO;
import FightLeagueKO.combo.service.IComboService;

@RestController
@RequestMapping("/combos")
public class ComboController {

    private IComboService comboService;

    @Autowired
    public ComboController(IComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping(value = "{comboId}")
    public ResponseEntity<ComboDTO> getComboById(@PathVariable UUID comboId) {
        return ResponseEntity.ok(comboService.getComboById(comboId));
    }

    @GetMapping("/{fighterId}/official")
    public ResponseEntity<List<OfficialComboDTO>> getOfficialCombos(@PathVariable UUID fighterId) {
        return ResponseEntity.ok(comboService.getOfficialCombosByFighter(fighterId));
    }

    @GetMapping
    public ResponseEntity<List<ComboDTO>> getAllCombos() {
        return ResponseEntity.ok(comboService.getAllCombo());
    }

    @PostMapping(value = "/search")
    public ResponseEntity<List<ComboDTO>> searchCombos(@RequestBody ComboFiltersDTO filters) {
        return ResponseEntity.ok(comboService.searchCombos(filters));
    }

    @PostMapping
    public ResponseEntity<ComboDTO> createCombo(@RequestBody ComboCreateDTO comboCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comboService.createCombo(comboCreateDTO));
    }

    @PatchMapping("/{comboId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCombo(@PathVariable UUID comboId, @RequestBody ComboUpdateDTO comboUpdateDTO) {
        comboService.updateCombo(comboId, comboUpdateDTO);
    }

    @PatchMapping("/{comboId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteComb(@PathVariable UUID comboId) {
        comboService.softDeleteCombo(comboId);
    }

    @PatchMapping("/{comboId}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreCombo(@PathVariable UUID comboId) {
        comboService.restoreCombo(comboId);
    }

    @PatchMapping("{comboId}/public")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setComboPublic(@PathVariable UUID comboId) {
        comboService.setComboPublic(comboId);
    }

    @PatchMapping("{comboId}/private")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setComboPrivate(@PathVariable UUID comboId) {
        comboService.setComboPrivate(comboId);
    }

    @PatchMapping("{comboId}/add-like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLikeCombo(@PathVariable UUID comboId) {
        comboService.addLikeCombo(comboId);
    }

    @PatchMapping("{comboId}/add-dislike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addDislikeCombo(@PathVariable UUID comboId) {
        comboService.addDislikeCombo(comboId);
    }

    @PatchMapping("{comboId}/remove-like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeCombo(@PathVariable UUID comboId) {
        comboService.removeLikeCombo(comboId);
    }

    @PatchMapping("{comboId}/remove-dislike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDislikeCombo(@PathVariable UUID comboId) {
        comboService.removeDislikeCombo(comboId);
    }

}
