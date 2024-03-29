package com.example.demo.service;

import com.example.demo.entity.Game;
import com.example.demo.entity.Player;
import com.example.demo.entity.User;

import com.example.demo.pojo.CardDeck;
import com.example.demo.pojo.Dealer;

import com.example.demo.pojo.GameInfo;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class GameServiceImpl implements GameService{

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private UserRepository userRepository;


    //player service
    private PlayerService playerService;

    //entity manager
    private EntityManager entityManager;



    //find games
    @Override
    public Game getGame(Long id) {
        Optional<Game> game = gameRepository.findById(id);
        return unwrapGame(game, id);
    }

    //creating game
    @Override
    public Game createGame(Game game) {
        //creating deck
        CardDeck cardDeck= new CardDeck();
        List<CardDeck> deck = cardDeck.createDeck();

        //creating dealer
        Dealer dealer = new Dealer();
        game.setDealer(dealer);
        game.setDeck(deck);

        //creating object to hold game information
        GameInfo gameInfo = new GameInfo();
        game.setGameInfo(gameInfo);

        return gameRepository.save(game);
    }

    @Override
    public void deleteGame(Long id) {
        entityManager.remove(getGame(id));
    }

    //create and add player
    @Override
    public void addPlayer(Long gameId, Long userId) {
        Game game = getGame(gameId);
        Optional<User> user = userRepository.findById(userId);

        //unwrap the user so can pass into player
        User unwrappedUser = UserServiceImp.unwrapUser(user, userId);
        Player player = new Player(unwrappedUser, game, unwrappedUser.getUsername(),0, 0);

        //add player to player set for game
        game.getPlayers().add(player);

        gameRepository.save(game);
    }

    //Add player to gameInfo object
    @Override
    public void setPlayerInfo(Long gameId, Long playerId) {
        Game game = getGame(gameId);
        Optional<Player> player = playerRepository.findById(playerId);

        //unwrap player
        Player unwrappedPlayer = PlayerServiceImpl.unwrapPlayer(player,playerId);

        //enter the player's id and name into the game information object then increase amount
        game.getGameInfo().addPlayer(unwrappedPlayer.getId(), unwrappedPlayer.getPName());
        game.getGameInfo().increaseAmt();

        gameRepository.save(game);
    }

    //Set of players in game
    @Override
    public Set<Player> getPlayers(Long id) {
        Game game = getGame(id);
        return game.getPlayers();
    }

    //add the bets from the players to the pot
    @Override
    public Integer placedBets(Long gameId) {
        Game game = getGame(gameId);
        Set<Player> playerSet = game.getPlayers();

        for (Player player : playerSet) {
            game.setPot(game.getPot() + player.getBet());
        }
        gameRepository.save(game);
        return game.getPot();
    }

    /*
    Card and deck logic
     */

    //deal at start of game
    @Override
    public void deal(Long gameId) {
        Game game = getGame(gameId);
        Set<Player> playerSet = game.getPlayers();

        //make sure the visuals are clear before dealing
        game.getGameInfo().clearHands();

        CardDeck cardDeck = new CardDeck();
        cardDeck.shuffle(game.getDeck());

        //hand starts with 2 cards
        for (int i = 0; i < 2; i++) {
            //iterate through players
            for (Player player : playerSet) {
                //only deal to players who have placed a bet
                if (player.getBet() != 0) {
                    CardDeck card = game.getDeck().remove(0);

                    player.getHand().add(card);

                    //ace logic
                    if (card.getCardName().equals("ace") && player.getHandValue() > 10) {
                        card.setCardValue(1);
                    }
                    player.setHandValue(player.getHandValue() + card.getCardValue());

                    playerRepository.save(player);
                }
            }

            //deal to dealer
            CardDeck card = game.getDeck().remove(0);
            game.getDealer().getDealerHand().add(card);
            game.getGameInfo().getDHand().add(card);

            if (card.getCardName().equals("ace") && game.getDealer().getHandValue() > 10) {
                card.setCardValue(1);
            }
            game.getDealer().setHandValue(game.getDealer().getHandValue() + card.getCardValue());
            game.getGameInfo().setDValue(game.getDealer().getHandValue());
            //using hard 17 rule
            game.getDealer().stay(game.getDealer().getHandValue());

        }

        //set the visual players objects hands
        for (Player player : playerSet) {
            game.getGameInfo().addToPlayerHand(player.getPName(), player.getHand(), player.getHandValue());
        }

        gameRepository.save(game);
    }



    //deal card to player that hit
    @Override
    public void hitDeal(Long gameId, Long playerId) {
        Game game = getGame(gameId);

        //unWrapping the individual player to add card
        Optional<Player> player = playerRepository.findById(playerId);
        Player unwrappedPlayer = PlayerServiceImpl.unwrapPlayer(player, playerId);

        CardDeck card = game.getDeck().remove(0);
        unwrappedPlayer.getHand().add(card);

        unwrappedPlayer.setHandValue(unwrappedPlayer.getHandValue() + card.getCardValue());

        for (CardDeck iterCard : unwrappedPlayer.getHand()) {
            if (iterCard.getCardName().equals("ace") && iterCard.getCardValue() == 11) {
                iterCard.setCardValue(1);
                unwrappedPlayer.setHandValue(unwrappedPlayer.getHandValue() - 10);
            }
        }
        if (unwrappedPlayer.getHandValue() >= 21) {
            unwrappedPlayer.setStay(true);
        }

        //add card to the visual player object
        game.getGameInfo().hitToPlayer(unwrappedPlayer.getPName(), card, unwrappedPlayer.getHandValue());

        playerRepository.save(unwrappedPlayer);
        gameRepository.save(game);
    }

    //deal card to dealer
    @Override
    public void hitDealer(Long gameId) {
        Game game = getGame(gameId);

        //dealer doesn't hit after hand value of 18 or greater
        while (game.getDealer().getHandValue() < 18) {
            CardDeck card = game.getDeck().remove(0);
            game.getDealer().getDealerHand().add(card);

            int dealerValue = game.getDealer().getHandValue() + card.getCardValue();

            game.getDealer().setHandValue(dealerValue);
            game.getDealer().stay(game.getDealer().getHandValue());

            //add information to visual object
            game.getGameInfo().getDHand().add(card);
            game.getGameInfo().setDValue(dealerValue);
        }

        //this checks if an ace is the reason hand went over 21
        //if it did it will change ace value to 1 and call function again with adjusted value
        for (int i = 0; i < game.getDealer().getDealerHand().size(); i++) {
            CardDeck card = game.getDealer().getDealerHand().get(i);

            if(card.getCardName().equals("ace") && card.getCardValue() == 11) {
                if (game.getDealer().getHandValue() > 21 && game.getDealer().getHandValue() < 31) {
                    card.setCardValue(1);
                    hitDealer(gameId);
                }
            }
        }

        gameRepository.save(game);
    }

    //check if all players stay
    @Override
    public boolean allStay(Long gameId) {
        Game game = getGame(gameId);
        Set<Player> playerSet = game.getPlayers();

        for (Player player : playerSet) {
            if (!player.getStay()) {
                return false;
            }
        }

        return game.getDealer().isStay();
    }

    //end the hand
    @Override
    public String endHand(Long gameId) {
        Game game = getGame(gameId);

        //determine winner and give credits accordingly
        String result = winner(game, game.getPot());

        //set stay booleans to false and bets to zero for next hand
        Set<Player> playerSet = game.getPlayers();
        for (Player player : playerSet) {
            player.setBet(0);
            player.setStay(false);
            playerRepository.save(player);
        }
        game.getDealer().setStay(false);
        backToDeck(game);

        return result;
    }

    //determine winner and give them the pot
    //if dealer won just set pot to 0
    public String winner(Game game, Integer pot) {
        int highestHand = 0;
        Set<Player> playerSet = game.getPlayers();

        //list in the case more than one player has the highest card
        List<Player> highestPlayer = new ArrayList<>();

        String result = "result";

        //most basic winning condition
        for (Player player : playerSet) {
            if (player.getHandValue() > highestHand && player.getHandValue() < 22) {
                highestHand = player.getHandValue();
                if (!highestPlayer.isEmpty()) {
                    highestPlayer.remove(0);
                }
                highestPlayer.add(player);
            }
        }

        //when dealer wins set pot to zero
        //if tie with players they get what they bet back
        //if player wins give pot
        if (game.getDealer().getHandValue() > highestHand && game.getDealer().getHandValue() < 22
        || highestPlayer.isEmpty()) {
            game.setPot(0);

            result = "lose";
        }
        else if (game.getDealer().getHandValue() == highestHand) {
            while(!highestPlayer.isEmpty()) {
                Player player = highestPlayer.remove(0);
                playerService.creditsBack(player, player.getBet());

                playerRepository.save(player);
            }

            result = "tie";
        }
        else {
            int winnings = (game.getPot() * 2);
            while(!highestPlayer.isEmpty()) {
                Player player = highestPlayer.remove(0);
                playerService.creditsBack(player, winnings);

                playerRepository.save(player);

                result = "win";
            }
        }

        game.setPot(0);
        gameRepository.save(game);

        return result;
    }

    //remove cards from all players hands
    public void backToDeck(Game game) {
        Set<Player> playerSet = game.getPlayers();

        for (Player player : playerSet) {
            List<CardDeck> playerHand = player.getHand();

            while (!playerHand.isEmpty()) {
                CardDeck card = playerHand.remove(0);

                if(card.getCardName().equals("ace")) {
                    card.setCardValue(11);
                }
                game.getDeck().add(card);
            }

            player.setHand(playerHand);
            player.setHandValue(0);
            playerRepository.save(player);
        }

        //remove cards from dealer
        while (!game.getDealer().getDealerHand().isEmpty()) {
            CardDeck card = game.getDealer().getDealerHand().remove(0);

            if(card.getCardName().equals("ace")) {
                card.setCardValue(11);
            }

            game.getDeck().add(card);
        }
        game.getDealer().setHandValue(0);

        gameRepository.save(game);
    }

    //display visual object
    @Override
    public GameInfo displayGameInfo(Long id) {
        Game game = getGame(id);
        return game.getGameInfo();
    }

    static Game unwrapGame(Optional<Game> entity, Long id) {
        if(entity.isPresent()) {
            return entity.get();
        }
        return null;
    }
}
