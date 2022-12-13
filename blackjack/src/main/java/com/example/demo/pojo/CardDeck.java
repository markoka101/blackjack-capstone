package com.example.demo.pojo;


import lombok.*;

import javax.persistence.Embeddable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//make each individual card an object in deck
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class CardDeck {

    private String suit;
    private String cardName;
    private int cardValue;
    private String imageUrl;

    //create a list of card objects
    public List<CardDeck> createDeck() {
        CardDeck[] cards = new CardDeck[] {
                new CardDeck("spade", "2", 2, "2_of_spades.png"),
                new CardDeck("club", "2", 2, "2_of_clubs.png"),
                new CardDeck("diamond", "2", 2, "2_of_diamonds.png"),
                new CardDeck("heart", "2", 2,"2_of_hearts.png"),
                new CardDeck("spade", "3", 3, "3_of_spades.png"),
                new CardDeck("club", "3", 3, "3_of_clubs.png"),
                new CardDeck("diamond", "3", 3, "3_of_diamonds.png"),
                new CardDeck("heart", "3", 3,"3_of_hearts.png"),
                new CardDeck("spade", "4", 4, "4_of_spades.png"),
                new CardDeck("club", "4", 4, "4_of_clubs.png"),
                new CardDeck("diamond", "4", 4, "4_of_diamonds.png"),
                new CardDeck("heart", "4", 4,"4_of_hearts.png"),
                new CardDeck("spade", "5", 5, "5_of_spades.png"),
                new CardDeck("club", "5", 5, "5_of_clubs.png"),
                new CardDeck("diamond", "5", 5, "5_of_diamonds.png"),
                new CardDeck("heart", "5", 5,"5_of_hearts.png"),
                new CardDeck("spade", "6", 6, "6_of_spades.png"),
                new CardDeck("club", "6", 6, "6_of_clubs.png"),
                new CardDeck("diamond", "6", 6, "6_of_diamonds.png"),
                new CardDeck("heart", "6", 6,"6_of_hearts.png"),
                new CardDeck("spade", "7", 7, "7_of_spades.png"),
                new CardDeck("club", "7", 7, "7_of_clubs.png"),
                new CardDeck("diamond", "7", 7, "7_of_diamonds.png"),
                new CardDeck("heart", "7", 7,"7_of_hearts.png"),
                new CardDeck("spade", "8", 8, "8_of_spades.png"),
                new CardDeck("club", "8", 8, "8_of_clubs.png"),
                new CardDeck("diamond", "8", 8, "8_of_diamonds.png"),
                new CardDeck("heart", "8", 8,"8_of_hearts.png"),
                new CardDeck("spade", "9", 9, "9_of_spades.png"),
                new CardDeck("club", "9", 9, "9_of_clubs.png"),
                new CardDeck("diamond", "9", 9, "9_of_diamonds.png"),
                new CardDeck("heart", "9", 9,"9_of_hearts.png"),
                new CardDeck("spade", "10", 10, "10_of_spades.png"),
                new CardDeck("club", "10", 10, "10_of_clubs.png"),
                new CardDeck("diamond", "10", 10, "10_of_diamonds.png"),
                new CardDeck("heart", "10", 10,"10_of_hearts.png"),
                new CardDeck("spade", "jack", 10, "jack_of_spades.png"),
                new CardDeck("club", "jack", 10, "jack_of_clubs.png"),
                new CardDeck("diamond", "jack", 10, "jack_of_diamonds.png"),
                new CardDeck("heart", "jack", 10,"jack_of_hearts.png"),
                new CardDeck("spade", "queen", 10, "queen_of_spades.png"),
                new CardDeck("club", "queen", 10, "queen_of_clubs.png"),
                new CardDeck("diamond", "queen", 10, "queen_of_diamonds.png"),
                new CardDeck("heart", "queen", 10,"queen_of_hearts.png"),
                new CardDeck("spade", "king", 10, "king_of_spades.png"),
                new CardDeck("club", "king", 10, "king_of_clubs.png"),
                new CardDeck("diamond", "king", 10, "king_of_diamonds.png"),
                new CardDeck("heart", "king", 10,"king_of_hearts.png"),
                new CardDeck("spade", "ace", 11, "ace_of_spades.png"),
                new CardDeck("club", "ace", 11, "ace_of_clubs.png"),
                new CardDeck("diamond", "ace", 11, "ace_of_diamonds.png"),
                new CardDeck("heart", "ace", 11,"ace_of_hearts.png")
        };
        return Arrays.stream(cards).toList();
    }

    //shuffle cards
    public void shuffle(List<CardDeck> deck) {
        Random random = new Random();

        for (int i = deck.size()-1; i > 0; i--) {
            int randomIndex = random.nextInt(i+1);

            CardDeck temp = deck.get(i);
            deck.set(i, deck.get(randomIndex));
            deck.set(randomIndex, temp);
        }
    }
}
