package FightLeagueKO.game.model;

import java.time.LocalDate;
import java.util.UUID;

import FightLeagueKO.team.model.Team;
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

    @ManyToOne
    @JoinColumn(name = "team_user1_id", nullable = false)
    private Team teamUser1;

    @ManyToOne
    @JoinColumn(name = "team_user2_id", nullable = false)
    private Team teamUser2;

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

    public Team getTeamUser1() {
        return teamUser1;
    }

    public void setTeamUser1(Team teamUser1) {
        this.teamUser1 = teamUser1;
    }

    public Team getTeamUser2() {
        return teamUser2;
    }

    public void setTeamUser2(Team teamUser2) {
        this.teamUser2 = teamUser2;
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
