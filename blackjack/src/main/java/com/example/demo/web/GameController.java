package com.example.demo.web;

import com.example.demo.entity.Game;
import com.example.demo.entity.Player;
import com.example.demo.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Set;

@AllArgsConstructor
@RestController
@CrossOrigin(
        origins = "http://localhost:4080", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
@RequestMapping("/game")
public class GameController {

    GameService gameService;

    //get specific game
    @GetMapping("/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Game> getGame(@PathVariable Long id) {
        return new ResponseEntity<>(gameService.getGame(id), HttpStatus.OK);
    }

    //list players in game
    @GetMapping("/{gameid}/players")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Set<Player>> getPlayers(@PathVariable Long gameid) {
        return new ResponseEntity<>(gameService.getPlayers(gameid),HttpStatus.OK);
    }

    //create game
    @PostMapping("/create")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) {
        return new ResponseEntity<>(gameService.createGame(game), HttpStatus.CREATED);
    }

    //delete game
    @DeleteMapping("/delete/{gameid}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @Transactional
    public ResponseEntity<?> deleteGame(@PathVariable Long gameid) {
        gameService.deleteGame(gameid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //add player to game
    @PostMapping("/{gameid}/add/{userid}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> addPlayer(@PathVariable Long gameid, @PathVariable Long userid) {
        gameService.addPlayer(gameid, userid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //add player to game information object
    @PostMapping("/{gameid}/playervisual/{playerid}")
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<?> addPlayerVisual(@PathVariable Long gameid, @PathVariable Long playerid) {
        gameService.setPlayerInfo(gameid, playerid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //add players bets to pot
    @PutMapping("/{gameid}/addbets")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Integer> placedBets(@PathVariable Long gameid) {
        return new ResponseEntity<>(gameService.placedBets(gameid), HttpStatus.CREATED);
    }
    //deal
    @PutMapping("/{gameid}/deal")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Game> deal(@PathVariable Long gameid) {
        gameService.deal(gameid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //end the hand
    @GetMapping("/{gameid}/endhand")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public String endHand(@PathVariable Long gameid) {
        return gameService.endHand(gameid);
    }

    //deal card to player who hit
    @PutMapping("/{gameid}/hit/{playerid}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Game> hit(@PathVariable Long gameid, @PathVariable Long playerid) {
        gameService.hitDeal(gameid, playerid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //deal card to dealer
    @PutMapping("/{gameid}/dealerhit")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Game> dealerHit(@PathVariable Long gameid) {
        gameService.hitDealer(gameid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //check if everyone is done with cards
    @GetMapping("/{gameid}/allstay")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Boolean> allStay(@PathVariable Long gameid) {
        return new ResponseEntity<Boolean>(gameService.allStay(gameid), HttpStatus.OK);
    }

}
