//register and sign in
let loginForm = document.getElementById('login-user');
let registerForm = document.getElementById('register-user');

//hold user id
let userId;

//hold player id
let playerId;

//hold token
let token;

//check if signed in
let signedIn = false;

//play game button
const playGameBut = document.getElementById('play-game');

//game buttons 
const dealBtn = document.getElementById('deal');
const betBtn = document.getElementById('bet');
const hitBtn = document.getElementById('hit');
const stayBtn = document.getElementById('stay');

//test buttton
const testBtn = document.getElementById('test');

//end of game
let end = false;

//visual gameplay
let dealer;
let player;

//inner html
const displayDealer = document.getElementsByClassName('dealer');
const displayDealerHand = document.getElementById('dealer-hand');

const displayPlayer = document.getElementsByClassName('player');
const displayPlayerHand = document.getElementById('player-hand');

const displayPlayerInfo = document.getElementById('player-info');

const infoBox = document.getElementsByClassName('game-information');


//test
testBtn.addEventListener('click', (e) => {
    e.preventDefault();
    fetch('http://localhost:8080/user/1/credits', ({
        method: "GET",
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    }))
    .then(res => {
        console.log(res.json());
    })
    .then(data => console.log(data))
    .catch(err => console.log(err))
});

/*
user login and register
*/
//login user
loginForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const user = document.querySelectorAll('#login');
    const [username, password] = user;

    const userObj = {
        username: username.value,
        password: password.value
    };

    fetch ('http://localhost:8080/user/login', ({
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(userObj)
    }))
    .then(res => res.json())
    .then(data => {
        token = data.accessToken;
        signedIn = true;
        loginForm.remove();
        registerForm.remove();
    })
    .catch(err => console.log(err));
});

//register user
registerForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const user = document.querySelectorAll('#reg');
    const [username, password, email, credits] = user;

    const userObj = {
        username: username.value,
        password: password.value,
        email: email.value,
        credits: Number(credits.value)
    }

    fetch ('http://localhost:8080/user/register', ({
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(userObj)

    }))
    .then((res) => {
        //set userId variable
        if (res.status === 201) {
            userId = httpGet(`http://localhost:8080/user/findbyname/${userObj.username}`);
            signedIn = true;
            loginForm.remove();
            registerForm.remove();
        }
    })
    .catch(err => console.log(err))
});

//show basic game information
const addPlayerInfo = () => {
    let userCred = httpGet(`http://localhost:8080/user/${userId}/credits`);
    displayPlayerInfo.innerHTML = '';
    player = JSON.parse(httpGet(`http://localhost:8080/player/getplayer/${playerId}`));
    displayPlayerInfo.innerHTML += `    User credits: ${userCred} <br>`;
    displayPlayerInfo.innerHTML += `    bet: ${player.bet} <br>`;
    displayPlayerInfo.innerHTML += `    hand value: ${player.handValue} <br>`;
}

//play game
playGameBut.addEventListener('click', e => {
    e.preventDefault();
    if (!signedIn) {
        alert('you must be signed in to play');
    }
    //create game if not one in progress
    else if (!gameInProg()) {
        console.log('no game in prog');
        const potObj = {
            pot: 0
        }

        fetch ('http://localhost:8080/game/create', ({
            method: "POST",
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(potObj)
        }))
        .then()
        .catch(err => console.log(err));   
        
        playGameBut.remove();  
        betBtn.disabled = false;
        setTimeout(() => {
            playerJoin();
        }, 100);
        
    } else {
        //game currently in progress
        console.log('game in prog');
        if (playerInGame()) {
            console.log('user is in game');
            playerId = httpGet(`http://localhost:8080/player/findbyuser/${userId}`);
        } else {
            console.log('user will be added');
            playerJoin();
        }
        playGameBut.remove();
        getPlayerHand();
        getDealerHand();
        betBtn.disabled = false;
        addPlayerInfo();
    }
})


/*
gameplay buttons
*/
//place bet
betBtn.addEventListener('click', e => {
    e.preventDefault();
    const playerBet = document.getElementById('bet-amt');

     let betAmt = playerBet.value

     fetch(`http://localhost:8080/player/${playerId}/bet/${betAmt}`, ({
        method: "PUT",
        mode: "cors",
     }))
     .then((res) => {
        if (res.status === 200) {
           betBtn.disabled = true;
           dealBtn.disabled = false;
           addPlayerInfo();

           getDealerHand();
           getPlayerHand();
        }
     })
     .catch(err => console.log(err));
})

