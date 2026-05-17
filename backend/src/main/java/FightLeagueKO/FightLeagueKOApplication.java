package FightLeagueKO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FightLeagueKOApplication {

	public static void main(String[] args) {
		SpringApplication.run(FightLeagueKOApplication.class, args);
	}

}