package FightLeagueKO.combo.model;

import java.sql.Date;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "combos")
public class Combo {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private boolean oficial;

    @Column(nullable = false)
    private Character pointCharacter;

    @Column(nullable = true)
    private Character secondCharacter;

    @Column(nullable = false)
    private String textNotation;

    @Column(nullable = false)
    private ComboDificulty comboDificulty;

    @Column(nullable = false)
    private FuseType fuse;

    @Column(nullable = false)
    private String imageUrl;

    @Lob
    @Column(nullable = false)
    private String description;

    private Date createdAt;

    private Date upDateAt;

    private int meterCost;

    private int damage;

    public Combo() {
    } // POJO

    public boolean isOficial() {
        return oficial;
    }

    public void setOficial(boolean oficial) {
        this.oficial = oficial;
    }

    public Character getPointCharacter() {
        return pointCharacter;
    }

    public void setPointCharacter(Character pointCharacter) {
        this.pointCharacter = pointCharacter;
    }

    public Character getsecondCharacter() {
        return secondCharacter;
    }

    public void setSecondCharacter(Character secondCharacter) {
        this.secondCharacter = secondCharacter;
    }

    public String getTextNotation() {
        return textNotation;
    }

    public void setTextNotation(String textNotation) {
        this.textNotation = textNotation;
    }

    public ComboDificulty getComboDificulty() {
        return comboDificulty;
    }

    public void setComboDificulty(ComboDificulty comboDificulty) {
        this.comboDificulty = comboDificulty;
    }

    public FuseType getFuse() {
        return fuse;
    }

    public void setFuse(FuseType fuse) {
        this.fuse = fuse;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpDateAt() {
        return upDateAt;
    }

    public void setUpDateAt(Date upDateAt) {
        this.upDateAt = upDateAt;
    }

    public int getMeterCost() {
        return meterCost;
    }

    public void setMeterCost(int meterCost) {
        this.meterCost = meterCost;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}
