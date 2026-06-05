package FightLeagueKO.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import FightLeagueKO.user.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    private final RestClient restClient;

    @Autowired
    private UserRepository userRepository;

    AuthControllerIntegrationTest(@Value("${local.server.port}") int port) {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void registerCreatesUserAndReturnsToken() {
        ResponseEntity<String> response = postJson("/auth/register", """
                {
                  "username": "new-user",
                  "email": "new-user@example.test",
                  "password": "password123"
                }
                """);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("\"token\":").contains("\"username\":\"new-user\"");
        assertThat(userRepository.existsByUsername("new-user")).isTrue();
    }

    @Test
    void loginReturnsTokenForDefaultAdmin() {
        ResponseEntity<String> response = postJson("/auth/login", """
                {
                  "usernameOrEmail": "admin",
                  "password": "admin1234"
                }
                """);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"token\":").contains("\"role\":\"ADMIN\"");
    }

    private ResponseEntity<String> postJson(String path, String body) {
        return restClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(String.class);
    }
}
