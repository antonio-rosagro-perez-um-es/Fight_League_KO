package FightLeagueKO.combo.model;

import java.time.LocalDate;
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

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private boolean deleted;

    @Column(nullable = false)
    private boolean oficial;

    @Column(nullable = false)
    private UUID pointCharacterId;

    @Column(nullable = true)
    private UUID secondCharacterId;

    @Column(nullable = false)
    private String textNotation;

    @Column(nullable = false)
    private ComboDificulty comboDificulty;

    @Column(nullable = false)
    private FuseType fuse;

    @Column(nullable = false)
    private String mediaUrl;

    @Lob
    @Column(nullable = false)
    private String description;

    private LocalDate createdAt;

    private LocalDate upDateAt;

    private int meterCost;

    private int damage;

    public Combo() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isOficial() {
        return oficial;
    }

    public void setOficial(boolean oficial) {
        this.oficial = oficial;
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

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpDateAt() {
        return upDateAt;
    }

    public void setUpDateAt(LocalDate upDateAt) {
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
