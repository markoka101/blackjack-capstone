package com.example.demo.pojo;

import com.example.demo.entity.Game;
import com.example.demo.repository.GameRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.*;



/*
This class is used to allow front end to access game information
easily.This will manually store the important information
for the visible aspect of the application.
This will be used to display cards and info even after the hand has finished
 */
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class GameInfo {
    private int playerAmt = 0;
    private String winner = "";


    //hold dealer hand and value
    @ElementCollection
    private List<CardDeck> dHand;
    private int dValue = 0;

    //hold playerInfo objects
    @Lob
    private ArrayList<PlayerInfo> playersInfo = new ArrayList<>();

    //increase amount of players in game
    public void increaseAmt() {
        this.playerAmt = playerAmt + 1;
    }

    //decrease amount of players
    public void decreaseAmt() {
        this.playerAmt = playerAmt - 1;
    }

    //add player to visual object
    public void addPlayer(Long id, String name) {
        PlayerInfo playerInfo = new PlayerInfo(id, name);
        playersInfo.add(playerInfo);
    }

    //add the hand value to visual player object
    public void addToPlayerHand(String name, List<CardDeck> hand, int hValue) {

        for (PlayerInfo player : playersInfo) {
            if (player.getName().equals(name)) {
                player.getPHand().addAll(hand);
                player.setHandValue(hValue);
            }
        }

    }
}
