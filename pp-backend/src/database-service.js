const db = require('./db');
let DTO = require('./dto');


exports.getAllUsers = function (callback) {
    db.pool.query('SELECT * FROM user', (err, rows) => {
        if (err) throw err;
        callback(rows);
    });
};

exports.getUserByEmail = function (userEmail) {
    return new Promise((resolve, reject) => {
        let query = "SELECT * FROM user WHERE email = \"" + userEmail + "\"";
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            resolve(rows);
        })
    })
}

exports.createUser = async function (email, password) {
    let emailAlreadyExists = await isExistingEmail(email);
    console.log(emailAlreadyExists);
    if (emailAlreadyExists) {
        return new DTO(false, "Email exists. Please choose a different email.");
    } else {
        let successfulSave = await saveUser(email, password);
        return new DTO(true);
    }
}

saveUser = function (email, password) {
    return new Promise((resolve, reject) => {
        let query = "INSERT INTO user (id, email, password) VALUES (default , \'" + email + "\',\'" + password + "\')";
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            resolve(rows);
        })
    })
}

isExistingEmail = function (username) {
    return new Promise((resolve, reject) => {
        let query = `SELECT EXISTS(SELECT * FROM user WHERE email = '${username}');`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            let result = rows[0];
            resolve(result[Object.keys(result)[0]]);
        })
    })
}

exports.isExistingEmailExportVersion = function (username) {
    return new Promise((resolve, reject) => {
        let query = `SELECT EXISTS(SELECT * FROM user WHERE email = '${username}');`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            let result = rows[0];
            resolve(result[Object.keys(result)[0]]);
        })
    })
}

exports.isValidCredentials = function (email, password) {
    return new Promise((resolve, reject) => {
        let query = `SELECT EXISTS(SELECT * FROM user WHERE email = '${email}' AND password = '${password}');`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            let result = rows[0];
            isValid = Boolean(result[Object.keys(result)[0]]);
            resolve(new DTO(isValid));
        })
    })
}

exports.registerKakaotalkId = function (email, kakaotalkId) {
    return new Promise((resolve, reject) => {
        let query = `UPDATE user SET kakaotalk_id = '${kakaotalkId}' WHERE email = '${email}';`;
        console.log(query);
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            console.log(rows);
            resolve(rows);
        })
    })
}

exports.isRegisteredId = function (kakaotalkId) {
    return new Promise((resolve, reject) => {
        let query = `SELECT count(*) FROM user WHERE email =  (SELECT email FROM user WHERE kakaotalk_id = '${kakaotalkId}' ) AND kakaotalk_id IS NULL;`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            console.log(rows);
            let result = rows[0];
            resolve(result[Object.keys(result)[0]]);
        })
    })
}

/** */
exports.getPlantsOfKakaotalkUser = function (kakaotalkId) {
    return new Promise((resolve, reject) => {
        // let query = `SELECT nickname FROM plant WHERE owner_email = (SELECT email FROM user WHERE kakaotalk_id LIKE '${kakaotalkId}');`;

        let query = `SELECT nickname FROM plant WHERE owner_email = (SELECT email FROM user WHERE kakaotalk_id = '${kakaotalkId}');`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            console.log(rows);
            resolve(new DTO(true, rows));
        })
    })
}

isExistingDeviceId = (deviceId) => {
    return new Promise((resolve, reject) => {
        let query = `SELECT EXISTS(SELECT * FROM plant WHERE device_id = '${deviceId}');`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            let result = rows[0];
            resolve(result[Object.keys(result)[0]]);
        })
    })
}

setDeviceIdToNull = (deviceId) => {
    return new Promise((resolve, reject) => {
        let query = `UPDATE plant SET device_id = null WHERE device_id = '${deviceId}'`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            resolve();
        })
    })
}

exports.registerPlant = (deviceId, userEmail, species, nickname) => {
    return new Promise(async (resolve, reject) => {
        try{
            let deviceIdExists = await isExistingDeviceId(deviceId);
            if(deviceIdExists){
                await setDeviceIdToNull(deviceId);
            }
        } catch(err){
            console.log(err);
        }

        let query = `INSERT INTO plant (id, device_id, owner_email, species, nickname)  
        VALUES ( default, '${deviceId}', '${userEmail}', '${species}', '${nickname}'); `;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            resolve(new DTO(true));
        })
    })
}

exports.saveLog = (deviceId, illuminationLevel, temperatureLevel, moistureLevel) => {
    return new Promise((resolve, reject) => {
        let query = `INSERT INTO plant_log (plant_id, illumination_level, temperature_level, moisture_level) VALUES
        ( (SELECT id FROM plant WHERE device_id = '${deviceId}'), ${illuminationLevel}, ${temperatureLevel}, ${moistureLevel});`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            resolve(new DTO(true));
        })
    })
}

