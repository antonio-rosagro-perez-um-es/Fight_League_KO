package FightLeagueKO.game.model;

import java.time.LocalDate;
import java.util.UUID;

import FightLeagueKO.team.model.Team;
import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.user.model.User;
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

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @ManyToOne
    @JoinColumn(name = "team_user1_id", nullable = false)
    private Team teamUser1;

    @ManyToOne
    @JoinColumn(name = "team_user2_id", nullable = false)
    private Team teamUser2;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    @Column(nullable = false)
    private LocalDate gameDate;

    @Column(nullable = false)
    private boolean delete;

    public Game() {
    } // POJO

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
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

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDate startDate) {
        this.gameDate = startDate;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
