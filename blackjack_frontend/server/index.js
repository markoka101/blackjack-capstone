const express = require('express');
const path =  require('path');

const app = express();
app.use(express.json());


//middleware setup
app.get('/js', (req,res) => {
    res.sendFile(path.join(__dirname, '../src/frontend.js'));
})
app.get('/styles', (req,res) => {
    res.sendFile(path.join(__dirname, '../public/design.css'));
})
//homepage
app.get('/', (req,res) => {
    res.sendFile(path.join(__dirname, '../public/homepage.html'));
})

app.use(express.static(__dirname+'/cardImage'));

const port = 4080;
app.listen(port, () => {
    console.log(`listening on  port ${port}`);
});