package FightLeagueKO.game.model;

import java.time.LocalDate;
import java.util.UUID;



import FightLeagueKO.team.model.Team;
import FightLeagueKO.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private User user1;

    @Column(nullable = false)
    private User user2;

    @Column(nullable = false)
    private Team teamUser1;

    @Column(nullable = false)
    private Team teamUser2;

    private User winner;

    @Column(nullable = false)
    private LocalDate gameDate;

    @Column(nullable = false)
    private boolean delete;

    public Game() {
    } // POJO

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
