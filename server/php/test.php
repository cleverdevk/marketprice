<!DOCTYPE html>
<html>
<body>

<?php

   $hostname = "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com";
   $username = "MarketPrice";
   $password = "price12345";
   $db = "marketprice_db";

   $dbconnect = mysqli_connect($hostname, $username, $password, $db);

if(!$dbconnect) {
   echo "fail";
   die('could not connect'.mysql_error());

} else {
   echo "success\n";
  $id = $_GET['id'];
  $password = $_GET['password'];
  $nickname = $_GET['nickname'];

  $sql = "INSERT INTO user (id, password, nickname) VALUES ('$id', '$password', '$nickname')";

    if (!mysqli_query($dbconnect, $sql)) {
      //echo "Connect Failed : ".mysqli_connect_error();
        //die('An error occurred. Your review has not been submitted.');
die(mysqli_error($dbconnect));

    } else {

      echo "Thanks for your review.";

    }

}
?>

</body>
</html>
