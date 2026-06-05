package FightLeagueKO.common.seed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import FightLeagueKO.combo.enums.ComboDificulty;
import FightLeagueKO.combo.enums.FuseType;
import FightLeagueKO.combo.enums.VoteType;
import FightLeagueKO.combo.model.Combo;
import FightLeagueKO.combo.model.ComboVote;
import FightLeagueKO.combo.repository.ComboRepositoryPostgre;
import FightLeagueKO.combo.repository.ComboVoteRepository;
import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.repository.FighterRepositoryPostgre;
import FightLeagueKO.game.model.Game;
import FightLeagueKO.game.repository.GameRepositoryPostgre;
import FightLeagueKO.team.model.Team;
import FightLeagueKO.team.repository.TeamRepositoryPostgre;
import FightLeagueKO.tournament.enums.TournamentStates;
import FightLeagueKO.tournament.model.Tournament;
import FightLeagueKO.tournament.repository.TournamentRepositoryPostgre;
import FightLeagueKO.user.enums.UserRole;
import FightLeagueKO.user.model.User;
import FightLeagueKO.user.repository.UserRepositoryPostgre;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    private static final String TEST_PASSWORD = "password123";

    private final FighterRepositoryPostgre fighterRepository;
    private final UserRepositoryPostgre userRepository;
    private final TeamRepositoryPostgre teamRepository;
    private final TournamentRepositoryPostgre tournamentRepository;
    private final GameRepositoryPostgre gameRepository;
    private final ComboRepositoryPostgre comboRepository;
    private final ComboVoteRepository comboVoteRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(FighterRepositoryPostgre fighterRepository, UserRepositoryPostgre userRepository,
            TeamRepositoryPostgre teamRepository, TournamentRepositoryPostgre tournamentRepository,
            GameRepositoryPostgre gameRepository, ComboRepositoryPostgre comboRepository,
            ComboVoteRepository comboVoteRepository, PasswordEncoder passwordEncoder) {
        this.fighterRepository = fighterRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
        this.gameRepository = gameRepository;
        this.comboRepository = comboRepository;
        this.comboVoteRepository = comboVoteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Seeding demo database data...");
        seedFighters();
        Map<String, Fighter> fighters = loadFightersBySlug();
        Map<String, User> users = seedUsers();
        seedTournamentFixtures(users, fighters);
        List<Combo> combos = seedCombos(users, fighters);
        seedVotes(users, combos);
        log.info("Demo database seeding complete. Test user password: {}", TEST_PASSWORD);
    }

    private void seedFighters() {
        seedFighter(fighter("Akali",
                "A deadly assassin shrouded in smoke and mystery, Akali strikes from the shadows with blinding speed. Her kunai and kama make her a lethal close-range threat.",
                "Ionia", "Assassin", "The Rogue Assassin", "Shadow tactics, precision strikes, outplaying opponents",
                "Bright lights, predictable patterns, crowded fights", "akali", 3, 2, 8, 3, 9, 4));
        seedFighter(fighter("Caitlyn",
                "The Sheriff of Piltover never misses her mark. Armed with a custom hextech rifle, Caitlyn controls the battlefield from afar, trapping opponents and punishing them from across the screen.",
                "Piltover", "Marksman", "The Sheriff of Piltover", "Long-range control, trap setups, clean execution",
                "Close-quarters brawls, dirty fighting, disorder", "caitlyn", 4, 9, 6, 4, 5, 6));
        seedFighter(fighter("Warwick",
                "A beast driven by bloodlust, Warwick hunts his prey with primal ferocity. His regenerative abilities let him absorb punishment while closing in for the kill.",
                "Zaun", "Juggernaut", "The Uncaged Wrath of Zaun",
                "The thrill of the hunt, bloodshed, relentless pursuit",
                "Escaped prey, bright lights, being kept at range", "warwick", 7, 2, 7, 8, 6, 7));
        seedFighter(fighter("Teemo",
                "Though small in stature, Teemo is a relentless scout with a mischievous arsenal. Traps, poison, and blinding darts make him a frustrating opponent who controls space through cunning.",
                "Bandle City", "Scout", "The Swift Scout", "Stealth, traps, outsmarting bigger foes",
                "Being caught, direct confrontation, losing patience", "teemo", 3, 6, 5, 3, 7, 8));
        seedFighter(fighter("Blitzcrank",
                "A massive steam golem with a magnetic grasp, Blitzcrank punishes any opponent who mispositions. One well-timed grab can turn the tide of any match.",
                "Zaun", "Tank", "The Great Steam Golem", "Big grabs, knockouts, protecting teammates",
                "Fast opponents, being kited, malfunction", "blitzcrank", 8, 4, 6, 9, 3, 5));
        seedFighter(fighter("Vi",
                "Vi punches first and asks questions never. Armed with hextech gauntlets, she breaks through defenses with brute force and relentless pressure.",
                "Piltover", "Brawler", "The Piltover Enforcer",
                "Breaking things, decisive victories, high-impact combat", "Indecision, red tape, slippery opponents",
                "vi", 6, 3, 8, 6, 7, 6));
        seedFighter(fighter("Jinx",
                "Chaos personified, Jinx rains explosives from above with maniacal glee. Her unpredictable toolkit keeps opponents guessing between rocket barrages and rapid-fire minigun pressure.",
                "Zaun", "Marksman", "The Loose Cannon", "Explosions, mayhem, stylish finishes",
                "Rules, silence, being predictable", "jinx", 3, 8, 9, 2, 5, 3));
        seedFighter(fighter("Braum",
                "Braum turns defense into offense with his unbreakable ice shield. He protects allies and shrugs off hits that would fell lesser fighters, marching forward with gentle strength.",
                "Freljord", "Support", "The Heart of the Freljord",
                "Protecting others, shared meals, friendly competition", "Bullies, cruelty, seeing others hurt",
                "braum", 9, 2, 5, 9, 4, 7));
        seedFighter(fighter("Illaoi",
                "The Kraken Priestess summons otherworldly tentacles to overwhelm her foes. She controls space with spiritual manifestations and crushes anyone who dares stand their ground.",
                "Bilgewater", "Juggernaut", "The Kraken Priestess",
                "Proving faith through strength, crushing the faithful", "Cowardice, doubt, those who flee from truth",
                "illaoi", 7, 4, 8, 7, 3, 4));
        seedFighter(fighter("Yasuo",
                "A wandering swordsman exiled by false accusation, Yasuo cuts through any opposition with unparalleled iaijutsu. His wind wall and steel tempest create deadly spacing puzzles.",
                "Ionia", "Duelist", "The Unforgotten", "Sake, the sound of steel, a worthy duel",
                "Liars, cowards, being bound by tradition", "yasuo", 4, 3, 7, 4, 8, 2));
        seedFighter(fighter("Ahri",
                "Enchanting and elusive, Ahri weaves between foes with mystical grace. Her orb of deception rewards clever positioning while her charm turns the tide of any engagement.",
                "Ionia", "Mage", "The Nine-Tailed Fox", "Mystical artifacts, charming encounters, clever plays",
                "Confinement, brute force, being ignored", "ahri", 4, 6, 7, 3, 8, 5));
        seedFighter(fighter("Ekko",
                "Master of time and momentum, Ekko dances around opponents with clever traps and hit-and-run tactics. His temporal abilities let him reverse mistakes and capitalize on openings.",
                "Zaun", "Assassin", "The Boy Who Shattered Time", "Innovation, second chances, proving himself",
                "Injustice, wasting time, giving up", "ekko", 4, 3, 7, 4, 9, 4));
        seedFighter(fighter("Darius",
                "The Hand of Noxus brings absolute judgment to the battlefield. His massive axe cleaves through entire teams, and once he draws blood, there is no escape from his merciless combo.",
                "Noxus", "Juggernaut", "The Hand of Noxus", "Strength, discipline, decisive victories",
                "Weakness, treason, mercy without purpose", "darius", 8, 3, 9, 8, 3, 6));
    }

    private void seedFighter(Fighter fighter) {
        if (fighterRepository.findBySlug(fighter.getSlug()).isPresent()) {
            return;
        }

        fighterRepository.save(fighter);
    }

    private Map<String, Fighter> loadFightersBySlug() {
        return StreamSupport.stream(fighterRepository.findAll().spliterator(), false)
                .filter(fighter -> !fighter.isDeleted())
                .collect(Collectors.toMap(Fighter::getSlug, fighter -> fighter));
    }

    private Map<String, User> seedUsers() {
        Map<String, User> users = new LinkedHashMap<>();
        users.put("admin", userRepository.findByUsername("admin").orElseGet(() -> seedUser("admin", "admin@fightleague.local", UserRole.ADMIN)));
        users.put("riven", seedUser("riven-main", "riven.main@fightleague.local", UserRole.ORGANIZER));
        users.put("yasuo", seedUser("yasuo-dojo", "yasuo.dojo@fightleague.local", UserRole.ORGANIZER));
        users.put("jinx", seedUser("jinx-rockets", "jinx.rockets@fightleague.local", UserRole.REGISTERED));
        users.put("akali", seedUser("akali-smoke", "akali.smoke@fightleague.local", UserRole.REGISTERED));
        users.put("ekko", seedUser("ekko-rewind", "ekko.rewind@fightleague.local", UserRole.REGISTERED));
        users.put("ahri", seedUser("ahri-charms", "ahri.charms@fightleague.local", UserRole.REGISTERED));
        users.put("braum", seedUser("braum-shield", "braum.shield@fightleague.local", UserRole.REGISTERED));
        users.put("caitlyn", seedUser("caitlyn-sheriff", "caitlyn.sheriff@fightleague.local", UserRole.REGISTERED));
        for (int playerNumber = 10; playerNumber <= 32; playerNumber++) {
            users.put("player" + playerNumber, seedUser("player-" + playerNumber,
                    "player" + playerNumber + "@fightleague.local", UserRole.REGISTERED));
        }
        return users;
    }

    private User seedUser(String username, String email, UserRole role) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
            user.setRole(role);
            user.setDeleted(false);
            user.setScore(0);
            user.setTournamentWins(0);
            return userRepository.save(user);
        });
    }

    private void seedTournamentFixtures(Map<String, User> users, Map<String, Fighter> fighters) {
        Tournament registration = seedTournament("Rookie Open Registration", users.get("riven"), TournamentStates.REGISTRATION,
                8, LocalDate.now().plusDays(21), LocalDate.now().plusDays(14), false, null,
                userIds(users, "jinx", "akali", "ekko"));
        Tournament waiting = seedTournament("Piltover Waiting Bracket", users.get("yasuo"), TournamentStates.WAITING_START,
                4, LocalDate.now().plusDays(5), LocalDate.now().minusDays(1), true, null,
                userIds(users, "jinx", "akali", "ekko", "ahri"));
        seedTournament("Today Community Clash", users.get("riven"), TournamentStates.WAITING_START,
                4, LocalDate.now(), LocalDate.now().minusDays(1), true, null,
                userIds(users, "jinx", "akali", "ekko", "ahri"));
        Tournament progress = seedTournament("Zaun In Progress Clash", users.get("riven"), TournamentStates.IN_PROGRESS,
                4, LocalDate.now().minusDays(1), LocalDate.now().minusDays(3), true, null,
                userIds(users, "jinx", "akali", "ekko", "ahri"));
        Tournament finished = seedTournament("Ionia Finals Archive", users.get("yasuo"), TournamentStates.FINISHED,
                4, LocalDate.now().minusDays(15), LocalDate.now().minusDays(20), true, users.get("akali").getId(),
                userIds(users, "jinx", "akali", "ekko", "ahri"));
        Tournament grandFinals = seedTournament("Grand Finals 32 Players Archive", users.get("yasuo"), TournamentStates.FINISHED,
                32, LocalDate.now().minusDays(7), LocalDate.now().minusDays(14), true, users.get("akali").getId(),
                grandFinalsPlayerIds(users));

        resetTournamentGames(progress);
        resetTournamentGames(finished);
        resetTournamentGames(grandFinals);

        Team akaliAhri = seedTeam(fighters.get("akali"), fighters.get("ahri"), FuseType.FREESTYLE, 2, 2, 0);
        Team jinxVi = seedTeam(fighters.get("jinx"), fighters.get("vi"), FuseType.DOUBLE_DOWN, 2, 0, 2);
        Team ekkoYasuo = seedTeam(fighters.get("ekko"), fighters.get("yasuo"), FuseType.TWO_X_ASSIST, 2, 1, 1);
        Team braumCaitlyn = seedTeam(fighters.get("braum"), fighters.get("caitlyn"), FuseType.SIDEKICK, 2, 0, 2);
        Team dariusWarwick = seedTeam(fighters.get("darius"), fighters.get("warwick"), FuseType.JUGGERNAUT, 1, 1, 0);
        Team illaoiBlitz = seedTeam(fighters.get("illaoi"), fighters.get("blitzcrank"), FuseType.DOUBLE_DOWN, 1, 0, 1);

        seedGame(progress, users.get("jinx"), users.get("akali"), jinxVi, akaliAhri, users.get("akali"), 1, 1,
                LocalDate.now().minusDays(1));
        seedGame(progress, users.get("ekko"), users.get("ahri"), ekkoYasuo, braumCaitlyn, null, 1, 2,
                LocalDate.now());

        seedGame(finished, users.get("jinx"), users.get("akali"), jinxVi, akaliAhri, users.get("akali"), 1, 1,
                LocalDate.now().minusDays(14));
        seedGame(finished, users.get("ekko"), users.get("ahri"), ekkoYasuo, braumCaitlyn, users.get("ekko"), 1, 2,
                LocalDate.now().minusDays(14));
        seedGame(finished, users.get("akali"), users.get("ekko"), akaliAhri, dariusWarwick, users.get("akali"), 2, 1,
                LocalDate.now().minusDays(12));
        seedGame(finished, users.get("jinx"), users.get("ahri"), jinxVi, illaoiBlitz, users.get("jinx"), 2, 2,
                LocalDate.now().minusDays(12));
        seedGrandFinalsBracket(grandFinals, users,
                List.of(akaliAhri, jinxVi, ekkoYasuo, braumCaitlyn, dariusWarwick, illaoiBlitz));

        applyFighterStats(fighters, "akali", 2, 2, 0);
        applyFighterStats(fighters, "ahri", 2, 2, 0);
        applyFighterStats(fighters, "jinx", 2, 0, 2);
        applyFighterStats(fighters, "vi", 2, 0, 2);
        applyFighterStats(fighters, "ekko", 2, 1, 1);
        applyFighterStats(fighters, "yasuo", 2, 1, 1);
        applyFighterStats(fighters, "braum", 2, 0, 2);
        applyFighterStats(fighters, "caitlyn", 2, 0, 2);
        applyFighterStats(fighters, "darius", 1, 1, 0);
        applyFighterStats(fighters, "warwick", 1, 1, 0);
        applyFighterStats(fighters, "illaoi", 1, 0, 1);
        applyFighterStats(fighters, "blitzcrank", 1, 0, 1);

        applyUserStats(users.get("akali"), 10, 1);
        applyUserStats(users.get("ekko"), 9, 0);
        applyUserStats(users.get("jinx"), 8, 0);
        applyUserStats(users.get("ahri"), 7, 0);

        tournamentRepository.save(registration);
        tournamentRepository.save(waiting);
    }

    private Tournament seedTournament(String title, User owner, TournamentStates state, int maxPlayers, LocalDate startDate,
            LocalDate closeDate, boolean manualClose, UUID winnerId, List<UUID> playerIds) {
        Tournament tournament = findTournamentByTitle(title);
        if (tournament == null) {
            tournament = new Tournament();
        }
        tournament.setUserOwnerId(owner.getId());
        tournament.setTitle(title);
        tournament.setTournamentState(state);
        tournament.setMaxPlayers(maxPlayers);
        tournament.setPlayersIds(new ArrayList<>(playerIds));
        tournament.setStartDate(startDate);
        tournament.setInscriptionCloseDate(closeDate);
        tournament.setManualClose(manualClose);
        tournament.setWinnerId(winnerId);
        tournament.setScored(state == TournamentStates.FINISHED);
        tournament.setScoredAt(state == TournamentStates.FINISHED ? LocalDateTime.now().minusDays(10) : null);
        tournament.setDeleted(false);
        return tournamentRepository.save(tournament);
    }

    private Tournament findTournamentByTitle(String title) {
        return StreamSupport.stream(tournamentRepository.findAll().spliterator(), false)
                .filter(tournament -> title.equals(tournament.getTitle()))
                .findFirst()
                .orElse(null);
    }

    private List<UUID> userIds(Map<String, User> users, String... keys) {
        return Arrays.stream(keys).map(key -> users.get(key).getId()).collect(Collectors.toList());
    }

    private List<UUID> grandFinalsPlayerIds(Map<String, User> users) {
        return grandFinalsPlayerKeys().stream().map(key -> users.get(key).getId()).collect(Collectors.toList());
    }

    private List<String> grandFinalsPlayerKeys() {
        List<String> playerKeys = new ArrayList<>(List.of("jinx", "akali", "ekko", "ahri", "braum", "caitlyn", "riven", "yasuo", "admin"));
        for (int playerNumber = 10; playerNumber <= 32; playerNumber++) {
            playerKeys.add("player" + playerNumber);
        }
        return playerKeys;
    }

    private void seedGrandFinalsBracket(Tournament tournament, Map<String, User> users, List<Team> teams) {
        List<User> competitors = grandFinalsPlayerKeys().stream().map(users::get).collect(Collectors.toList());
        User champion = users.get("akali");
        int roundNumber = 1;
        LocalDate gameDate = tournament.getStartDate();

        while (competitors.size() > 1) {
            List<User> winners = new ArrayList<>();
            for (int gameIndex = 0; gameIndex < competitors.size(); gameIndex += 2) {
                User user1 = competitors.get(gameIndex);
                User user2 = competitors.get(gameIndex + 1);
                User winner = user1.equals(champion) || user2.equals(champion)
                        ? champion
                        : user1;
                int bracketPosition = (gameIndex / 2) + 1;
                seedGame(tournament, user1, user2, teams.get(gameIndex % teams.size()),
                        teams.get((gameIndex + 1) % teams.size()), winner, roundNumber, bracketPosition, gameDate);
                winners.add(winner);
            }
            competitors = winners;
            roundNumber++;
            gameDate = gameDate.plusDays(1);
        }
    }

    private void resetTournamentGames(Tournament tournament) {
        gameRepository.getTournamentGames(tournament.getId()).forEach(gameRepository::delete);
    }

    private Team seedTeam(Fighter point, Fighter second, FuseType fuse, int plays, int wins, int loses) {
        Team team = teamRepository.existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(
                point.getId(), second.getId(), fuse)
                .or(() -> teamRepository.existsByPointFighterIdAndSecondFighterIdAndFuseAndDeletedFalse(
                        second.getId(), point.getId(), fuse))
                .orElseGet(Team::new);
        team.setPointFighterId(point.getId());
        team.setSecondFighterId(second.getId());
        team.setFuse(fuse);
        team.setDeleted(false);
        team.setPlayCounter(plays);
        team.setWinCounter(wins);
        team.setLoseCounter(loses);
        return teamRepository.save(team);
    }

    private Game seedGame(Tournament tournament, User user1, User user2, Team team1, Team team2, User winner,
            int roundNumber, int bracketPosition, LocalDate gameDate) {
        Game game = new Game();
        game.setTournament(tournament);
        game.setUser1Id(user1.getId());
        game.setUser2Id(user2.getId());
        game.setTeamUser1Id(team1.getId());
        game.setTeamUser2Id(team2.getId());
        game.setWinnerId(winner == null ? null : winner.getId());
        game.setRoundNumber(roundNumber);
        game.setBracketPosition(bracketPosition);
        game.setGameDate(gameDate);
        game.setDelete(false);
        return gameRepository.save(game);
    }

    private void applyFighterStats(Map<String, Fighter> fighters, String slug, int plays, int wins, int loses) {
        Fighter fighter = fighters.get(slug);
        fighter.setPlayCounter(plays);
        fighter.setWinCounter(wins);
        fighter.setLoseCounter(loses);
        fighterRepository.save(fighter);
    }

    private void applyUserStats(User user, int score, int tournamentWins) {
        user.setScore(score);
        user.setTournamentWins(tournamentWins);
        userRepository.save(user);
    }

    private List<Combo> seedCombos(Map<String, User> users, Map<String, Fighter> fighters) {
        List<Combo> combos = new ArrayList<>();
        combos.add(seedCombo("Official Akali Smoke Route", users.get("admin"), fighters.get("akali"), fighters.get("ahri"),
                "2L 2M 5H > Assist > 236H", ComboDificulty.INTERMEDIATE, FuseType.FREESTYLE,
                "https://media.fightleague.local/combos/akali-smoke-route.mp4",
                "Official starter route that demonstrates Akali corner carry with an Ahri assist extension.", 1, 430,
                true, false));
        combos.add(seedCombo("Official Jinx Rocket Confirm", users.get("admin"), fighters.get("jinx"), fighters.get("vi"),
                "5M 5H > 214M > Super", ComboDificulty.BEGINNER, FuseType.DOUBLE_DOWN,
                "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                "Simple punish route into reliable super damage for new Jinx players.", 2, 510, true, false));
        combos.add(seedCombo("Official Ekko Reset Drill", users.get("admin"), fighters.get("ekko"), fighters.get("yasuo"),
                "2M 5H > 236M > Assist > 22H", ComboDificulty.ADVANCED, FuseType.TWO_X_ASSIST,
                "https://media.fightleague.local/combos/ekko-reset-drill.mp4",
                "Advanced reset sequence built around Ekko time pressure and Yasuo screen control.", 2, 560, true, false));
        combos.add(seedCombo("Community Ahri Charm Loop", users.get("ahri"), fighters.get("ahri"), fighters.get("braum"),
                "5L 5M 2H > j.M j.H > Charm", ComboDificulty.INTERMEDIATE, FuseType.SIDEKICK,
                "https://media.fightleague.local/combos/ahri-charm-loop.mp4",
                "Public community route focused on consistent air pickup after charm pressure.", 1, 390, false, false));
        combos.add(seedCombo("Private Akali Lab Route", users.get("akali"), fighters.get("akali"), fighters.get("ekko"),
                "2M 5H > 236L > dash > 5M", ComboDificulty.ADVANCED, FuseType.FREESTYLE,
                "https://media.fightleague.local/combos/private-akali-lab.mp4",
                "Private lab note for a tighter Akali route that still needs matchup testing.", 1, 470, false, true));
        combos.add(seedCombo("Community Braum Wall Bounce", users.get("braum"), fighters.get("braum"), fighters.get("caitlyn"),
                "5H > 214H > Assist > 2H", ComboDificulty.BEGINNER, FuseType.SIDEKICK,
                "https://media.fightleague.local/combos/braum-wall-bounce.mp4",
                "Beginner-friendly public combo for defensive teams that want a clear punish route.", 0, 330, false, false));
        combos.add(seedCombo("Official Jinx Pow-Pow Starter", users.get("admin"), fighters.get("jinx"), fighters.get("vi"),
                "5L 5M 5H > 236M", ComboDificulty.BEGINNER, FuseType.DOUBLE_DOWN,
                "https://youtu.be/dQw4w9WgXcQ",
                "Fast midscreen starter that keeps Jinx close enough to continue pressure after Pow-Pow hits.", 1, 360,
                true, false));
        combos.add(seedCombo("Official Jinx Flame Chompers Setup", users.get("admin"), fighters.get("jinx"), fighters.get("vi"),
                "2M 5H > 214L > dash > 5M", ComboDificulty.INTERMEDIATE, FuseType.SIDEKICK,
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "Chompers route built to test the combo window description area under an embedded video.", 1, 420,
                true, false));
        combos.add(seedCombo("Official Jinx Zap Extension", users.get("admin"), fighters.get("jinx"), fighters.get("vi"),
                "5M 2H > j.M j.H > 236H", ComboDificulty.INTERMEDIATE, FuseType.FREESTYLE,
                "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                "Air pickup into Zap extension for checking longer notation blocks inside the official combo modal.", 2, 500,
                true, false));
        combos.add(seedCombo("Official Jinx Corner Rocket Loop", users.get("admin"), fighters.get("jinx"), fighters.get("vi"),
                "5H > 214M > 5M > 2H > j.H", ComboDificulty.ADVANCED, FuseType.TWO_X_ASSIST,
                "https://youtu.be/dQw4w9WgXcQ",
                "Corner-focused loop with enough damage and metadata to validate restricted combo list scrolling.", 2, 590,
                true, false));
        combos.add(seedCombo("Official Jinx Super Mega Ender", users.get("admin"), fighters.get("jinx"), fighters.get("vi"),
                "2M 5H > 236H > 214H > Super", ComboDificulty.ADVANCED, FuseType.JUGGERNAUT,
                "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                "High-damage ender route for testing the official combo detail labels, video, and lower description layout.", 3,
                680, true, false));

        combos.add(seedCombo("Notation Test Combo", users.get("admin"), fighters.get("akali"), fighters.get("ahri"),
                "5L > 5M > 5H(2) > 2H > 9 jc > delayed j.2HH > hold j.S2 > 1T > 3S1 > 7S2 > 6H > 4M > 8L",
                ComboDificulty.ADVANCED, FuseType.FREESTYLE,
                "https://media.fightleague.local/combos/notation-test.mp4",
                "Public test combo that exercises every supported notation glyph, direction, repeated hit, air tag, and modifier for visual QA.",
                0, 0, false, false));
        return combos;
    }

    private Combo seedCombo(String title, User creator, Fighter point, Fighter second, String notation,
            ComboDificulty difficulty, FuseType fuse, String mediaUrl, String description, int meterCost, int damage,
            boolean official, boolean privateCombo) {
        Combo combo = findComboByTitle(title);
        if (combo == null) {
            combo = new Combo();
        }
        combo.setTitle(title);
        combo.setDeleted(false);
        combo.setOficial(official);
        combo.setCreatorUserId(creator.getId());
        combo.setPointFighterId(point.getId());
        combo.setSecondFighterId(second.getId());
        combo.setTextNotation(notation);
        combo.setComboDificulty(difficulty);
        combo.setFuse(fuse);
        combo.setMediaUrl(mediaUrl);
        combo.setDescription(description);
        combo.setMeterCost(meterCost);
        combo.setDamage(damage);
        combo.setPrivateCombo(privateCombo);
        combo.setCreatedAt(LocalDate.now().minusDays(7));
        combo.setUpDateAt(LocalDate.now());
        return comboRepository.save(combo);
    }

    private Combo findComboByTitle(String title) {
        return comboRepository.findAll().stream()
                .filter(combo -> title.equals(combo.getTitle()))
                .findFirst()
                .orElse(null);
    }

    private void seedVotes(Map<String, User> users, List<Combo> combos) {
        for (Combo combo : combos) {
            combo.setLikeCounter(0);
            combo.setDislikeCounter(0);
            comboRepository.save(combo);
        }

        vote(combos.get(0), users.get("jinx"), VoteType.LIKE);
        vote(combos.get(0), users.get("ekko"), VoteType.LIKE);
        vote(combos.get(0), users.get("braum"), VoteType.DISLIKE);
        vote(combos.get(1), users.get("akali"), VoteType.LIKE);
        vote(combos.get(1), users.get("ahri"), VoteType.LIKE);
        vote(combos.get(2), users.get("jinx"), VoteType.DISLIKE);
        vote(combos.get(2), users.get("akali"), VoteType.LIKE);
        vote(combos.get(3), users.get("jinx"), VoteType.LIKE);
        vote(combos.get(3), users.get("ekko"), VoteType.LIKE);
        vote(combos.get(3), users.get("caitlyn"), VoteType.LIKE);
        vote(combos.get(5), users.get("ahri"), VoteType.LIKE);
        vote(combos.get(5), users.get("akali"), VoteType.DISLIKE);

        for (Combo combo : combos) {
            combo.setLikeCounter(comboVoteRepository.countByComboIdAndVoteType(combo.getId(), VoteType.LIKE));
            combo.setDislikeCounter(comboVoteRepository.countByComboIdAndVoteType(combo.getId(), VoteType.DISLIKE));
            comboRepository.save(combo);
        }
    }

    private void vote(Combo combo, User user, VoteType voteType) {
        ComboVote vote = comboVoteRepository.findByComboIdAndUserId(combo.getId(), user.getId())
                .orElseGet(() -> new ComboVote(combo.getId(), user.getId(), voteType));
        vote.setVoteType(voteType);
        vote.setCreatedAt(LocalDateTime.now().minusDays(1));
        comboVoteRepository.save(vote);
    }

    private Fighter fighter(String name, String description, String region, String archetype, String title,
            String itLikes, String itDislike, String slug, int health, int range, int power, int vitality,
            int mobility, int easyOfUse) {
        Fighter fighter = new Fighter();
        fighter.setName(name);
        fighter.setDescription(description);
        fighter.setRegion(region);
        fighter.setArchetype(archetype);
        fighter.setTitle(title);
        fighter.setItLikes(itLikes);
        fighter.setItDislike(itDislike);
        fighter.setSlug(slug);
        fighter.setDeleted(false);
        fighter.setHealth(health);
        fighter.setRange(range);
        fighter.setPower(power);
        fighter.setVitality(vitality);
        fighter.setMobility(mobility);
        fighter.setEasyOfUse(easyOfUse);
        return fighter;
    }
}
