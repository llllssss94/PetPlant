const swaggerJSDoc = require('swagger-jsdoc');

exports.swaggerSpec = swaggerJSDoc({
    swaggerDefinition: {
        info: {
            title: 'Pet Plant Backend API',
            version: '1.0.0'
        }
    },
    apis: ['./routers/User.js'],
    apis: ['./routers/Log.js']
});