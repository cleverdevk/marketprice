<?php

 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf-8');

 $mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

 if(!$mysqli) {

   die('could not connect'.mysql_error());

 } else {

   $sql = "select * from review_food ";

   $res = mysqli_query($mysqli,$sql);

   //mysql_query("set session character_set_connection=utf8;");
   //mysql_query("set session character_set_results=utf8;");
   //mysql_query("set session character_set_client=utf8;");

   $result = array();

   while($row = mysqli_fetch_array($res)) {

     $row_array['no'] = $row['no'];
     $row_array['lat'] = $row['lat'];
     $row_array['lng'] = $row['lng'];
     $row_array['imageurl'] = $row['imageurl'];
     $row_array['content'] = $row['content'];
     $row_array['rate'] = $row['rate'];
     $row_array['cost'] = $row['cost'];
     $row_array['name'] = $row['name'];
     $row_array['good'] = $row['good'];
     $row_array['bad'] = $row['bad'];

     array_push($result,$row_array);
   }
   $json = json_encode($result);
   echo $json;
 }

 mysqli_close($mysqli);

?>
