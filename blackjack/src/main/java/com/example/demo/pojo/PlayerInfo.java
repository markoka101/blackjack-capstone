package com.example.demo.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.ElementCollection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.pojo.CardDeck;

public class PlayerInfo implements Serializable {
    private Long id;
    private String name;

    @ElementCollection
    private static List<CardDeck> pHand = new ArrayList<CardDeck>();
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

    @ElementCollection
    public List<CardDeck> getPHand() {
        return pHand;
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

    public void setPHand(List<CardDeck> playerHand) {
        pHand = playerHand;
    }

    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }
}