package FightLeagueKO.tournament.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import FightLeagueKO.game.model.Game;
import FightLeagueKO.tournament.enums.TournamentStates;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID userOwnerId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private TournamentStates tournamentState;

    @Column(nullable = false)
    private int maxPlayers;

    @ElementCollection
    @CollectionTable(
        name = "tournament_players",
        joinColumns = @JoinColumn(name = "tournament_id")
    )
    @Column(name = "user_id")
    private List<UUID> playersIds;

    @OneToMany(mappedBy = "tournament")
    private List<Game> gamesList;

    private LocalDate startDate;

    private LocalDate inscriptionCloseDate;

    private boolean manualClose;

    @Column(name = "winner_id")
    private UUID winnerId;

    private boolean deleted;

    public Tournament() { // POJO

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserOwnerId() {
        return userOwnerId;
    }

    public void setUserOwnerId(UUID userOwnerId) {
        this.userOwnerId = userOwnerId;
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

    public List<UUID> getPlayersIds() {
        return playersIds;
    }

    public void setPlayersIds(List<UUID> playersIds) {
        this.playersIds = playersIds;
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

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void addPlayer(UUID userId) {
        playersIds.add(userId);
    }

    public void removePlayer(UUID userId) {
        playersIds.remove(userId);
    }

}
