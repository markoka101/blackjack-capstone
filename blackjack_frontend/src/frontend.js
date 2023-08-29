//register and sign in
let loginForm = document.getElementById('login-user');
let registerForm = document.getElementById('register-user');

//hold username
let currUser;

//hold player id
let playerId;

//check if signed in
let signedIn = false;

//play game button
let playGameBut = document.getElementById('play-game');

//leave game button
const leaveBtn = document.getElementById('leave');

//game buttons 
const dealBtn = document.getElementById('deal');
const betBtn = document.getElementById('bet');
const hitBtn = document.getElementById('hit');
const stayBtn = document.getElementById('stay');

//end of game
let end = false;

//visual gameplay
let dealer;
let dealerCardNum = 0;
let player;

//inner html
const displayDealer = document.getElementsByClassName('dealer');
const displayDealerHand = document.getElementById('dealer-hand');

const displayPlayer = document.getElementsByClassName('player');
const displayPlayerHand = document.getElementById('player-hand');
const displayPlayerInfo = document.getElementById('player-info');


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
    .then(res => {

        if (res.status === 200) {
            res.json()
            .then(data => {
                signedIn = true;
                currUser = data.username;
                setCook('token', data.accessToken);
    
                loginForm.remove();
                registerForm.remove();
            })
        } else {
            alert('username or password is incorrect');
        }
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
        if (res.status === 201) {
            registerForm.remove();
        }
    })
    .catch(err => console.log(err))
});

//show basic game information
async function addPlayerInfo() {
    const userId = httpGet(`http://localhost:8080/user/findbyname/${currUser}`)
    const userCred = httpGet(`http://localhost:8080/user/${userId}/credits`);
    displayPlayerInfo.innerHTML = '';

    player = await JSON.parse(httpGet(`http://localhost:8080/player/getplayer/${playerId}`));
    displayPlayerInfo.innerHTML += `    User credits: ${userCred} <br>`;
    displayPlayerInfo.innerHTML += `    bet: ${player.bet} <br>`;
    displayPlayerInfo.innerHTML += `    hand value: ${player.handValue} <br>`;
}

//play game
playGameBut.addEventListener('click', e => {
    e.preventDefault();
    playGame();

})

async function playGame() {
    if (!signedIn) {
        alert('you must be signed in to play');
    }
    //create game if not one in progress
    else if (!gameInProg()) {
        const potObj = {
            pot: 0
        }

        await fetch ('http://localhost:8080/game/create', ({
            method: "POST",
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${getCook('token')}`
            },
            body: JSON.stringify(potObj)
        }))
        .then(async () =>  {
            playGameBut = playGameBut.parentElement.removeChild(playGameBut);
            leaveBtn.disabled = false;
            betBtn.disabled = false;
            await playerJoin();
            console.log(playerId + '    playGame()')
            await addPlayerInfo();

        })
        .catch(err => console.log(err));   
        
    } else {
        //this is for joining a game that is already in progress
        if (playerInGame()) {
            const userId = httpGet(`http://localhost:8080/user/findbyname/${currUser}`);
            playerId = httpGet(`http://localhost:8080/player/findbyuser/${userId}`);
            await setPlayer();
        } else {
            playerJoin();
            addPlayerInfo();
        }

        playGameBut = playGameBut.parentElement.removeChild(playGameBut);
        leaveBtn.disabled = false;

        if (player.bet != 0) {
            hitBtn.disabled = false;
            stayBtn.disabled = false;
        } else {
            betBtn.disabled = false;
        }

        getPlayerHand();
        getDealerHand();
        addPlayerInfo();
    }
}

//leave game
leaveBtn.addEventListener('click', e => {
    e.preventDefault();

    fetch(`http://localhost:8080/player/${playerId}/leave`, ({
        method:"DELETE",
        mode:"cors",
        headers: {
            "Authorization": `Bearer ${getCook('token')}`
        }
    }))
    .then(() => {
        //disabling game buttons
        hitBtn.disabled = true;
        stayBtn.disabled = true;
        dealBtn.disabled = true;
        betBtn.disabled = true;
        leaveBtn.disabled = true;

        //remove the visuals from the screen
        const dealerVisual = document.querySelectorAll('.dealer');
        const playerVisual = document.querySelectorAll('.player');
        dealerVisual.forEach(elem => elem.remove);
        playerVisual.forEach(elem => elem.remove);

        document.getElementById('games').appendChild(playGameBut);
    })

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
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${getCook('token')}`
        }
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
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${getCook('token')}`
        }
    }))
    .then(() => {
        dealBtn.disabled = true;
        hitBtn.disabled = false;
        stayBtn.disabled = false;

        dealerCardNum = 2;

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
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${getCook('token')}`
        }
    }))
    .then(() => {
        getPlayerHand();

        //when player bust the stay value is autimatically set to true in back end
        //so if they hit and their stay value is true that, then the player bust
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
            "Content-Type": "application/json",
            "Authorization": `Bearer ${getCook('token')}`
        }
    }))
    .then(() => {
        stayBtn.disabled = true;
        hitBtn.disabled = true;

        while (!(httpGet(`http://localhost:8080/game/1/allstay`))) {
            console.log('running')
            setTimeout(100);
        }
        hitToDealer();
    })
    .catch(err => console.log(err));
})


