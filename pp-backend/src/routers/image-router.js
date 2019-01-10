var express = require('express'),
    router = express.Router(),
    multer = require('multer'),
    pythonShell = require('python-shell'),
    fs = require('fs'),
    path = require('path');

var storage = multer.diskStorage({
    destination: async function (req, file, callback) {
        console.log("file: " + JSON.stringify(file));

        // Make isolated directory
        let dir = './image_classification_module/data/test/'+ file.originalname;

        if (!(await fs.existsSync(dir))){
            await fs.mkdirSync(dir);
        }
        let dir2 = './image_classification_module/data/test/'+ file.originalname + '/upload';

        if (!(await fs.existsSync(dir2))){
            await fs.mkdirSync(dir2);
        }
        callback(null, dir2);
    },
    filename: function (req, file, callback) {
        callback(null, file.originalname);
    }
});

var upload = multer({ storage: storage }).single('image');

router.post('/',function(req,res){

    upload(req,res,function(err) {
        if(err) {
            console.log(err);
            return res.end("Error uploading file.");
        }
        console.log(req.file);
        
        fileName = req.file.filename;

        // This is options and parameters to use with python script
        var options = {
            mode: 'text',
            pythonPath: '', // We don't have to write it, because of windows PATH variable
            pythonOptions: ['-u'],  // Python script option
            scriptPath: '', // We don't have to write it, because of windows PATH variable
            args: [fileName]    // parameters - We use only one parameter which represent image file name
        };
        
        // Make absolutepath
        let reqPath = path.join(__dirname, '../../image_classification_module');
        console.log("dirname:" + reqPath);

        // Call python image classification module with file name
        pythonShell.PythonShell.run(reqPath +'/image_classification_module.py', options, function (err, results){
            if (err) {
                console.log("Error running python script: " + err);
                throw err;
            }
            // Image class result
            let result = results.slice(-1)[0]

            console.log('results: %j', result);
            // Return the result
            res.writeHead(200, {'Content-Type':'text/plain; charset=utf-8'});
            res.end(result);
        });
    });
});

module.exports = router;