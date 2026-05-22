package FightLeagueKO.game.model;

import java.time.LocalDate;
import java.util.UUID;

import FightLeagueKO.tournament.model.Tournament;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @Column(name = "user1_id", nullable = false)
    private UUID user1Id;

    @Column(name = "user2_id", nullable = false)
    private UUID user2Id;

    @Column(name = "team_user1_id")
    private UUID teamUser1Id;

    @Column(name = "team_user2_id")
    private UUID teamUser2Id;

    @Column(name = "winner_id")
    private UUID winnerId;

    @Column(nullable = false)
    private LocalDate gameDate;

    @Column(nullable = false)
    private boolean deleted;

    public Game() {
    } // POJO

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public UUID getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(UUID user1Id) {
        this.user1Id = user1Id;
    }

    public UUID getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(UUID user2Id) {
        this.user2Id = user2Id;
    }

    public UUID getTeamUser1Id() {
        return teamUser1Id;
    }

    public void setTeamUser1Id(UUID teamUser1Id) {
        this.teamUser1Id = teamUser1Id;
    }

    public UUID getTeamUser2Id() {
        return teamUser2Id;
    }

    public void setTeamUser2Id(UUID teamUser2Id) {
        this.teamUser2Id = teamUser2Id;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDate startDate) {
        this.gameDate = startDate;
    }

    public boolean isDelete() {
        return deleted;
    }

    public void setDelete(boolean deleted) {
        this.deleted = deleted;
    }
}
