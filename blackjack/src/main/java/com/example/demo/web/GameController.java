package com.example.demo.web;

import com.example.demo.entity.Game;
import com.example.demo.entity.Player;
import com.example.demo.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/game")
public class GameController {

    GameService gameService;

    //get specific game
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable Long id) {
        return new ResponseEntity<>(gameService.getGame(id), HttpStatus.OK);
    }

    //list players in game
    @GetMapping("/{gameid}/players")
    public ResponseEntity<Set<Player>> getPlayers(@PathVariable Long gameid) {
        return new ResponseEntity<>(gameService.getPlayers(gameid),HttpStatus.OK);
    }

    //create game
    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) {
        return new ResponseEntity<>(gameService.createGame(game), HttpStatus.CREATED);
    }

    //add player to game
    @PostMapping("/{gameid}/add/{userid}")
    public ResponseEntity<Game> addPlayer(@PathVariable Long gameid, @PathVariable Long userid) {
        return new ResponseEntity<>(gameService.addPlayer(gameid, userid), HttpStatus.OK);
    }

    //add players bets to pot
    @PutMapping("/{gameid}/addbets")
    public ResponseEntity<Integer> placedBets(@PathVariable Long gameid) {
        return new ResponseEntity<>(gameService.placedBets(gameid), HttpStatus.CREATED);
    }
    //deal
    @PutMapping("/{gameid}/deal")
    public ResponseEntity<Game> deal(@PathVariable Long gameid) {
        gameService.deal(gameid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //emd the hand
    @GetMapping("/{gameid}/endhand")
    public String endHand(@PathVariable Long gameid) {
        return gameService.endHand(gameid);
    }

    //deal card to player who hit
    @PutMapping("/{gameid}/hit/{playerid}")
    public ResponseEntity<Game> hit(@PathVariable Long gameid, @PathVariable Long playerid) {
        gameService.hitDeal(gameid, playerid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //deal card to dealer
    @PutMapping("/{gameid}/dealerhit")
    public ResponseEntity<Game> dealerHit(@PathVariable Long gameid) {
        gameService.hitDealer(gameid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //check if everyone is done with cards
    @GetMapping("/{gameid}/allstay")
    public ResponseEntity<Boolean> allStay(@PathVariable Long gameid) {
        return new ResponseEntity<Boolean>(gameService.allStay(gameid), HttpStatus.OK);
    }

}
