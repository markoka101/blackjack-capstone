package com.example.demo.service;


import com.example.demo.entity.Player;
import com.example.demo.pojo.CardDeck;

import java.util.List;

public interface PlayerService {
    Player findById(Long id);
    Player findByUserId(Long userId);
    Player createPlayer(Player player);

    //gameplay
    Player addToHand(Long playerId, CardDeck card);
    CardDeck removeFromHand(Player player);
    Player bet(Long playerId, Integer betCredit);
    void creditsBack(Player player, Integer credit);

    Player stay(Long playerId);
}
