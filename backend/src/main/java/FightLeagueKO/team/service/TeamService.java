package FightLeagueKO.team.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import FightLeagueKO.fighter.service.FighterService;
import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.team.dto.CreateTeamDTO;
import FightLeagueKO.team.dto.TeamStatsDTO;
import FightLeagueKO.team.dto.UpdateTeamDTO;
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

        if (teamDTO.pointFighterId().equals(teamDTO.secondFighterId())) {
            throw new IllegalArgumentException("Point fighter and second fighter must be different");
        }

        Optional<Team> alreadyExist = teamRepository.existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(
                teamDTO.pointFighterId(),
                teamDTO.secondFighterId(),
                teamDTO.fuse());

        if (alreadyExist.isEmpty()) {
            alreadyExist = teamRepository.existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(
                    teamDTO.secondFighterId(),
                    teamDTO.pointFighterId(),
                    teamDTO.fuse());
        }

        if (alreadyExist.isPresent()) {
            return alreadyExist.get();
        }

        team.setPointFighterId(teamDTO.pointFighterId());
        team.setSecondFighterId(teamDTO.secondFighterId());
        team.setFuse(teamDTO.fuse());
        team.setDeleted(false);
        team.setPlayCounter(0);
        team.setWinCounter(0);

        return teamRepository.save(team);
    }

    @Override
    public void updateTeam(UUID teamId, UpdateTeamDTO teamDTO) {

        Objects.requireNonNull(teamDTO, "Parameter teamDTO could not be null");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + teamId));

        UUID pointFighterId = teamDTO.pointFighterId() != null
                ? teamDTO.pointFighterId()
                : team.getPointFighterId();
        UUID secondFighterId = teamDTO.secondFighterId() != null
                ? teamDTO.secondFighterId()
                : team.getSecondFighterId();
        FuseType fuse = teamDTO.fuse() != null
                ? teamDTO.fuse()
                : team.getFuse();

        if (pointFighterId.equals(secondFighterId)) {
            throw new IllegalArgumentException("Point fighter and second fighter must be different");
        }

        Optional<Team> existing = teamRepository.existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(
                pointFighterId, secondFighterId, fuse);
        if (existing.isEmpty()) {
            existing = teamRepository.existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(
                    secondFighterId, pointFighterId, fuse);
        }
        if (existing.isPresent() && !existing.get().getId().equals(teamId)) {
            throw new IllegalArgumentException("A team with these fighters already exists");
        }

        Optional.ofNullable(teamDTO.pointFighterId())
                .ifPresent(team::setPointFighterId);

        Optional.ofNullable(teamDTO.secondFighterId())
                .ifPresent(team::setSecondFighterId);

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

        fighterService.updateFighterStats(team.getPointFighterId(), isWinner);
        fighterService.updateFighterStats(team.getSecondFighterId(), isWinner);

        teamRepository.save(team);
    }

    @Override
    public void revertTeamStats(UUID teamId, boolean wasWinner) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + teamId));

        team.removePlayTeamCounter();

        if (wasWinner) {
            team.removeWinCounter();
        } else {
            team.removeLoseCounter();
        }

        fighterService.revertFighterStats(team.getPointFighterId(), wasWinner);
        fighterService.revertFighterStats(team.getSecondFighterId(), wasWinner);

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

        return teamMapper.toTeamStatsDTO(team, playRate,
                fighterService.getFighterById(team.getPointFighterId()).getName(),
                fighterService.getFighterById(team.getSecondFighterId()).getName());
    }

    @Override
    public List<TeamStatsDTO> getRankingTeams() {
        List<Team> teams = teamRepository.getAllActiveTeamsWithPlays();

        long allTeamsPlayRate = teamRepository.getAllTeamsPlayRate();

        return teams.stream()
                .sorted((a, b) -> Double.compare(b.getWinRate(), a.getWinRate()))
                .limit(10)
                .map(team -> {
                    double playRate = allTeamsPlayRate > 0
                            ? team.getPlayCounter() * 100.0 / allTeamsPlayRate
                            : 0.0;
                    return teamMapper.toTeamStatsDTO(team, playRate,
                            fighterService.getFighterById(team.getPointFighterId()).getName(),
                            fighterService.getFighterById(team.getSecondFighterId()).getName());
                })
                .collect(Collectors.toList());
    }

}
