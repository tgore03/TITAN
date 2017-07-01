<?php
//$q = $_REQUEST["q"];
$r = $_REQUEST["r"];
//$con =mysqli_connect("localhost","root","","titan_main");

$con = mysqli_connect("mysql.hostinger.in","u231863402_titan","9820900119","u231863402_titan");
// Check connection
if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
  else

{

  $status=substr($r,10);
 // echo $status;
  
	  $did=substr($r,3,6);
	 // echo $did;

	  	 $sql="UPDATE titan_devices SET status='$status' WHERE device_id='$did'"; 
$result=mysqli_query($con,$sql);

 	
} 	
	?>