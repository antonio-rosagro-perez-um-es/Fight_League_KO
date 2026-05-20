package FightLeagueKO.user.model;

import java.util.List;
import java.util.UUID;

import FightLeagueKO.tournament.model.Tournament;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "userOwner")
    private List<Tournament> ownedTournaments;

    @ManyToMany(mappedBy = "playersList")
    private List<Tournament> participatedTournaments;

    @OneToMany(mappedBy = "winner")
    private List<Tournament> wonTournaments;

    public User() {
        // POJO
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Tournament> getOwnedTournaments() {
        return ownedTournaments;
    }

    public void setOwnedTournaments(List<Tournament> ownedTournaments) {
        this.ownedTournaments = ownedTournaments;
    }

    public List<Tournament> getParticipatedTournaments() {
        return participatedTournaments;
    }

    public void setParticipatedTournaments(List<Tournament> participatedTournaments) {
        this.participatedTournaments = participatedTournaments;
    }

    public List<Tournament> getWonTournaments() {
        return wonTournaments;
    }

    public void setWonTournaments(List<Tournament> wonTournaments) {
        this.wonTournaments = wonTournaments;
    }

}