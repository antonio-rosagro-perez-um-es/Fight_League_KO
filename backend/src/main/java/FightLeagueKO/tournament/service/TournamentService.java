package FightLeagueKO.tournament.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.tournament.repository.TournamentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TournamentService {


    private TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository){
        this.tournamentRepository = tournamentRepository;
    }

    public Tournament getTournamentById(UUID id){
        Objects.requireNonNull(id, "Paramenter id could not be null");

        return tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found with id: " + id));
    }

    

}
