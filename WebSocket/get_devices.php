<?
session_start();

//$con = mysqli_connect("localhost","root","","titan_main");
$con = mysqli_connect("mysql.hostinger.in","u231863402_titan","9820900119","u231863402_titan");

// Check connection
if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
else{

	 

$cid = $_GET['cid'];

//$cid="0001";	 




//email
$var = array();
if( $query  = mysqli_query($con," SELECT device_name,status,device_id from `titan_devices` WHERE `client_id` = '$cid' and `type`='thing'"))

	{
	
$count=mysqli_num_rows($query);

if($count>0)
{






while($obj = mysqli_fetch_object($query)) {
$var[] = $obj;
}
echo '{"devices":'.json_encode($var).'}';
}}}
?>


