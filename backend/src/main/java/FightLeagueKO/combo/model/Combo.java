package FightLeagueKO.combo.model;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.fighter.model.Fighter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "point_fighter_id", nullable = false)
    private Fighter pointFighter;
    @ManyToOne
    @JoinColumn(name = "second_fighter_id", nullable = true)
    private Fighter secondFighter;

    @Column(nullable = false)
    private String textNotation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComboDificulty comboDificulty;

    @Column(nullable = false)
    private FuseType fuse;

    @Column(nullable = false)
    private String mediaUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private LocalDate createdAt;

    private LocalDate upDateAt;

    private int meterCost;

    private int damage;

    private int likeCounter;

    private int dislikeCounter;

    private boolean privateCombo;

    public Combo() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
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

    public int getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(int likeCounter) {
        this.likeCounter = likeCounter;
    }

    public int getDislikeCounter() {
        return dislikeCounter;
    }

    public void setDislikeCounter(int dislikeCounter) {
        this.dislikeCounter = dislikeCounter;
    }

    public boolean isPrivateCombo() {
        return privateCombo;
    }

    public void setPrivateCombo(boolean privateCombo) {
        this.privateCombo = privateCombo;
    }

    public void addLikeCombo() {
        this.likeCounter++;
    }

    public void removeLikeCombo() {
        this.likeCounter--;
    }

    public void addDislikeCombo() {
        this.dislikeCounter++;
    }

    public void removeDislikeCombo() {
        this.dislikeCounter--;
    }

}