/*
Game functionality
*/
//end the hand
async function endTheHand() {
    end = true;
    betBtn.disabled = false;

    getDealerHand();

    dealerCardNum = 0;
    

    const result = httpGet('http://localhost:8080/game/1/endhand');
    alert(`Player ${result}! Dealer's hand: ${dealer.handValue}`);

    getPlayerHand();
    getDealerHand();
    addPlayerInfo();
    
    end = false;
}


//hit to dealer
const hitToDealer = () => {
    fetch(`http://localhost:8080/game/1/dealerhit`, ({
        method: "PUT",
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${getCook('token')}`
        }
    }))
    .then(() => {
        getDealerHand();
        endTheHand();
    })
    .catch(err => console.log(err));

}

//display cards in player hand
async function getPlayerHand() {
    player = await JSON.parse(httpGet(`http://localhost:8080/player/getplayer/${playerId}`));
    displayPlayerHand.innerHTML ='';

    player.hand.forEach(card => {
            displayPlayerHand.innerHTML+= `<img src=${card.imageUrl} />`;
    })  
}

//display cards in dealer hand
async function getDealerHand() {
    let game = httpGet(`http://localhost:8080/game/1`);
    dealer = await (JSON.parse(game)).dealer;
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
    const userId = httpGet(`http://localhost:8080/user/findbyname/${currUser}`);
    if (gameInProg() && httpGet(`http://localhost:8080/player/findbyuser/${userId}`) != 0) {
        playerId = httpGet(`http://localhost:8080/player/findbyuser/${userId}`);
        return true;
    }
    return false;
}

//let user join as player in game
async function playerJoin(){
    const userId = httpGet(`http://localhost:8080/user/findbyname/${currUser}`);
    await fetch(`http://localhost:8080/game/1/add/${userId}`, ({
                    method: "POST",
                    mode: "cors",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${getCook('token')}`
                    }
                }))
                .then(() => {
                    playerId = httpGet(`http://localhost:8080/player/findbyuser/${userId}`);
                    console.log(playerId + '    playerJoin()');
                })
                .catch(err => console.log(err));
}

//set the player
async function setPlayer() {
    console.log(playerId + '    setPlayer()');
    player = await JSON.parse(httpGet(`http://localhost:8080/player/getplayer/${playerId}`));
}

//put bets into the game pot
const addToPot = () => {
    fetch(`http://localhost:8080/game/1/addbets`, ({
        method: "PUT",
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${getCook('token')}`
        }
    }))
    .then()
    .catch(err => console.log(err));
}

//used for get requests
function httpGet(theUrl) {
    let xmlHttpReq = new XMLHttpRequest();
    xmlHttpReq.open("GET", theUrl, false);
    xmlHttpReq.setRequestHeader("Authorization", `Bearer ${getCook('token')}`);
    xmlHttpReq.send();
    return xmlHttpReq.responseText;
};

//create cookie
function setCook(name, value) {
    document.cookie = name + "=" + value +"; Secure";
}

function getCook(cName) {
    let name = cName + "=";
    let ca = document.cookie.split(';');

    for (const element of ca) {
        let c = element;
        while (c.startsWith(' ')) {
            c = c.substring(1);
        }

        if (c.startsWith(name)) {
            return c.substring(name.length, c.length);
        }
    }

    return "";
}

