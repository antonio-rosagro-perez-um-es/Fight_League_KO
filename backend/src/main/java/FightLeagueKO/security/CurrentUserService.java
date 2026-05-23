package FightLeagueKO.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import FightLeagueKO.user.model.User;
import FightLeagueKO.user.service.UserService;

@Service
public class CurrentUserService {

    private final UserService userService;

    public CurrentUserService(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser() {
        UUID userId = getCurrentUserId();
        return userService.findUserEntityById(userId);
    }

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated user found");
        }

        return UUID.fromString(jwt.getSubject());
    }
}
