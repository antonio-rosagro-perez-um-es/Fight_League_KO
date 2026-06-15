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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import FightLeagueKO.fighter.dto.FighterBannerDTO;
import FightLeagueKO.fighter.dto.FighterDTO;
import FightLeagueKO.fighter.dto.FighterStatsDTO;
import FightLeagueKO.fighter.dto.FighterUpdateDTO;
import FightLeagueKO.fighter.dto.CreateFighterDTO;
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
    public ResponseEntity<FighterDTO> getFighterById(@PathVariable UUID id) {
        return ResponseEntity.ok(fightersService.getFighterDTOById(id));
    }

    @GetMapping
    public ResponseEntity<List<FighterDTO>> getAllFighters() {
        return ResponseEntity.ok(fightersService.getAllFighters());
    }

    @GetMapping(value = "/all-banners")
    public ResponseEntity<List<FighterBannerDTO>> getAllActiveFighters() {
        return ResponseEntity.ok(fightersService.getAllFightersBanner());
    }

    @PostMapping
    public ResponseEntity<FighterDTO> createFighter(
            @RequestBody CreateFighterDTO fighterDTO) {

        FighterDTO created = fightersService.createFighter(fighterDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(value = "/{id}/media", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadFighterMedia(
            @PathVariable UUID id,
            @RequestParam(value = "portrait", required = false) MultipartFile portrait,
            @RequestParam(value = "banner", required = false) MultipartFile banner,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        fightersService.uploadFighterMedia(id, portrait, banner, icon);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFighter(
            @PathVariable UUID id,
            @RequestBody FighterUpdateDTO fighterDTO) {
        fightersService.updateFighter(id, fighterDTO);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteFighter(@PathVariable UUID id) {
        fightersService.softDeleteFighter(id);
    }

    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreFighter(@PathVariable UUID id) {
        fightersService.restoreFighter(id);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<FighterStatsDTO> getFighterStats(@PathVariable UUID id) {
        return ResponseEntity.ok(fightersService.getFighterStats(id));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<FighterStatsDTO>> getFightersRanking(){
        return ResponseEntity.ok(fightersService.getFightersRanking());
    }

}
