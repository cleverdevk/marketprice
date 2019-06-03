<?php

 $mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

 if(!$mysqli) {

   die('could not connect'.mysql_error());

 } else {

   $id = $_POST['id'];
   $password = $_POST['password'];

   $sql = "select * from user where id = '".$id."' AND password = '".$password."'";

   $result = mysqli_query($mysqli,$sql);

    $rows = mysqli_num_rows($result);

    if($rows == 0) {

      echo "User Not Found";

    } else {

      echo "User Found";

    }
 }

 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf8');
 mysqli_close($mysqli);

?>