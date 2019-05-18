<?php

$mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

 if(!$mysqli) {

   die('could not connect'.mysql_error());

 } else {

   $id = $_POST['id'];
   $lat = $_POST['lat'];
   $lng = $_POST['lng'];
   $imageurl = $_POST['imageurl'];
   $content = $_POST['content'];
   $rate = $_POST['rate'];
   $cost = $_POST['cost'];
   $name = $_POST['name'];
   $good = $_POST['good'];
   $bad = $_POST['bad'];
   

   $sql = "insert into review_food(id,lat,lng,imageurl,content,rate,cost,name,good,bad) values ('".$id."',".$lat.",".$lng.",'".$imageurl."','".$content."',".$rate.",".$cost.",'".$name."',".$good.",".$bad.");";
   echo $sql;
   $result = mysqli_query($mysqli,$sql);
   echo $result;
   echo "Hello";
}
 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf8');
 mysqli_close($mysqli);

?>
