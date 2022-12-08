package hello;

import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

	static class Self {
		public String href;
	}

	static class Links {
		public Self self;
	}

	static class PlayerState {
		public Integer x;
		public Integer y;
		public String direction;
		public Boolean wasHit;
		public Integer score;
	}

	static class Arena {
		public List<Integer> dims;
		public Map<String, PlayerState> state;
	}

	static class ArenaUpdate {
		public Links _links;
		public Arena arena;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.initDirectFieldAccess();
	}

	@GetMapping("/")
	public String index() {
		return "Let the battle begin!";
	}

	@PostMapping("/**")
	public String index(@RequestBody ArenaUpdate arenaUpdate) {
		if (canThrow(arenaUpdate)) {
			return "T";
		} else {
			return move(arenaUpdate);
		}
	}

	private String move(ArenaUpdate arenaUpdate) {
		if (areEnemiesToTheLeft(arenaUpdate)) {
			return "L";
		}
		if (areEnemiesToTheRight(arenaUpdate)) {
			return "R";
		}
		if (areEnemiesToTheBack(arenaUpdate)) {
			return "L";
		} else {
			return "F";
		}
	}

	private boolean areEnemiesToTheBack(ArenaUpdate arenaUpdate) {

		PlayerState self = arenaUpdate.arena.state.entrySet().stream()
				.filter(x -> x.getKey().equals(arenaUpdate._links.self.href)).findFirst().get().getValue();

		return arenaUpdate.arena.state.entrySet().stream().filter(x -> !x.getKey().equals(arenaUpdate._links.self.href))
				.anyMatch(x -> isEnemyToTheBack(self, x.getValue()));
	}

	private boolean isEnemyToTheBack(PlayerState self, PlayerState opponent) {

		switch (self.direction) {
		case "N":
			return self.x == opponent.x && self.y < opponent.y && self.y + 3 >= opponent.y;
		case "S":
			return self.x == opponent.x && self.y > opponent.y && self.y - 3 <= opponent.y;
		case "E":
			return self.x > opponent.x && self.x + 3 <= opponent.x && self.y == opponent.y;
		case "W":
			return self.x < opponent.x && self.x + 3 >= opponent.x && self.y == opponent.y;
		default:
			System.out.println("WARNING unknown direction: " + self.direction);
			return false;
		}
	}

	private boolean areEnemiesToTheRight(ArenaUpdate arenaUpdate) {

		PlayerState self = arenaUpdate.arena.state.entrySet().stream()
				.filter(x -> x.getKey().equals(arenaUpdate._links.self.href)).findFirst().get().getValue();

		return arenaUpdate.arena.state.entrySet().stream().filter(x -> !x.getKey().equals(arenaUpdate._links.self.href))
				.anyMatch(x -> isEnemyToTheRight(self, x.getValue()));
	}

	private boolean isEnemyToTheRight(PlayerState self, PlayerState opponent) {
		switch (self.direction) {
		case "E":
			return self.x.equals(opponent.x) && self.y > opponent.y && self.y + 3 <= opponent.y;
		case "W":
			return self.x.equals(opponent.x) && self.y < opponent.y && self.y - 3 >= opponent.y;
		case "N":
			return self.x > opponent.x && self.x + 3 <= opponent.x && self.y == opponent.y;
		case "S":
			return self.x > opponent.x && self.x - 3 <= opponent.x && self.y == opponent.y;
		default:
			System.out.println("WARNING unknown direction: " + self.direction);
			return false;
		}
	}

	private boolean areEnemiesToTheLeft(ArenaUpdate arenaUpdate) {
		PlayerState self = arenaUpdate.arena.state.entrySet().stream()
				.filter(x -> x.getKey().equals(arenaUpdate._links.self.href)).findFirst().get().getValue();

        System.out.println("areEnemiesToTheLeft" + self);

		return arenaUpdate.arena.state.entrySet().stream().filter(x -> !x.getKey().equals(arenaUpdate._links.self.href))
				.anyMatch(x -> isEnemyToTheLeft(self, x.getValue()));
	}

	private boolean isEnemyToTheLeft(PlayerState self, PlayerState opponent) {
		switch (self.direction) {
		case "E":
			return self.x.equals(opponent.x) && self.y > opponent.y && self.y - 3 <= opponent.y;
		case "W":
			return self.x.equals(opponent.x) && self.y < opponent.y && self.y + 3 >= opponent.y;
		case "N":
			return self.x > opponent.x && self.x - 3 <= opponent.x && self.y == opponent.y;
		case "S":
			return self.x > opponent.x && self.x + 3 <= opponent.x && self.y == opponent.y;
		default:
			System.out.println("WARNING unknown direction: " + self.direction);
			return false;
		}
	}

	private boolean canThrow(ArenaUpdate arenaUpdate) {
		PlayerState self = arenaUpdate.arena.state.entrySet().stream()
				.filter(x -> x.getKey().equals(arenaUpdate._links.self.href)).findFirst().get().getValue();

		return arenaUpdate.arena.state.entrySet().stream().filter(x -> !x.getKey().equals(arenaUpdate._links.self.href))
				.anyMatch(x -> isInFront(self, x.getValue()));
	}

	private boolean isInFront(PlayerState self, PlayerState opponent) {
		switch (self.direction) {
		case "N":
			return self.x == opponent.x && self.y > opponent.y && self.y - 3 <= opponent.y;
		case "S":
			return self.x == opponent.x && self.y < opponent.y && self.y + 3 >= opponent.y;
		case "W":
			return self.x > opponent.x && self.x - 3 <= opponent.x && self.y == opponent.y;
		case "E":
			return self.x > opponent.x && self.x + 3 <= opponent.x && self.y == opponent.y;
		default:
			System.out.println("WARNING unknown direction: " + self.direction);
			return false;
		}
	}

}