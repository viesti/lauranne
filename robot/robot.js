var express = require('express')
var app = express()
var spawn = require('child_process').spawn
var bodyParser = require('body-parser');

var ev3dev = require('./node_modules/ev3dev/bin/index.js');
var motor = new ev3dev.Motor("outA");

app.use(bodyParser.json());
app.post('/say', function(req, res) {
  console.log(req.body.message);
  var child = spawn('say.sh', [req.body.message]);
  res.send("ok");
})

app.post('/greet', function(req, res) {
  motor.rampUpSp = 100;
  motor.rampDownSp = 100;
  motor.runMode = 'time';
  motor.timeSp = 1000;
  motor.dutyCycleSp = 50;
  console.log('Running motor...');
  motor.run = 1;
  res.send("ok");
})

var server = app.listen(3000, function () {
  var host = server.address().address
  var port = server.address().port
  console.log('Robot app listening at http://%s:%s', host, port)
})
