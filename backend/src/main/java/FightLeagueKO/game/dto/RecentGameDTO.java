package FightLeagueKO.game.dto;

import java.time.LocalDate;
import java.util.UUID;

public record RecentGameDTO(
    UUID id,
    UUID tournamentId,
    String tournamentTitle,
    UUID user1Id,
    String user1Username,
    UUID user2Id,
    String user2Username,
    UUID teamUser1Id,
    UUID teamUser2Id,
    RecentGameTeamDTO teamUser1,
    RecentGameTeamDTO teamUser2,
    UUID winnerId,
    LocalDate gameDate,
    boolean wonByCurrentUser
) {
    public record RecentGameTeamDTO(
        UUID id,
        UUID pointFighterId,
        String pointFighterName,
        String pointFighterSlug,
        UUID secondFighterId,
        String secondFighterName,
        String secondFighterSlug,
        String fuse
    ) {
    }
}
