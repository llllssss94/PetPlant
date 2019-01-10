var express = require('express');
var router = express.Router();
var databaseService = require('../database-service');
var nongsaro = require('../nongsaro');

router.post('/', async function (req, res) {
    let userKey = decodeURIComponent(req.body.user_key); // user's key
    let type = decodeURIComponent(req.body.type); // message type
    let content = decodeURIComponent(req.body.content); // user's message
    let answer;

    console.log(userKey);
    console.log(type);
    console.log(content);

    if (content.includes("@")) {
        let response;
        let emailExists = await databaseService.isExistingEmailExportVersion(content);
        if(emailExists){
            await databaseService.registerKakaotalkId(content, userKey);
            response = "정상적으로 연동이 되었습니다. 무슨 일이신가요 주인님?";
        } else{
            response = "존재하지 않는 이메일 계정입니다. 확인을 한 후에 다시 보내주세요.";
        }
        answer = {
            "message": {
                "text": response
            }
        };
        res.send(answer);
        return;
    }

    let isExistingKakaotalkKey = await databaseService.kakaotalkKeyExistsInDatabase(userKey);
    console.log(isExistingKakaotalkKey);
    if (!isExistingKakaotalkKey) {
        const answer = {
            "message": {
                "text": `Pet Plant 앱과 현재 연동이 되어있지 않습니다. 연동을 하기 위해서 Pet Plant 앱에서 등록한 아이디를 이 방에서 말해주세요.\n예시: mail@domain.com`,
            }
        };
        res.send(answer);
        return;
    }

    let plantNameQuery;
    try{
        plantNameQuery = await databaseService.getSelectedPlantOfKakaotalkUser(userKey);
    } catch(err){
        res.send("서버 오류가 발생하였습니다.");
        return;
    }
    if(plantNameQuery.details.length == 0){
        const answer = {
            "message": {
                "text": `현재 등록된 식물이 없습니다. 앱에서 식물을 등록해주세요!`,
            }
        };
        res.send(answer);
        return;
    }

    let plantName = plantNameQuery.details[0].nickname;

    if (content.includes("대화")) {
        let response = `저는 '${plantName}'입니다. 무슨 일이신가요 주인님?`;
        answer = {
            "message": {
                "text": response
            }
        };
    }

    else if (content.includes("잘자") || content.includes("잘 자")) {
        answer = {
            "message": {
                "text": "안녕히 주무세요 주인님!"
            }
        };
    }

    else if (content.includes("ㅎㅇ")) {
        answer = {
            "message": {
                "text": "ㅎㅇ"
            }
        };
    }

    else if (content.includes("사랑")) {
        answer = {
            "message": {
                "text": "ㅎㅎ고마워요!"
            }
        };
    }

    else if (content.includes("도움말")) {
        answer = {
            "message": {
                "text": "Pet Plant 앱에서 email 계정으로 가입 후, 키우시는 식물의 사진을 찍어서 등록을 해주세요! 그 후 대화를 원하시는 식물을 앱에서 선택해주시면 됩니다."
            }
        };
    }

    else if (content.includes("상태") || content.includes("어때") || content.includes("습도") || content.includes("온도") || content.includes("조도") || content.includes("목") ) {
        console.log("user key:" + userKey);
        let response;
        try {
            let log = await databaseService.getMostRecentLogOfSelectedPlantWithKakaoId(userKey);
            console.log(log);
            if(log.details.length == 0){
                response = "제가 아직 측정을 시작하지 못해서 측정값이 없습니다.";    
            } else{
                let plantId = log.details[0].plant_id;
                let species = await databaseService.getSpeciesOfPlantWithId(plantId);
                species = (species.details.species);
                console.log(species);
                species = species.replace(/\s/g, "&nbsp");
                console.log(species);

                let plantInfo = await nongsaro.getPlantData(species);

                console.log(log);
                let measurements = log.details[0];
                console.log(measurements);

                let emotion = '';
                if(measurements.temperature_level < plantInfo.temp.min){
                    emotion += "약간 쌀쌀해요!"
                } else if(measurements.temperature_level > plantInfo.temp.max){
                    emotion += "좀 더워요!"
                } 

                if(measurements.moisture_level < plantInfo.humidity.min){
                    if(emotion.length > 0){
                        emotion += "그리고 ";
                    }
                    emotion += "목말라요!"
                } else if(measurements.moisture_level > plantInfo.humidity.max){
                    if(emotion.length > 0){
                        emotion += "그리고 ";
                    }
                    emotion += "물을 너무 많이 주셨네요. 한동안 안 주셔도 될 것 같습니다!"
                } 

                if(measurements.illumination_level < plantInfo.illuminance.min){
                    if(emotion.length > 0){
                        emotion += " 또, ";
                    }
                    emotion += "어두워요. 아무것도 안보이네요. 빛을 좀 더 주세요!";
                } else if(measurements.illumination_level > plantInfo.illuminance.max){
                    if(emotion.length > 0){
                        emotion += " 또, ";
                    }
                    emotion += "눈이 부셔요! 좀 더 어두운 곳으로 데려가 주세요!";
                } 

                let currentStatus = `온도는 ${measurements.temperature_level}%이며, 습도는 ${measurements.moisture_level}%이고 조도는 ${measurements.illumination_level}%입니다!`;
                response = `'${plantName}'의 ${currentStatus}. ${emotion}`;
            }
        } catch (err) {
            console.log(err);
            response = "서버 오류가 발생했습니다."
        }
        answer = {
            "message": {
                "text": response
            }
        };
    }

    else if (content.includes("야")) {
        answer = {
            "message": {
                "text": "왜"
            }
        };
    }

    else if (content.includes("야?")) {
        answer = {
            "message": {
                "text": "저도 모르겠어요!"
            }
        };
    }

    else if(content.includes("안녕")){
        answer = {
            "message": {
                "text": "안녕하세요!"
            }
        };
    }

    else if(content.includes("뭐해") || content.includes("뭐 해")){
        answer = {
            "message": {
                "text": "자라고 있죠! 쑥쑥!"
            }
        };
    }

    else if(content.includes("ㅋㅋ")){
        answer = {
            "message": {
                "text": "웃지 마용"
            }
        };
    }

    else if(content.includes("?")){
        answer = {
            "message": {
                "text": "??"
            }
        };
    }

    else if(content.includes("멍청") || content.includes("바보")){
        answer = {
            "message": {
                "text": "우씨! 욕하지 말아주세요"
            }
        };
    }

    else if(content.includes("이름")){
        answer = {
            "message": {
                "text": "ㅎㅎ제 이름은 비밀이에요"
            }
        };
    }

    else {
        answer = {
            "message": {
                "text": "무슨 말인지 모르겠어요."
            }
        };
    }
    res.send(answer);
});

module.exports = router;