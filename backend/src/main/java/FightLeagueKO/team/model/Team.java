package FightLeagueKO.team.model;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.fighter.model.Fighter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "first_fighter_id", nullable = false)
    private Fighter pointFighter;

    @ManyToOne
    @JoinColumn(name = "second_fighter_id", nullable = false)
    private Fighter secondFighter;

    @Column(nullable = false)
    private FuseType fuse;

    @Column(nullable = false)
    private int playCounter;

    @Column(nullable = false)
    private int winCounter;

    @Column(nullable = false)
    private int loseCounter;

    @Column(nullable = false)
    private boolean deleted;

    public Team() { // POJO

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Fighter getPointFighter() {
        return pointFighter;
    }

    public void setPointFighter(Fighter pointFighter) {
        this.pointFighter = pointFighter;
    }

    public Fighter getSecondFighter() {
        return secondFighter;
    }

    public void setSecondFighter(Fighter secondFighter) {
        this.secondFighter = secondFighter;
    }

    public FuseType getFuse() {
        return fuse;
    }

    public void setFuse(FuseType fuse) {
        this.fuse = fuse;
    }

    public int getPlayCounter() {
        return playCounter;
    }

    public void setPlayCounter(int playCounter) {
        this.playCounter = playCounter;
    }

    public int getWinCounter() {
        return winCounter;
    }

    public void setWinCounter(int winCounter) {
        this.winCounter = winCounter;
    }

    public int getLoseCounter() {
        return loseCounter;
    }

    public void setLoseCounter(int loseCounter) {
        this.loseCounter = loseCounter;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void addWinCounter() {
        this.winCounter++;
    }

    public void addLoseCounter(){
        this.loseCounter++;
    }

    public void addPlayTeamCounter() {
        this.playCounter++;
    }
    
    public double getWinRate() {

        if (playCounter == 0) {
            return 0;
        }

        return (winCounter / playCounter) * 100.0;
    }

}
