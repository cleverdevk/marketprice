<!DOCTYPE html>
<html>
<body>

<?php

   $hostname = "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com";
   $username = "MarketPrice";
   $password = "price12345";
   $db = "marketprice";

   $dbconnect = mysqli_connect($hostname, $username, $password, $db);

if(!$dbconnect) {
   echo "fail";
   die('could not connect'.mysql_error());

} else {
   echo "success\n";
  $id = $_GET['id'];
  $password = $_GET['password'];
  $nickname = $_GET['nickname'];
  $salt = $_GET['salt'];

  $sql = "INSERT INTO user (id, password, nickname, salt) VALUES ('$id', '$password', '$nickname', '$salt')";

    if (!mysqli_query($dbconnect, $sql)) {
die(mysqli_error($dbconnect));

    } else {

      echo "Thanks for your review.";

    }

}
?>

</body>
</html>
