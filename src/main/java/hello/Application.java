package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

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
    

    System.out.println(arenaUpdate._links.self.href);
    System.out.println(arenaUpdate.arena.dims);
    System.out.println(arenaUpdate.arena.state.values());
    System.out.println(arenaUpdate.arena.state.get("https://foo.com").x);
    
    // String[] commands = new String[]{"F", "R", "L", "T"};
    String[] commands = new String[]{"F", "T", "R", "T", "L", "T"};
    String command = "F";
    int i = new Random().nextInt(6);

    switch (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction) {
      case "N":
        System.out.println("North");
        break;
      case "W":
        
        break;
      case "S":
        
        break;
      case "E":
        
        break;
    
      default:

        break;
    }
    // TODO add your implementation here to replace the random response. 
    // return commands[i];
    return "T";
  }

}

