package FightLeagueKO.team.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import FightLeagueKO.combo.mapper.ComboMapper;
import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.service.FighterService;
import FightLeagueKO.team.dto.CreateTeamDTO;
import FightLeagueKO.team.dto.TeamStatsDTO;
import FightLeagueKO.team.mapper.TeamMapper;
import FightLeagueKO.team.model.Team;
import FightLeagueKO.team.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TeamService implements ITeamService {

    private TeamRepository teamRepository;
    private FighterService fighterService;
    private TeamMapper teamMapper;

    @Autowired
    public TeamService(TeamRepository teamRepository, FighterService fighterService, TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.fighterService = fighterService;
        this.teamMapper = teamMapper;
    }

    @Override
    public Team getTeamById(UUID teamId) {

        Objects.requireNonNull(teamId, "Parameter id for team could not be null");

        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));
    }

    @Override
    public List<Team> getAllTeams() {
        return StreamSupport.stream(teamRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Team> getAllActiveTeams() {

        return teamRepository.getAllActiveTeams();
    }

    @Override
    public Team createTeam(CreateTeamDTO teamDTO) {

        Objects.requireNonNull(teamDTO, "Parameter teamDTO could not be null");

        Team team = new Team();

        if (teamDTO.pointFighterId() == null) {
            throw new IllegalArgumentException("Team point fighter could not be null");
        }

        if (teamDTO.secondFighterId() == null) {
            throw new IllegalArgumentException("Team second fighter could not be null");
        }

        if (teamDTO.fuse() == null) {
            throw new IllegalArgumentException("Team fuse could not be null");
        }

        Optional<Team> alreadyExist = teamRepository.existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(
                teamDTO.pointFighterId(),
                teamDTO.secondFighterId(),
                teamDTO.fuse());

        if (alreadyExist.isPresent()) {
            return alreadyExist.get();
        }

        Fighter pointFighter = fighterService.getFighterById(teamDTO.pointFighterId());
        Fighter seconFighter = fighterService.getFighterById(teamDTO.secondFighterId());

        team.setPointFighter(pointFighter);
        team.setSecondFighter(seconFighter);
        team.setFuse(teamDTO.fuse());
        team.setDeleted(false);
        team.setPlayCounter(0);
        team.setWinCounter(0);

        return teamRepository.save(team);
    }

    @Override
    public void updateTeam(UUID teamId, CreateTeamDTO teamDTO) {

        Objects.requireNonNull(teamDTO, "Parameter teamDTO could not be null");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + teamId));

        Optional.ofNullable(teamDTO.pointFighterId())
                .map(fighterService::getFighterById)
                .ifPresent(team::setPointFighter);

        Optional.ofNullable(teamDTO.secondFighterId())
                .map(fighterService::getFighterById)
                .ifPresent(team::setSecondFighter);

        Optional.ofNullable(teamDTO.fuse())
                .ifPresent(team::setFuse);

        teamRepository.save(team);
    }

    @Override
    public void softDeleteTeam(UUID teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + teamId));

        team.setDeleted(true);

        teamRepository.save(team);

    }

    @Override
    public void restoreTeam(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + teamId));

        team.setDeleted(false);

        teamRepository.save(team);

    }

    @Override
    public void updateTeamStats(UUID teamId, boolean isWinner) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + teamId));

        team.addPlayTeamCounter();

        if (isWinner == true)
            team.addWinCounter();
        else
            team.addLoseCounter();

        team.getPointFighter().getId();

        fighterService.updateFighterStats(team.getPointFighter().getId(), isWinner);
        fighterService.updateFighterStats(team.getSecondFighter().getId(), isWinner);

        teamRepository.save(team);
    }

    @Override
    public TeamStatsDTO getTeamStats(UUID teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + teamId));

        long allTeamsPlayRate = teamRepository.getAllTeamsPlayRate();

        double playRate = allTeamsPlayRate > 0
                ? team.getPlayCounter() * 100.0 / allTeamsPlayRate
                : 0.0;

        return teamMapper.toTeamStatsDTO(team, playRate);
    }

}
