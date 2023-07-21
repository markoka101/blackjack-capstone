package com.example.demo.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class PlayerInfo implements Serializable {
    private Long id;
    private String name;

    private List<CardDeck> playerHand = new ArrayList<>();
    private int handValue = 0;

    public PlayerInfo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    //getters
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public List<CardDeck> getPlayerHand() {
        return this.getPlayerHand();
    }

    public int getHandValue() {
        return this.handValue;
    }

    //setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayerHand(List<CardDeck> playerHand) {
        this.playerHand = playerHand;
    }

    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }
}