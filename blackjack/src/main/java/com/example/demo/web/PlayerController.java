package com.example.demo.web;

import com.example.demo.entity.Player;
import com.example.demo.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/player")
public class PlayerController {

    PlayerService playerService;

    @PutMapping("/{playerid}/bet/{amount}")
    public ResponseEntity<Player> bet(@PathVariable Long playerid, @PathVariable Integer amount) {
        return new ResponseEntity<>(playerService.bet(playerid, amount), HttpStatus.OK);
    }

    @GetMapping("/findbyuser/{userid}")
    public Long findByUserId(@PathVariable Long userid) {
        if (playerService.findByUserId(userid) != null) {
            return playerService.findByUserId(userid).getId();
        }
        return 0L;
    }

    @GetMapping("/getplayer/{playerid}")
    public Player getPlayer(@PathVariable Long playerid) {
        return playerService.findById(playerid);
    }

    @PutMapping("/{playerid}/stay")
    public ResponseEntity<Player> bet(@PathVariable Long playerid) {
        return new ResponseEntity<>(playerService.stay(playerid), HttpStatus.OK);
    }
}
