package com.example.demo.entity;

import com.example.demo.pojo.CardDeck;
import com.example.demo.pojo.Dealer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //set of all players
    @JsonIgnore
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private Set<Player> players;

    @Column(name = "dealer")
    private Dealer dealer;

    //since pot will at zero it cant be null
    @NonNull
    @Column(name = "pot")
    private Integer pot;

    //hold the deck
    @Column(name = "deck")
    @ElementCollection
    private List<CardDeck> deck;



}
