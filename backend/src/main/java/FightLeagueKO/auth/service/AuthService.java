package FightLeagueKO.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import FightLeagueKO.auth.dto.AuthResponse;
import FightLeagueKO.auth.dto.LoginRequest;
import FightLeagueKO.auth.dto.RegisterRequest;
import FightLeagueKO.user.dto.CreateUserDTO;
import FightLeagueKO.user.dto.UserDTO;
import FightLeagueKO.user.mapper.UserMapper;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.service.UserService;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final UserMapper userMapper;
    private final String issuer;
    private final long expirationMinutes;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            UserMapper userMapper,
            @Value("${app.security.jwt.issuer}") String issuer,
            @Value("${app.security.jwt.expiration-minutes}") long expirationMinutes) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.userMapper = userMapper;
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    public AuthResponse register(RegisterRequest request) {
        UserDTO saved = userService.createUser(new CreateUserDTO(
                request.username(), request.email(), request.password()));
        return new AuthResponse(createToken(saved), saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userService.findByUsername(request.usernameOrEmail())
                .or(() -> userService.findByEmail(request.usernameOrEmail()))
                .filter(found -> !found.isDeleted())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        UserDTO userDTO = userMapper.toDTO(user);
        return new AuthResponse(createToken(userDTO), userDTO);
    }

    private String createToken(UserDTO user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(user.id().toString())
                .claim("username", user.username())
                .claim("roles", List.of(user.role().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
