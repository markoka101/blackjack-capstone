package com.example.demo.service;


import com.example.demo.entity.Player;
import com.example.demo.entity.User;
import com.example.demo.pojo.CardDeck;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PlayerServiceImpl implements PlayerService{

    private PlayerRepository playerRepository;
    private UserRepository userRepository;

    //entity manager
    private EntityManager entityManager;

    @Override
    public Player findById(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        return unwrapPlayer(player, id);
    }

    @Override
    public Player findByUserId(Long  userId) {
        Optional<Player> player;
        player = playerRepository.findByUserId(userId);
        return unwrapPlayer(player, 404L);
    }

    @Override
    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public void removePlayer(Long id) {
        entityManager.remove(findById(id));
    }

    //Gameplay logic
    //adding card to hand
    @Override
    public Player addToHand(Long playerId, CardDeck card) {
        Player player = findById(playerId);
        player.getHand().add(card);
        return playerRepository.save(player);
    }

    //removing card from hand
    @Override
    public CardDeck removeFromHand(Player player) {

        //in case player has no more cards in hand
        if (player.getHand().size() == 0) {
            return null;
        }
        return player.getHand().remove(0);
    }

    //taking credits from user
    @Override
    public Player bet(Long playerId, Integer betCredit) {
        Player player = findById(playerId);
        User user = player.getUser();

        Integer userCredits = user.getCredits();
        if(userCredits < betCredit) {
            return player;
        }

        player.setBet(betCredit);
        user.setCredits((userCredits - betCredit));
        return playerRepository.save(player);
    }

    //set player to stay so game continues
    @Override
    public Player stay(Long playerId) {
        Player player = findById(playerId);
        player.setStay(true);
        return playerRepository.save(player);
    }

    //give the credits won to the player and send it back to the user total
    @Override
    public void creditsBack(Player player, Integer credit) {
        User user = player.getUser();
        user.setCredits(user.getCredits() + credit);
        userRepository.save(user);
    }


    /*
    Add exception later
     */
    static Player unwrapPlayer(Optional<Player> entity, Long id) {
        if (entity.isPresent()) {
            return entity.get();
        }
        return null;
    }
}
