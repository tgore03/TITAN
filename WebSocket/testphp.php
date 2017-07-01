<?php
//$q = $_REQUEST["q"];
$r = $_REQUEST["r"];
//$conn =mysqli_connect("localhost","root","");

$con = mysqli_connect("mysql.hostinger.in","u231863402_titan","9820900119","u231863402_titan");
// Check connection
if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
  else

{
//echo"helo";
$sql="SELECT device_output FROM titan_mapping WHERE device_input='$r' LIMIT 1";
 
$result=mysqli_query($con,$sql);
$count=mysqli_num_rows($result);



if($count==1)
{

 while ($row = mysqli_fetch_assoc($result)) 
	   {
	   // $relayid=$row["relayid"];
		//$functionid=$row["functionid"];
		$device_output=$row["device_output"];
}

	} 
	if($count==0)
	{
	echo "-No device is assigned to be controlled by this sticker-/00"; 	
	} 
	else
	{
	echo "--";
	//echo "/";echo $relayid; echo "/";
//echo $functionid;
echo $device_output; echo "-";
	} 	
} 	
	?>