<?php

 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf-8');

 $mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

 if(!$mysqli) {

   die('could not connect'.mysql_error());

 } else {

   $sql = "select * from accounting ";

   $res = mysqli_query($mysqli,$sql);

   $result = array();

   while($row = mysqli_fetch_array($res)) {

     $row_array['no'] = $row['no'];
     $row_array['id'] = $row['id'];
     $row_array['lat'] = $row['lat'];
     $row_array['lng'] = $row['lng'];
     $row_array['title'] = $row['title'];
     $row_array['content'] = $row['content'];
     $row_array['start_time'] = $row['start_time'];
     $row_array['end_time'] = $row['end_time'];
     $row_array['member'] = $row['member'];
     $row_array['share'] = $row['share'];

     array_push($result,$row_array);
   }
   $json = json_encode($result);
   echo $json;
 }

 mysqli_close($mysqli);

?>
