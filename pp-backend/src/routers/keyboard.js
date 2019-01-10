var express = require('express');
var router = express.Router();

router.get('/', async function (req, res) {
    let answer = {
        "type": "buttons",
        "buttons": ["자유롭게 대화하기", "현재 상태 물어보기", "도움말"]
    };
    res.send(answer);
});

module.exports = router;