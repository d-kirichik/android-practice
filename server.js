var express = require('express');
var app = express();
var qs = require('querystring');
var bodyParser = require('body-parser');
var fs = require("fs");


app.use(bodyParser.json())

app.post('/postData', function (req, res) {
   fs.readFile( __dirname + "/" + "data.json", 'utf8', function (err, data) {
       data = JSON.parse( data );
       cur_data ={"x" : req.body.x, "y" : req.body.y, "z" : req.body.z, "cos" : req.body.cos, "acc_x" : req.body.acc_x, "acc_y" : req.body.acc_y, "acc_z" : req.body.acc_z};
       data["data" + Date.now()] = cur_data;
       console.log("request handled\n");
       res.end(JSON.stringify(cur_data));
       fs.writeFile("data.json", JSON.stringify(data)); 
   });
})

var server = app.listen(8081, function () {

  var host = server.address().address
  var port = server.address().port
  console.log("simple server listening at http://%s:%s", host, port)

})
