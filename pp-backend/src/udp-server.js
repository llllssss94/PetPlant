var dgram = require('dgram');
var server = dgram.createSocket('udp4');
const databaseService = require('./database-service');

var PORT = 8080;
var HOST = '0.0.0.0';

server.bind(PORT, HOST);

server.on('listening', () => {
    var address = server.address();
    console.log('UDP Server listening on ' + address.address + ":" + address.port);
});

server.on('message', (message, remote) => {
    try{
        if(message.length > 21 && message.length < 26)
        {
            console.log("Received message: " + message);
            let messageToStringArray = message.toString().split("::");

            let plantId = messageToStringArray[0];
            let illuminationLevel = messageToStringArray[2];
            let temperatureLevel = messageToStringArray[3];
            let moistureLevel = messageToStringArray[1];
        
            databaseService.saveLog(plantId, illuminationLevel, temperatureLevel, moistureLevel)
        }
            
    } catch(err){
        console.log("Received sensor input but format did not match. Received message: " + message);
    }
});

