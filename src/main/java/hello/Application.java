package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

		PlayerState(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof PlayerState && ((PlayerState) obj).x == x && ((PlayerState) obj).y == y;
		}
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
		System.out.println(arenaUpdate);
		String[] commands = new String[] { "F", "R", "L", "T" };
		String me = arenaUpdate._links.self.href;
		PlayerState meState = arenaUpdate.arena.state.get(me);
		int vector = 1;
		String resultCmd = "T";
		switch (meState.direction) {
		case "N":
			vector = 1;
			break;
		case "E":
			vector = 2;
			break;
		case "S":
			vector = 3;
			break;
		case "W":
			vector = 4;
		}
		int maxBotsInRange = 0;
		int botsInRange = findBotsInRange(meState, arenaUpdate, vector);
		if (maxBotsInRange < botsInRange) {
			maxBotsInRange = botsInRange;
		}
		int leftBotsInRange = findBotsInRange(meState, arenaUpdate, turnLeft(vector));
		if (maxBotsInRange < leftBotsInRange) {
			maxBotsInRange = leftBotsInRange;
		}
		int rightBotsInRange = findBotsInRange(meState, arenaUpdate, turnRight(vector));
		if (maxBotsInRange < rightBotsInRange) {
			maxBotsInRange = rightBotsInRange;
		}
		int backBotsInRange = findBotsInRange(meState, arenaUpdate, turnLeft(turnLeft(vector)));
		if (maxBotsInRange < backBotsInRange) {
			maxBotsInRange = backBotsInRange;
		}
		if (maxBotsInRange > 0) {
			if (maxBotsInRange == botsInRange) {
				resultCmd = "T";
			} else if (maxBotsInRange == leftBotsInRange) {
				resultCmd = "L";
			} else if (maxBotsInRange == rightBotsInRange) {
				resultCmd = "R";
			} else {
				resultCmd = "L";
			}
		} else {
			// No bots in range
			PlayerState closestBot = findClosestBot(meState, arenaUpdate);
			int deltaX = meState.x - closestBot.x;
			int deltaY = meState.y - closestBot.y;
			if (deltaY == 0) {
				if (deltaX > 3) {
					// Closest bot is right of me
					// move right
					resultCmd = moveRight(meState);
				} else {
					// closest bot is left of me
					// move left
					resultCmd = moveLeft(meState);
				}
			} else if (deltaX == 0) {
				// closest bot is above or below me
				if (deltaY > 3) {
					// Closest bot is below of me
					// move down
					resultCmd = moveDown(meState);
				} else {
					// closest bot is above of me
					// move up
					resultCmd = moveUp(meState);
				}
			} else if (deltaX < deltaY) {
				if (deltaX > 3) {
					// Closest bot is right of me
					// move right
					resultCmd = moveRight(meState);
				} else {
					// closest bot is left of me
					// move left
					resultCmd = moveLeft(meState);
				}
			} else {
				// closest bot is above or below me
				if (deltaY > 3) {
					// Closest bot is below of me
					// move down
					resultCmd = moveDown(meState);
				} else {
					// closest bot is above of me
					// move up
					resultCmd = moveUp(meState);
				}
			}
		}
		return resultCmd;
	}

	private String moveDown(PlayerState meState) {
		switch (meState.direction) {
		case "N":
			return "R";
		case "E":
			return "R";
		case "S":
			return "F";
		case "W":
			return "L";
		default:
			return "F";
		}
	}

	private String moveUp(PlayerState meState) {
		switch (meState.direction) {
		case "N":
			return "F";
		case "E":
			return "L";
		case "S":
			return "R";
		case "W":
			return "R";
		default:
			return "F";
		}
	}

	private String moveRight(PlayerState meState) {
		switch (meState.direction) {
		case "N":
			return "R";
		case "E":
			return "F";
		case "S":
			return "L";
		case "W":
			return "R";
		default:
			return "F";
		}
	}

	private String moveLeft(PlayerState meState) {
		switch (meState.direction) {
		case "N":
			return "L";
		case "E":
			return "R";
		case "S":
			return "R";
		case "W":
			return "F";
		default:
			return "F";
		}
	}

	private PlayerState findClosestBot(PlayerState meState, ArenaUpdate arenaUpdate) {
		Collection<PlayerState> playerStates = arenaUpdate.arena.state.values();
		List<PlayerState> players = playerStates.stream().filter(v -> !v.equals(meState))
				.collect(Collectors.toCollection(ArrayList::new));
		Collections.sort(players, (o1, o2) -> {
			int D1 = Math.abs(meState.x - ((PlayerState) o1).x) + Math.abs(meState.y - ((PlayerState) o1).y);
			int D2 = Math.abs(meState.x - ((PlayerState) o2).x) + Math.abs(meState.y - ((PlayerState) o2).y);
			if (D1 < D2)
				return -1;
			else if (D1 > D2)
				return 1;
			else
				return 0;
		});
		return players.get(0);
	}

	private int findBotsInRange(PlayerState meState, ArenaUpdate arenaUpdate, int vector) {
		PlayerState[] line = new PlayerState[3];
		getLine(meState, line, vector);
		int nLineBots = 0;
		Collection<PlayerState> playerStates = arenaUpdate.arena.state.values();
		for (PlayerState checkPos : line) {
			if (playerStates.contains(checkPos)) {
				nLineBots++;
			}
		}
		return nLineBots;
	}

	private int turnLeft(int vector) {
		int result = vector - 1;
		if (result < 0)
			result = 4;
		return result;
	}

	private int turnRight(int vector) {
		int result = vector + 1;
		if (result > 4)
			result = 1;
		return result;
	}

	private void getLine(PlayerState meState, PlayerState[] line, int vector) {
		switch (vector) {
		case 1:
			line[0] = new PlayerState(meState.x, meState.y - 1);
			line[1] = new PlayerState(meState.x, meState.y - 2);
			line[2] = new PlayerState(meState.x, meState.y - 3);
			break;
		case 3:
			line[0] = new PlayerState(meState.x, meState.y + 1);
			line[1] = new PlayerState(meState.x, meState.y + 2);
			line[2] = new PlayerState(meState.x, meState.y + 3);
			break;
		case 2:
			line[0] = new PlayerState(meState.x + 1, meState.y);
			line[1] = new PlayerState(meState.x + 2, meState.y);
			line[2] = new PlayerState(meState.x + 3, meState.y);
			break;
		case 4:
			line[0] = new PlayerState(meState.x - 1, meState.y);
			line[1] = new PlayerState(meState.x + 2, meState.y);
			line[2] = new PlayerState(meState.x - 3, meState.y);
			break;
		}
	}

}
