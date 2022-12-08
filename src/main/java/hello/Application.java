package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
		System.out.println(arenaUpdate);
		System.out.println("Dim: " + arenaUpdate.arena.dims.toString());
		Map<String, PlayerState> state = arenaUpdate.arena.state;

		int dim_x = arenaUpdate.arena.dims.get(0);
		int dim_y = arenaUpdate.arena.dims.get(1);

		int my_x = 0;
		int my_y = 0;
		String my_dir = "N";
		boolean my_washit = false;

		Set<PlayerState> nsSet = new HashSet<PlayerState>();
		Set<PlayerState> weSet = new HashSet<PlayerState>();

		System.out.println("Player count: " + state.size());

		for (String str : state.keySet()) {
			PlayerState player = state.get(str);
			String me = "";
			if (str.contains("so6zjyq3bq")) {
				my_x = player.x;
				my_y = player.y;
				my_dir = player.direction;
				my_washit = player.wasHit;
				me = "ME->";
			}

			if (my_y == player.y && Math.abs(my_x - player.x) <= 3) {
				nsSet.add(player);
			}

			if (my_x == player.x && Math.abs(my_y - player.y) <= 3) {
				weSet.add(player);
			}

			System.out.println(me + "URL= " + str + ", dir=" + player.direction + ", x=" + player.x + ", y=" + player.y
					+ ", washit=" + player.wasHit + ", score=" + player.score);
		}
		System.out.println("My stats are x,y,dir,washit: " + my_x + ", " + my_y + ", " + my_dir + ", " + my_washit);

		boolean canThrow = false;

		for (String str : state.keySet()) {

			PlayerState player = state.get(str);

			if (!str.contains("so6zjyq3bq")) {
//    		PlayerState player = state.get(str);
				int x1 = player.x;
				int y1 = player.y;
//    		double distance_sq = Math.pow( Math.abs(my_x-x1), 2) + Math.pow(Math.abs(my_y- y1), 2);
//    		double distance = Math.sqrt(distance_sq);
//    		System.out.println("Opponent in range, " + distance + ", " + str + " , Throw is set");
//    		if(distance<=4) {
//    			canThrow=true;
//    			
//    		}

				if (my_dir.equals("E") && my_y == y1 && x1 - my_x <= 3 && (x1 > my_x)) {
					System.out.println("Opponent in range (" + x1 + "," + y1 + "), me (" + my_x + "," + my_y + ") "
							+ str + " , Throw is set");
					canThrow = true;
					break;
				}
				if (my_dir.equals("W") && my_y == y1 && (my_x - x1) <= 3 & (my_x > x1)) {
					System.out.println("Opponent in range (" + x1 + "," + y1 + "), me (" + my_x + "," + my_y + ") "
							+ str + " , Throw is set");
					canThrow = true;
					break;
				}
				if (my_dir.equals("N") && my_x == x1 && my_y - y1 <= 3 && (my_y > y1)) {
					System.out.println("Opponent in range (" + x1 + "," + y1 + "), me (" + my_x + "," + my_y + ") "
							+ str + " , Throw is set");
					canThrow = true;
					break;
				}
				if (my_dir.equals("S") && my_x == x1 && y1 - my_y <= 3 && (y1 > my_y)) {
					System.out.println("Opponent in range (" + x1 + "," + y1 + "), me (" + my_x + "," + my_y + ") "
							+ str + " , Throw is set");
					canThrow = true;
					break;
				}
			}
		}

		String[] commands = new String[] { "F", "R", "L", "T" };

		if (my_washit) {
			String[] move_commands = new String[] { "F", "R", "L" };
			int i = new Random().nextInt(3);
			System.out.println("washit true, random action: " + move_commands[i]);
			return move_commands[i];
		}

		boolean[] luck = new boolean[] { false, false, false, false, false, false, true };
		int randLuck = new Random().nextInt(5);
		boolean randomReturn = luck[randLuck];
		if (randomReturn) {
			int i = new Random().nextInt(4);
			System.out.println("randomReturn: " + randomReturn + ", random move: " + commands[i]);
			return commands[i];
		} else {

			System.out.println("randomReturn: " + randomReturn + ", standard logic.");
		}

		if (canThrow) {
			System.out.println("Throw Water");
			return "T";
		} else {

			String move = "F";

			/* look for opp in 4 directions */
//    	if( my_dir.equals("N"))
			for (PlayerState player : nsSet) {
				if (my_dir.equals("N") && player.x < my_x) {
					move = "L";
					System.out.println("Turning L towards player (" + player.x + "," + player.y + ")");
					break;
				}
				if (my_dir.equals("N") && my_x < player.x) {
					move = "R";
					System.out.println("Turning R towards player (" + player.x + "," + player.y + ")");
					break;
				}

				if (my_dir.equals("S") && player.x < my_x) {
					move = "R";
					System.out.println("Turning R towards player (" + player.x + "," + player.y + ")");
					break;
				}
				if (my_dir.equals("S") && player.x > my_x) {
					move = "L";
					System.out.println("Turning L towards player (" + player.x + "," + player.y + ")");
					break;
				}
			}

			// return only if there is a change
			if (!move.equals("F")) {
				return move;
			}

			/* no opponent found, default is F */

			System.out.println("No opp in range, deciding next move.");
			move = "F";

			// moving Southwards
			if (my_dir.equals("S") && dim_y - my_y <= 3) {
				if (dim_x / 2 > my_x) {
					move = "L";
				} else {
					move = "R";
				}
				System.out.println("[S]Move set to: " + move);
			}

			// moving Northwards
			if (my_dir.equals("N") && my_y <= 3) {
				if (dim_x / 2 > my_x) {
					move = "R";
				} else {
					move = "L";
				}
				System.out.println("[N]Move set to: " + move);
			}

			// moving East
			if (my_dir.equals("E") && dim_x - my_x <= 3) {
				if (dim_y / 2 > my_y) {
					move = "R";
				} else {
					move = "L";
				}
				System.out.println("[E]Move set to: " + move);
			}

			// moving West
			if (my_dir.equals("W") && my_x <= 3) {
				if (dim_y / 2 > my_y) {
					move = "L";
				} else {
					move = "R";
				}
				System.out.println("[W]Move set to: " + move);
			}

			System.out.println("Final Move: " + move);
			return move;
//    	int i = new Random().nextInt(4);
//    	return commands[i];
		}
	}

}
