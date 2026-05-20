package FightLeagueKO.fighter.controller;

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

import FightLeagueKO.fighter.dto.FighterBannerDTO;
import FightLeagueKO.fighter.dto.FighterDetailDTO;
import FightLeagueKO.fighter.dto.FighterUpdateDTO;
import FightLeagueKO.fighter.dto.CreateFighterDTO;
import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.service.IFighterService;

@RestController
@RequestMapping("/fighters")
public class FighterController {

    private IFighterService fightersService;

    @Autowired
    public FighterController(IFighterService fightersService) {
        this.fightersService = fightersService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Fighter> getFighterById(@PathVariable UUID fighterId) {
        return ResponseEntity.ok(fightersService.getFighterById(fighterId));
    }

    @GetMapping(value = "/{id}/official-combos")
    public ResponseEntity<FighterDetailDTO> getFighterWithOfficialCombos(@PathVariable UUID fighterId) {
        return ResponseEntity.ok(fightersService.getFighterWithOfficialCombos(fighterId));
    }

    @GetMapping
    public ResponseEntity<List<Fighter>> getAllFighters() {
        return ResponseEntity.ok(fightersService.getAllFighters());
    }

    @GetMapping(value = "/all-banners")
    public ResponseEntity<List<FighterBannerDTO>> getAllActiveFighters() {
        return ResponseEntity.ok(fightersService.getAllFightersBanner());
    }

    @PostMapping
    public ResponseEntity<Fighter> createFighter(
            @RequestBody CreateFighterDTO fighterDTO) {

        Fighter created = fightersService.createFighter(fighterDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFighter(
            @PathVariable UUID fighterId,
            @RequestBody FighterUpdateDTO fighterDTO) {
        fightersService.updateFighter(fighterId, fighterDTO);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteFighter(@PathVariable UUID fighterId) {
        fightersService.softDeleteFighter(fighterId);
    }

    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreFighter(@PathVariable UUID fighterId) {
        fightersService.restoreFighter(fighterId);
    }

    @GetMapping("/{id}/win-rate")
    public ResponseEntity<Double> getFighterWinrate(@PathVariable UUID fighterId) {
        return ResponseEntity.ok(fightersService.getFighterWinRate(fighterId));
    }

    @GetMapping("/{id}/play-rate")
    public ResponseEntity<Double> getFighterPlayRate(@PathVariable UUID fighterId) {
        return ResponseEntity.ok(fightersService.getFighterPlayRate(fighterId));
    }

}