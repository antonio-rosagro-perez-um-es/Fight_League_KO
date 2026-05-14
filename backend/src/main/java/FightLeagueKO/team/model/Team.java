package FightLeagueKO.team.model;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import FightLeagueKO.combo.enums.FuseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private UUID pointCharacterId;

    @Column(nullable = false)
    private UUID secondCharacterId;

    @Column(nullable = false)
    private FuseType fuse;

    @Column(nullable = false)
    private int playCounter;

    @Column(nullable = false)
    private int winCounter;

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

    public UUID getPointCharacterId() {
        return pointCharacterId;
    }

    public void setPointCharacterId(UUID pointCharacterId) {
        this.pointCharacterId = pointCharacterId;
    }

    public UUID getSecondCharacterId() {
        return secondCharacterId;
    }

    public void setSecondCharacterId(UUID secondCharacterId) {
        this.secondCharacterId = secondCharacterId;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void addWin(){
        this.winCounter++;
    }

    public void addPlayTeam(){
        this.playCounter++;
    }

    public double getWinRate(){

        if (playCounter == 0) {
            return 0;
        }
        
        return (double) winCounter/playCounter;
    }

}
