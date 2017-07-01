<?php
$con = mysqli_connect("mysql.hostinger.in","u231863402_titan","9820900119","u231863402_titan");

// Check connection
if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
else
{

$emailAddress = $_GET['emailaddress'];
$passWord = $_GET['password'];
$uname = mt_rand(1,999999);


$uid=mt_rand(1,999999);

$passhash = md5($passWord);
if( $query  = mysqli_query($con," SELECT * from `titan_user` WHERE `emailid` = '$emailAddress'"))

	if(mysqli_num_rows($query) != NULL )
{
echo '{"cid":"Email-Address already exists","uid":"enter again"}';
 }
else
{
$result=mysqli_query($con,"INSERT INTO `titan_user`(`client_id`,`password`,`emailid`,`uid`)
			VALUES ('$uname','$passhash','$emailAddress','$uid')");


if($result == true) 
{
	echo '{"cid":'.$uname.',"uid":'.$uid.'}';
}
else
{
	echo '{"cid":"FAILURE","uid":"Try Again"}';
}
mysqli_close($con);
}
 }
?>



