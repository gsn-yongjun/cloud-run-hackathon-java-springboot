package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;

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
		return "Let the battle begin! Here are some cool new changes!";
	}

	@PostMapping("/**")
	public String index(@RequestBody ArenaUpdate arenaUpdate) {

		// Get self state
		String selfKey = arenaUpdate._links.self.href;
		PlayerState selfState = arenaUpdate.arena.state.get(selfKey);

		// If you got hit, move away
		if (selfState.wasHit) {
			String[] commands = new String[] { "F", "R", "L" };
			Random r = new Random();
			int i = r.nextInt(3);
			return commands[i];
		}

		// Create Iterator
		Iterator<Map.Entry<String, PlayerState>> stateIt = arenaUpdate.arena.state.entrySet().iterator();

		// Iterate through map to see other player positions
		while (stateIt.hasNext()) {
			Map.Entry<String, PlayerState> playerState = stateIt.next();

			// Skip if it's your own player
			if (playerState.getKey().equals(selfKey)) {
				continue;
			}

			// Check if player is in your row/col
			int posX = playerState.getValue().x;
			int posY = playerState.getValue().y;

			// Same Column(x)
			if ((posX == selfState.x) && (Math.abs(posY - selfState.y) <= 3)) {
				// Check if same direction
				if (selfState.direction.equals("N")) {
					if (posY < selfState.y) {
						// Shoot water
						return "T";
					}
				} else if (selfState.direction.equals("S")) {
					if (posY > selfState.y) {
						return "T";
					}
				}
			}

			// Same Row(y)
			if ((posY == selfState.y) && (Math.abs(posX - selfState.x) <= 3)) {
				// Check if same direction
				if (selfState.direction.equals("W")) {
					if (posX < selfState.x) {
						return "T";
					}
				} else if (selfState.direction.equals("E")) {
					if (posX > selfState.x) {
						return "T";
					}
				}
			}
		}

		// Do something random if none of the conditions match
		String[] commands = new String[] { "F", "R", "L" };
		Random r = new Random();
		int i = r.nextInt(3);
		return commands[i];
	}
}
