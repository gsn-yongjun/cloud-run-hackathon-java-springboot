package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    System.out.println(arenaUpdate.arena.dims.get(1));
    System.out.println(arenaUpdate.arena.state.values());
    
    // String[] commands = new String[]{"F", "R", "L", "T"};
    String[] commands = new String[]{"F", "T", "R", "T", "L", "T"};
    String command = "F";
    int i = new Random().nextInt(6);

    System.out.println(arenaUpdate.arena.state.keySet());

    List<String> keyList = new ArrayList<>();
    List<Integer> arrX = new ArrayList<>();
    List<Integer> arrY = new ArrayList<>();
    keyList.addAll(arenaUpdate.arena.state.keySet());

    for(int idx=0; idx < keyList.size(); idx++){

      if(arenaUpdate._links.self.href!=keyList.get(idx)){
        System.out.println(arenaUpdate.arena.state.get(keyList.get(idx)).x);
        System.out.println(arenaUpdate.arena.state.get(keyList.get(idx)).y);
        arrX.add(arenaUpdate.arena.state.get(keyList.get(idx)).x);
        arrY.add(arenaUpdate.arena.state.get(keyList.get(idx)).y);
        }
    }

    int myX = arenaUpdate.arena.state.get(arenaUpdate._links.self.href).x;
    int myY = arenaUpdate.arena.state.get(arenaUpdate._links.self.href).y;

    switch (arenaUpdate.arena.state.get(arenaUpdate._links.self.href).direction) {
      case "N":
          if(myY == 0){
            command = "R";
          }
          for(int idx=0; idx < arrY.size(); idx++){
            if(myY-arrY.get(idx) > 0 && myY-arrY.get(idx) < 3){
              command = "T";
            }else if(myY == 0){
              command = "R";
            }else{
              command = "F";
            }
          }
        break;

      case "W":
          if(myX == 0){
            command = "R";
          }else{
            command = "F";
          }
          for(int idx=0; idx < arrX.size(); idx++){
            if(myX-arrX.get(idx) > 0 && myX-arrX.get(idx) < 3){
              command = "T";
            }
          }
        break;


      case "S":

          if(myY == arenaUpdate.arena.dims.get(1)){
            command = "L";
          }else{
            command = "F";
          }

          for(int idx=0; idx < arrY.size(); idx++){
            if(myY-arrY.get(idx) < 0 && myY-arrY.get(idx) > -3){
              command = "T";
            }
          }
        break;


      case "E":
        if(myX == arenaUpdate.arena.dims.get(0)){
          command = "R";
        }else{
          command = "F";
        }

        for(int idx=0; idx < arrX.size(); idx++ ){
          if(myX-arrX.get(idx) < 0 && myX-arrX.get(idx) > -3){
            command = "T";
          }
        }

        break;

      default:
        command = "F";
        break;
    }
    // TODO add your implementation here to replace the random response. 
    // return commands[i];
    return command;
  }


}

