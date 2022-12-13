package com.example.demo.pojo;

import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class Dealer {
    @ElementCollection
    private List<CardDeck> dealerHand;
    private int handValue;
    private boolean stay = false;

    //dealer automatically stays if hand value is 17 and over
    public void stay(int handValue) {
        setStay(handValue >= 17);
    }
}
