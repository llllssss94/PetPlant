let he = require('he');
let axios = require('axios');
let Measurement = require('./measurement');
let parser = require('fast-xml-parser');

const options = {
    attributeNamePrefix : "@_",
    attrNodeName: "attr", //default is 'false'
    textNodeName : "#text",
    ignoreAttributes : true,
    ignoreNameSpace : false,
    allowBooleanAttributes : false,
    parseNodeValue : true,
    parseAttributeValue : false,
    trimValues: false,
    cdataTagName: false, //default is 'false'
    cdataPositionChar: "!",
    localeRange: "", //To support non english character in tag/attribute values.
    parseTrueNumberOnly: false,
    attrValueProcessor: a => he.decode(a, {isAttributeValue: true}),//default is a=>a
    tagValueProcessor : a => he.decode(a) //default is a=>a
}

getPlantData = async (plantName) => {
    const nongsaroOptions = options;
    let contentNo = '';

    let response;
    try{
        response = await axios.get('http://api.nongsaro.go.kr/service/garden/gardenList?apiKey=201810240OZ0QZRO82I7A3HJEUJXTQ&sType=sPlntzrNm&sText=' + plantName)
    } catch(error){
        throw new Error(error);
    }

    var body = response.data;

    if (parser.validate(body) === true) { //optional (it'll return an object in case it's not valid)
        var jsonObj = parser.parse(body, nongsaroOptions);
    }

    // Intermediate obj
    var tObj = parser.getTraversalObj(body, nongsaroOptions);
    var jsonObj = parser.convertToJson(tObj, nongsaroOptions);

    if (Array.isArray(jsonObj["response"]["body"]["items"].item)) {
        contentNo += jsonObj["response"]["body"]["items"]["item"][0]["cntntsNo"];
    } else {
        contentNo += jsonObj["response"]["body"]["items"]["item"]["cntntsNo"];
    }

    try{
        response = await axios.get('http://api.nongsaro.go.kr/service/garden/gardenDtl?apiKey=201810240OZ0QZRO82I7A3HJEUJXTQ&sType=sCntntsSj&wordType=cntntsSj&cntntsNo=' + contentNo)
    } catch(error){
        throw new Error(error);
    }

    var body = response.data;

    if (parser.validate(body) === true) { //optional (it'll return an object in case it's not valid)
        var jsonObj = parser.parse(body, nongsaroOptions);
    }

    // Parse obj
    var tObj = parser.getTraversalObj(body, nongsaroOptions);
    var jsonObj = parser.convertToJson(tObj, nongsaroOptions);

    // Make JSON format

    let temp = jsonObj["response"]["body"]["item"]["grwhTpCodeNm"];
    temp = temp.split('~');
    temp = JSON.parse('{"min":' + temp[0] + ', "max":' + temp[1].split("℃")[0] + "}");

    let humidity = jsonObj["response"]["body"]["item"]["hdCodeNm"];
    humidity = humidity.split(' ~ ');
    humidity = JSON.parse('{"min":' + humidity[0] + ', "max":' + humidity[1].split("%")[0] + "}");

    let illuminance = jsonObj["response"]["body"]["item"]["lighttdemanddoCodeNm"];
    illuminance = illuminance.split("),");
    let tmp = [];
    for (let i = 0; i < illuminance.length; i++) {
        tmp.push(illuminance[i].split("(")[1].split(" Lux")[0]);
    }

    const minimumIlluminance = tmp[0].split("~")[0]/20;

    let maximumIlluminance;
    if(tmp[tmp.length - 1].split("~")[1].replace(',', '') > 2000){
        maximumIlluminance = 100;
    } else{
        maximumIlluminance = tmp[tmp.length - 1].split("~")[1].replace(',', '')/20;
    }
    illuminance = { min: minimumIlluminance, max: maximumIlluminance };

    let measurement = new Measurement(temp, humidity, illuminance);
    return measurement;
};

exports.getPlantData = getPlantData;
