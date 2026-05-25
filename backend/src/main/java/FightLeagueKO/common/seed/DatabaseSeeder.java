package FightLeagueKO.common.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import FightLeagueKO.fighter.model.Fighter;
import FightLeagueKO.fighter.repository.FighterRepositoryPostgre;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final FighterRepositoryPostgre fighterRepository;

    public DatabaseSeeder(FighterRepositoryPostgre fighterRepository) {
        this.fighterRepository = fighterRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Seeding official 2XKO fighter roster...");
        seedFighters();
        log.info("Fighter seeding complete.");
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
