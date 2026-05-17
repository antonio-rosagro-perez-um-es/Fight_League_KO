package FightLeagueKO.tournament.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import FightLeagueKO.game.model.Game;
import FightLeagueKO.tournament.enums.TournamentStates;
import FightLeagueKO.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private User userOwner;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private TournamentStates tournamentState;

    @Column(nullable = false)
    private int maxPlayers;

    private List<User> playersList;

    private List<Game> gamesList;

    private LocalDate startDate;

    private LocalDate inscriptionCloseDate;

    private boolean manualClose;

    private User winner;

    private boolean deleted;

    public Tournament() { // POJO

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(User userOwner) {
        this.userOwner = userOwner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TournamentStates getTournamentState() {
        return tournamentState;
    }

    public void setTournamentState(TournamentStates tournamentState) {
        this.tournamentState = tournamentState;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public List<User> getPlayersList() {
        return playersList;
    }

    public void setPlayersList(List<User> playersList) {
        this.playersList = playersList;
    }

    public List<Game> getGamesList() {
        return gamesList;
    }

    public void setGamesList(List<Game> matchList) {
        this.gamesList = matchList;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getInscriptionCloseDate() {
        return inscriptionCloseDate;
    }

    public void setInscriptionCloseDate(LocalDate inscriptionCloseDate) {
        this.inscriptionCloseDate = inscriptionCloseDate;
    }

    public boolean isManualClose() {
        return manualClose;
    }

    public void setManualClose(boolean manualClose) {
        this.manualClose = manualClose;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void addPlayer(User user) {
        playersList.add(user);
    }

    public void removePlayer(User user) {
        playersList.remove(user);
    }

}
