<!DOCTYPE html>


<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
 <style>
table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
}
th, td {
    padding: 5px;
    text-align: left;
}

</style>
		
	 <link href="css/bootstrap.min.css" rel="stylesheet">
		
		
 <script src="mqttws31.js" type="text/javascript"></script>
<script type="text/javascript">
var i=1;
function doDisconnect()
{
client.disconnect();
alert("Disconnected to Broker");
  }
 
var form = document.getElementById("tutorial");
 function doConnect() {
  // Create a client instance 36583
  client = new Paho.MQTT.Client("m10.cloudmqtt.com",36583,"web_" + parseInt(Math.random() * 100, 10)); 
  //client = new Paho.MQTT.Client("m10.cloudmqtt.com",32687, "web_" + parseInt(Math.random() * 100, 10));

  // set callback handlers
  client.onConnectionLost = onConnectionLost;
  client.onMessageArrived = onMessageArrived;
var pay="/w/Broker Disconnected";
var top="/0001/esp1";
var lwt = new Paho.MQTT.Message(pay);
lwt.destinationName = top;
lwt.qos = 0;
lwt.retained = false;
  var options = {
    useSSL: true,
   // userName: "yyqnfjeu",
  //  password: "ozeE7fmp8dJ5",
   userName: "bnbqhbne",
   password: "Q64GD7kAtlDv",
willMessage: lwt,
    onSuccess:onConnect,
    onFailure:doFail
  }

  // connect the client
  client.connect(options);
  
}

  // called when the client connects
  function onConnect() {
    // Once a connection has been made, make a subscription and send a message.
    console.log("onConnect");
	alert("Connected to broker");
   onSubscribe();
  
  //onPublish("/0001/esp1","/d0001/314769");
  }
  var topic="/0001/esp1";
function onSubscribe()
{
var topic="/0001/esp1";
client.subscribe(topic,{qos: 1});
console.log("Subscribed to"+topic);
}


  function doFail(e){
    console.log(e);
	alert(e);
  }

  // called when the client loses its connection
  function onConnectionLost(responseObject) {
    if (responseObject.errorCode !== 0) {
      console.log("onConnectionLost:"+responseObject.errorMessage);
    }
  }

  // called when a message arrives
  function onMessageArrived(message) {
  var msg=message.payloadString;
 
 Dtable(message.destinationName,msg);
// var res = msg.split("/");
  //var bid=res[2];
 // var did=res[1];
  //console.log(bid);
  //console.log(did);
    console.log(message.destinationName+"onMessageArrived:"+message.payloadString);
	//console.log(msg+"SS");
	if(msg.substring(0,2)=='/s')
	{
	//console.log(msg.substring(0,2));
	var errormsg="";
	  var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
			var x=xmlhttp.responseText;
			//alert(x);
			 var rec = x.split("-");
			 errormsg=rec[1];
			// alert(rec[]);
  var did_output=rec[2];
  //alert("didout"+did_output);
 // var fid=rec[3];
 // var smsg="/"+relay+"/"+fid;
  if(errormsg!="")
  {
  alert(errormsg);

    }
	else
  onPublish(topic,did_output);

  // alert(relay);
 //alert(fid);
 
              
            }
        };
        xmlhttp.open("GET", "testphp.php?&r=" + msg, true);
        xmlhttp.send();
	
  }
if(msg.substring(0,2)=='/a')
	{
	
	var errormsg="";
	  var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
			var x=xmlhttp.responseText;
			
			 

  // alert("status changed");
 //alert(fid);
 
              
            }
        };
        xmlhttp.open("GET", "statusphp.php?&r=" + msg, true);
        xmlhttp.send();
	
  }
   }
   function onPublish(topic,msg)
{
//var msg="/0001/314769";
//var topic="/0001/esp1";
  message = new Paho.MQTT.Message(msg);
    message.destinationName =topic;
    client.send(message); 
} 

function Dtable(showtopic,showmsg) {
//alert(i);
    var table = document.getElementById("myTable");
    var row = table.insertRow(i);
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    cell1.innerHTML = showtopic;
    cell2.innerHTML = showmsg;
	i++;
}
</script>


 
  
</head> 

<body>
  <div class="jumbotron">
  <h1>TITAN WEBSOCKET-IOT SERVER</h1>      

</div>
     <div class="row">
	  <div class="col-sm-1" style="background-color:lavenderblush;"> </div>
    <div class="col-sm-5" style="background-color:lavenderblush;">
   <form id="example">
   <div class="panel panel-primary">
      <div class="panel-heading">Connect</div>
      <div class="panel-body"><br>
	<button type="button" class="btn btn-primary" value="Connect" onClick="doConnect(this.form)" name="Connect">Connect</button></div>
    </div>
	</div>
	 <div class="col-sm-5" style="background-color:lavenderblush;">
<div class="panel panel-primary">
      <div class="panel-heading">Disconnect</div>
      <div class="panel-body"><br>
  <button type="button" class="btn btn-primary"value="Disconnect" onClick="doDisconnect()" name="Disconnect">Disconnect</button>
  </div>
  </div>
  </div> 
  </div>
  <div class="col-sm-2"> </div>
   <div class="table-responsive">
   <div class="col-sm-8" style="background-color:lavenderblush;">  <br><br>
<table  id="myTable" class="table table-hover" width="100%">
 <tr>
    <th style="text-align:center">Topic</th>
    <th  style="text-align:center">Message</th>
  </tr>
</table>
</div>
</div>

  
 
  </form>
  
</body>
</html>