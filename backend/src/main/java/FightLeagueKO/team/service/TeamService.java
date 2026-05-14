package FightLeagueKO.team.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import FightLeagueKO.team.dto.TeamDTO;
import FightLeagueKO.team.model.Team;
import FightLeagueKO.team.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TeamService implements ITeamService {

    private TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public Team getTeamById(UUID id) {

        Objects.requireNonNull(id, "Parameter id for team could not be null");

        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
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
    public Team createTeam(TeamDTO teamDTO) {

        Objects.requireNonNull(teamDTO, "Parameter teamDTO could not be null");

        Team team = new Team();

        if (teamDTO.pointCharacterId() == null) {
            throw new IllegalArgumentException("Team point character could not be null");
        }

        if (teamDTO.secondCharacterId() == null) {
            throw new IllegalArgumentException("Team second character could not be null");
        }

        if (teamDTO.secondCharacterId() == null) {
            throw new IllegalArgumentException("Team fuse could not be null");
        }

        team.setPointCharacterId(teamDTO.pointCharacterId());
        team.setSecondCharacterId(teamDTO.secondCharacterId());
        team.setFuse(teamDTO.fuse());
        team.setDeleted(false);

        return teamRepository.save(team);
    }

    @Override
    public void updateTeam(UUID id, TeamDTO teamDTO) {

        Objects.requireNonNull(teamDTO, "Parameter teamDTO could not be null");

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + id));

        Optional.ofNullable(teamDTO.pointCharacterId())
                .ifPresent(team::setPointCharacterId);

        Optional.ofNullable(teamDTO.secondCharacterId())
                .ifPresent(team::setSecondCharacterId);

        Optional.ofNullable(teamDTO.fuse())
                .ifPresent(team::setFuse);

        teamRepository.save(team);
    }

    @Override
    public void softDeleteTeam(UUID id) {

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + id));

        team.setDeleted(true);

        teamRepository.save(team);

    }

    @Override
    public void restoreTeam(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id:" + id));

        team.setDeleted(false);

        teamRepository.save(team);

    }

}
