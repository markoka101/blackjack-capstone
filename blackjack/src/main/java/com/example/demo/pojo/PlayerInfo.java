package com.example.demo.pojo;

import javax.persistence.ElementCollection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerInfo implements Serializable {
    private Long id;
    private String name;

    @ElementCollection
    private static List<CardDeck> pHand = new ArrayList<>();
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

    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }
}