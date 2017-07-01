<?php
session_start();

//$con = mysqli_connect("localhost","root","","titan_main");
$con = mysqli_connect("mysql.hostinger.in","u231863402_titan","9820900119","u231863402_titan");

// Check connection
if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
else{

	 

$did_input = $_GET['did_input'];
$did_output = $_GET['did_output'];
//$button_id = $_GET['button_id'];
//echo $did_input;
//echo $did_output;
//echo $buttion_id;

//$message_input_format="/s/".$did_input."/".$button_id ;
$message_input_format=$did_input;
$message_output_format=$did_output  ;
if( $query  = mysqli_query($con," SELECT * from `titan_mapping` WHERE `device_input` = '$did_input'"))

	if(mysqli_num_rows($query) != NULL ) 
	{
	  	 $sql="UPDATE titan_mapping SET device_output='$did_output' WHERE device_input='$did_input'"; 
$result=mysqli_query($con,$sql);
if($result)
echo '{"query_result": "Mapping Successful"}';
else
echo '{"query_result": "Mapping UnSuccessful.Try Again"}';
  }
  else
  {
$query=mysqli_query($con,"INSERT INTO `titan_mapping`(`device_input`,`device_output`)
			VALUES ('$message_input_format','$message_output_format')");


if($query)
echo '{"query_result": "Mapping Successful"}';
else
echo '{"query_result": "Mapping UnSuccessful.Try Again"}';

 }

}
?>
