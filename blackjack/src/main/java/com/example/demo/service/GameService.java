package com.example.demo.service;


import com.example.demo.entity.Game;
import com.example.demo.entity.Player;
import com.example.demo.entity.User;

import java.util.List;
import java.util.Set;

public interface GameService {
    //game information
    Game getGame(Long id);
    Game createGame(Game game);
    void deleteGame(Long id);

    //players
    Game addPlayer(Long gameId, Long userId);
    Set<Player> getPlayers(Long id);

    //credit logic
    Integer placedBets(Long gameId);

    //card logic
    void deal(Long gameId);
    void backToDeck(Game game);
    void hitDeal(Long gameId, Long playerId);
    void hitDealer(Long gameId);


    //end hand
    boolean allStay(Long gameId);
    String endHand(Long gameId);
    String winner(Game game, Integer pot);

    /*
    TEST FUNCTIONS FOR DISPLAYING INFORMATION FOR FRONT_END
     */

}
