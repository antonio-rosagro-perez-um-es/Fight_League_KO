package FightLeagueKO.user.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.service.IUserService;

@RestController
@RequestMapping("/users")    
public class UserController {

    private IUserService userService;

    @Autowired
    public UserController(IUserService userService){
        this.userService = userService;
    }

    @GetMapping("{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserDTO userDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO));
    }
}
