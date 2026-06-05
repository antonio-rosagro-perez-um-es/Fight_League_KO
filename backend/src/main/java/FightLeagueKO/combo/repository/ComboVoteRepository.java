package FightLeagueKO.combo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import FightLeagueKO.combo.enums.VoteType;
import FightLeagueKO.combo.model.ComboVote;

@Repository
public interface ComboVoteRepository extends JpaRepository<ComboVote, UUID> {

    Optional<ComboVote> findByComboIdAndUserId(UUID comboId, UUID userId);

    int countByComboIdAndVoteType(UUID comboId, VoteType voteType);

    void deleteByComboIdAndUserId(UUID comboId, UUID userId);
}