//deal
dealBtn.addEventListener('click', e => {
    e.preventDefault();
    fetch(`http://localhost:8080/game/1/deal`, ({
        method: "PUT",
        mode: "cors"
    }))
    .then(() => {
        dealBtn.disabled = true;
        hitBtn.disabled = false;
        stayBtn.disabled = false;
        addToPot();
        addPlayerInfo();
        getPlayerHand();
        getDealerHand();
    })
    .catch(err => console.log(err));
})


//hit player
hitBtn.addEventListener('click', e => {
    e.preventDefault();
    fetch(`http://localhost:8080/game/1/hit/${playerId}`, ({
        method: "PUT",
        mode: "cors",
        heades: {
            "Content-Type": "application/json"
        }
    }))
    .then(() => {
        getPlayerHand();
        if (player.stay) {
            hitBtn.disabled = true;
            stayBtn.disabled = true;
            hitToDealer();
        }
        addPlayerInfo();
    })
    .catch(err => console.log(err));
})

//stay
stayBtn.addEventListener('click', (e) => {
    e.preventDefault();
    fetch(`http://localhost:8080/player/${playerId}/stay`, ({
        method: "PUT",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        }
    }))
    .then(() => {
        stayBtn.disabled = true;
        hitBtn.disabled = true;
        hitToDealer();
    })
    .catch(err => console.log(err));
})


/*
Game functionality
*/
//end the hand
const endTheHand = () => {
    end = true;
    betBtn.disabled = false;
    addPlayerInfo();
    getPlayerHand();
    getDealerHand();

    const result = httpGet('http://localhost:8080/game/1/endhand');
    alert(`Player ${result}! Dealer's hand: ${dealer.handValue}`);

    end = false;
}


//hit to dealer
const hitToDealer = () => {
    fetch(`http://localhost:8080/game/1/dealerhit`, ({
        method: "PUT",
        mode: "cors"
    }))
    .then(() => {
        getDealerHand();
        endTheHand();
    })
    .catch(err => console.log(err));

}

//display cards in  player hand
const getPlayerHand = () => {
    player = JSON.parse(httpGet(`http://localhost:8080/player/getplayer/${playerId}`));
    displayPlayerHand.innerHTML ='';

    player.hand.forEach(card => {
        displayPlayerHand.innerHTML+= `<img src=${card.imageUrl} />`;
    })  
}

//display cards in dealer hand
const getDealerHand = () => {
    let game = httpGet(`http://localhost:8080/game/1`);
    dealer = (JSON.parse(game)).dealer;
    displayDealerHand.innerHTML = '';

    for (let i = 0; i < dealer.dealerHand.length; i++) {
        //make sure dealer's first card is face down
        if (i === 0 && !end) {
            displayDealerHand.innerHTML+= `<img src="cardback.png" />`;
        } else {
            displayDealerHand.innerHTML+= `<img src=${dealer.dealerHand[i].imageUrl} />`;
        }
    }
}

//check if game is in progress
const gameInProg = () => {
    return httpGet('http://localhost:8080/game/1').length != 0;
}

//check if player is in game
const playerInGame = () => {
    if (gameInProg() && httpGet(`http://localhost:8080/player/findbyuser/${userId}`) != 0) {
        playerId = httpGet(`http://localhost:8080/player/findbyuser/${userId}`);
        return true;
    }
    return false;
}

//let user join as player in game
const playerJoin = () => {
    fetch(`http://localhost:8080/game/1/add/${userId}`, ({
                    method: "POST",
                    mode: "cors",
                    headers: {
                        "Content-Type": "application/json"
                    }
                }))
                .then(() => {
                    playerId = httpGet(`http://localhost:8080/player/findbyuser/${userId}`);
                    console.log(playerId);
                })
                .catch(err => console.log(err));
}


//put bets into the game pot
const addToPot = () => {
    fetch(`http://localhost:8080/game/1/addbets`, ({
        method: "PUT",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        }
    }))
    .then()
    .catch(err => console.log(err));
}

//used for get requests
function httpGet(theUrl) {
    let xmlHttpReq = new XMLHttpRequest();
    xmlHttpReq.open("GET", theUrl, false);
    xmlHttpReq.send(null)
    return xmlHttpReq.responseText;
};




