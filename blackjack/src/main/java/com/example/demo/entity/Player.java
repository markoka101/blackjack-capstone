package com.example.demo.entity;

import com.example.demo.pojo.CardDeck;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    @NonNull
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @NonNull
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Game game;

    @NonNull
    @Column(name = "bet")
    private Integer bet;

    @Column(name = "hand")
    @ElementCollection
    private List<CardDeck> hand;

    @NonNull
    @Column(name = "hand_value")
    private Integer handValue;

    @Column(name = "stay")
    private Boolean stay = false;
}
