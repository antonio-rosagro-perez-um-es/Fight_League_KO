package FightLeagueKO.tournament.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import FightLeagueKO.tournament.enums.TournamentStates;
import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.tournament.repository.TournamentRepository;

@Service
public class TournamentSchedulerService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Scheduled(cron = "0 0 6 * * ?") // Daily at 6 AM
    public void updateTournamentStates() {
        LocalDate today = LocalDate.now();

        // REGISTRATION → WAITING_START
        List<Tournament> expiredReg = tournamentRepository
                .findByTournamentStateAndInscriptionCloseDateBefore(
                        TournamentStates.REGISTRATION, today);

        expiredReg.forEach(t -> {
            t.setTournamentState(TournamentStates.WAITING_START);

        });
        tournamentRepository.saveAll(expiredReg);

        // WAITING_START → IN_PROGRESS
        List<Tournament> started = tournamentRepository
                .findByTournamentStateAndStartDateLessThanEqual(
                        TournamentStates.WAITING_START, today);

        started.forEach(t -> {
            t.setTournamentState(TournamentStates.IN_PROGRESS);
        });
        tournamentRepository.saveAll(started);
    }
}
