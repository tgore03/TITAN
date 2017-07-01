<?php
session_start();
$email_error=0;
$pass_error=0;
//$con = mysqli_connect("localhost","root","","titan_main");
$con = mysqli_connect("mysql.hostinger.in","u231863402_titan","9820900119","u231863402_titan");

// Check connection
if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
else{

	 

//$cid = $_GET['cid'];
//$cid="123";	 

$cid = $_GET['cid'];

$s="sticker";
$t="thing";
$a="function";
//sticker
$var = array();
if( $query  = mysqli_query($con," SELECT device_id,device_name,button from `titan_devices` WHERE `client_id` = '$cid' and type='$s'"))

	{
	
$count=mysqli_num_rows($query);

if($count>0)
{






while($obj = mysqli_fetch_object($query)) {
$var[] = $obj;

}
}
}
//thing
$var2 = array();
if( $query2  = mysqli_query($con," SELECT device_id,device_name,button from `titan_devices` WHERE `client_id` = '$cid' and type='$t'"))

	{
	
$count=mysqli_num_rows($query2);

if($count>0)
{






while($obj = mysqli_fetch_object($query2)) {
$var2[] = $obj;
}
}
}
//android
$var3 = array();
if( $query3  = mysqli_query($con," SELECT device_id,device_name,button from `titan_devices` WHERE `client_id` = '$cid' and type='$a'")){
	
$count=mysqli_num_rows($query3);

if($count>0)
{

while($obj = mysqli_fetch_object($query3)) {
$var3[] = $obj;
}
}
}
echo '{"sticker":'.json_encode($var).',';
echo '"thing":'.json_encode($var2).',';
echo '"function":'.json_encode($var3).'}';



}
?>

