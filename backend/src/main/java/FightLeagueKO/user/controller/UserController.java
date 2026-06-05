package FightLeagueKO.user.controller;

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
import org.springframework.http.HttpStatus;

import FightLeagueKO.security.CurrentUserService;
import FightLeagueKO.user.dto.AdminUserDTO;
import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.dto.UpdateUserProfileDTO;
import FightLeagueKO.user.dto.UserDTO;
import FightLeagueKO.user.dto.UserProfileDTO;
import FightLeagueKO.user.dto.UserRankingDTO;
import FightLeagueKO.user.service.IUserService;

@RestController
@RequestMapping("/users")    
public class UserController {

    private IUserService userService;
    private CurrentUserService currentUserService;

    @Autowired
    public UserController(IUserService userService, CurrentUserService currentUserService){
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<UserRankingDTO>> getUsersRanking() {
        return ResponseEntity.ok(userService.getTopUsersRanking());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<AdminUserDTO>> getAllUsersForAdmin() {
        return ResponseEntity.ok(userService.getAllUsersForAdmin());
    }

    @PatchMapping("/admin/{userId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteUser(@PathVariable UUID userId) {
        userService.softDeleteUser(userId);
    }

    @PatchMapping("/admin/{userId}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreUser(@PathVariable UUID userId) {
        userService.restoreUser(userId);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getUserProfile(currentUserService.getCurrentUserId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileDTO> updateCurrentUserProfile(@RequestBody UpdateUserProfileDTO profileDTO) {
        return ResponseEntity.ok(userService.updateUserProfile(currentUserService.getCurrentUserId(), profileDTO));
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable UUID userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO userDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO));
    }
}
