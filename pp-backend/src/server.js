require('dotenv').config();
const express = require('express'),
    bodyParser = require('body-parser'),
    userRouter = require('./routers/user-router'),
    imageRouter = require('./routers/image-router'),
    keyboardRouter = require('./routers/keyboard'),
    messageRouter = require('./routers/message'),
    udpServer = require('./udp-server'),
    app = express();

// middlewares
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// template engine
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.engine('html', require('ejs').renderFile);

// routers
app.use('/users', userRouter);
app.use('/images', imageRouter);
app.use('/keyboard', keyboardRouter);
app.use('/message', messageRouter);

// default route for health check
app.get('/', function (req, res) {
    res.send("I'm healthy!");
})

var server = app.listen(8080, function () {
    var host = server.address().address
    var port = server.address().port
    console.log("Express app listening at http://%s:%s", host, port)
})