package FightLeagueKO.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityIntegrationTest {

    private final RestClient restClient;

    SecurityIntegrationTest(@Value("${local.server.port}") int port) {
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void publicRankingEndpointAllowsAnonymous() {
        ResponseEntity<String> response = restClient.get().uri("/fighters/ranking").retrieve().toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void adminEndpointRejectsAnonymous() {
        assertThatThrownStatus(() -> restClient.get().uri("/fighters").retrieve().toEntity(String.class),
                HttpStatus.UNAUTHORIZED);
    }

    @Test
    void adminEndpointRejectsRegisteredUser() {
        String token = registerAndExtractToken("registered-user", "registered-user@example.test");

        assertThatThrownStatus(() -> restClient.get()
                .uri("/fighters")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toEntity(String.class), HttpStatus.FORBIDDEN);
    }

    private String registerAndExtractToken(String username, String email) {
        ResponseEntity<String> response = restClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "username": "%s",
                          "email": "%s",
                          "password": "password123"
                        }
                        """.formatted(username, email))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody().replaceFirst(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");
    }

    private void assertThatThrownStatus(Runnable request, HttpStatus status) {
        try {
            request.run();
        } catch (HttpClientErrorException exception) {
            assertThat(exception.getStatusCode()).isEqualTo(status);
            return;
        }
        throw new AssertionError("Expected HTTP status " + status);
    }
}