exports.getPlantsOfUser = (username) => {
    return new Promise((resolve, reject) => {
        let query = `SELECT * FROM plant WHERE owner_email = '${username}';`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            console.log(rows);
            resolve(new DTO(true, rows));
        })
    })
}

getEmailOfKakaotalkUser = (kakaotalkId) => {
    return new Promise((resolve, reject) => {
        console.log("yomski!" + kakaotalkId);

        let query = `SELECT email FROM user WHERE kakaotalk_id = '${kakaotalkId}';`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            console.log(rows);
            resolve(rows[0].email);
        })
    })
}

refreshPlantSelectionOf = (email, plantNickname) => {
    return new Promise((resolve, reject) => {
        let query = `UPDATE plant SET selected = 0 WHERE owner_email = '${email}'`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            resolve(rows);
        })
    })
}

setSelectedPlant = (email, plantNickname) => {
    return new Promise((resolve, reject) => {
        let query = `UPDATE plant SET selected = 1 WHERE owner_email = '${email}' AND nickname = '${plantNickname}'`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            resolve(rows);
        })
    })
}

exports.selectPlant = async (plantNickname, email) => {
    try{
        // let email = await getEmailOfKakaotalkUser(kakaotalkId);
        let refreshPlantSelectionOf2 = await refreshPlantSelectionOf(email, plantNickname);
        let setSelectedPlant2 = await setSelectedPlant(email, plantNickname);
        return setSelectedPlant2;
    } catch(err){
        console.log(err);
    }
}

exports.getSelectedPlantOfUser = (userEmail) => {
    return new Promise((resolve, reject)=>{
        let query = `SELECT nickname, species FROM plant WHERE owner_email = '${userEmail}' AND selected = 1;`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            resolve(new DTO(true, rows));
        })
    })
}

exports.getSelectedPlantOfKakaotalkUser = (kakaotalkId) => {
    return new Promise((resolve, reject)=>{
        let query = `SELECT nickname FROM plant WHERE owner_email = (SELECT email FROM user WHERE kakaotalk_id = '${kakaotalkId}') AND selected = 1;`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            resolve(new DTO(true, rows));
        })
    })
}

exports.getSpeciesOfPlantWithId = (id) => {
    return new Promise((resolve, reject)=>{
        let query = `SELECT species FROM plant WHERE id = '${id}';`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            resolve(new DTO(true, rows[0]));
        })
    })
}

exports.getMostRecentLogOfSelectedPlant = (userEmail) => {
    return new Promise((resolve, reject)=>{
        let query = `SELECT * FROM plant_log WHERE plant_id = (SELECT id FROM plant WHERE owner_email = '${userEmail}' AND SELECTED = 1) ORDER BY recorded_date DESC LIMIT 1;`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            resolve(new DTO(true, rows));
        })
    })
}

exports.getMostRecentLogOfSelectedPlantWithKakaoId = (kakaotalk_id) => {
    return new Promise((resolve, reject)=>{
        let query = `SELECT * FROM plant_log WHERE plant_id = (SELECT id FROM plant WHERE owner_email = (SELECT email FROM user WHERE kakaotalk_id = '${kakaotalk_id}') AND SELECTED = 1) ORDER BY recorded_date DESC LIMIT 1;`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                console.log(err);
                reject(new DTO(false, err));
            }
            resolve(new DTO(true, rows));
        })
    })
}

// exports.kakaotalkIdExistsInDatabase = async (userEmail) => {
//     return new Promise((resolve, reject)=>{

//         let query = `SELECT kakaotalk_id FROM user WHERE email = '${userEmail}'`;
 
//         db.pool.query(query, (err, rows) => {
//             if (err) {
//                 console.log(err);
//                 reject(new DTO(false, err));
//             }
//             console.log(rows);
//             console.log(rows[0].kakaotalk_id);
//             let exists = true;
//             if(rows[0].kakaotalk_id == null){
//                 exists = false;
//             } 
//             console.log("exists:" + exists);
//             resolve(exists);
//         })
//     })
// }

// exports.isExistingKakaotalkKey = function (kakaotalkId) {
//     return new Promise((resolve, reject) => {
//         let query = `SELECT EXISTS(SELECT * FROM user WHERE kakaotalk_id = '${kakaotalkId}');`;
//         db.pool.query(query, (err, rows) => {
//             if (err) {
//                 reject(new DTO(false, err));
//             }
//             let result = rows[0];
//             resolve(result[Object.keys(result)[0]]);
//         })
//     })
// }

exports.kakaotalkKeyExistsInDatabase = function (kakaotalkId) {
    return new Promise((resolve, reject) => {
        let query = `SELECT EXISTS(SELECT * FROM user WHERE kakaotalk_id = '${kakaotalkId}');`;
        db.pool.query(query, (err, rows) => {
            if (err) {
                reject(new DTO(false, err));
            }
            let result = rows[0];
            resolve(result[Object.keys(result)[0]]);
        })
    })
}