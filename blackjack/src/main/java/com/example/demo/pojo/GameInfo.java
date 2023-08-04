package com.example.demo.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
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

    public void increaseAmt() {
        this.playerAmt = playerAmt + 1;
    }

    public void decreaseAmt() {
        this.playerAmt = playerAmt - 1;
    }

    public void addPlayer(Long id, String name) {
        PlayerInfo playerInfo = new PlayerInfo(id, name);
        playersInfo.add(playerInfo);
    }

    public void addToPlayerHand(String name, CardDeck card) {

        for (PlayerInfo player : playersInfo) {
            if (player.getName().equals(name)) {
                player.getPlayerHand().add(card);
                player.setHandValue(player.getHandValue() + card.getCardValue());
            }
        }

    }
}
