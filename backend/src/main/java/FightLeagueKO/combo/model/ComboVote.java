package FightLeagueKO.combo.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import FightLeagueKO.combo.enums.VoteType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "combo_votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"combo_id", "user_id"})
})
public class ComboVote {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "combo_id", nullable = false)
    private UUID comboId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    private VoteType voteType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ComboVote() {
    }

    public ComboVote(UUID comboId, UUID userId, VoteType voteType) {
        this.comboId = comboId;
        this.userId = userId;
        this.voteType = voteType;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getComboId() {
        return comboId;
    }

    public void setComboId(UUID comboId) {
        this.comboId = comboId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
