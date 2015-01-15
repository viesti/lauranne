var express = require('express')
var app = express()
var spawn = require('child_process').spawn
var bodyParser = require('body-parser');

/*
var ev3dev = require('./node_modules/ev3dev/bin/index.js');
var motor = new ev3dev.Motor();
*/

app.use(bodyParser.json());
app.post('/say', function(req, res) {
  console.log(req.body.message);
  var child = spawn('say.sh', [req.body.message]);
  res.send("ok");
})

var server = app.listen(3000, function () {
  var host = server.address().address
  var port = server.address().port
  console.log('Robot app listening at http://%s:%s', host, port)
})
