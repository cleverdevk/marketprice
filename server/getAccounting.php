<?php

 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf-8');

 $mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

 if(!$mysqli) {

   die('could not connect'.mysql_error());

 } else {

   $id = $_POST['id'];

   $sql = "select * from accounting where id = '".$id."';";

   $res = mysqli_query($mysqli,$sql);

   //mysql_query("set session character_set_connection=utf8;");
   //mysql_query("set session character_set_results=utf8;");
   //mysql_query("set session character_set_client=utf8;");

   $result = array();

   while($row = mysqli_fetch_array($res)) {

     $row_array['no'] = $row['no'];
     $row_array['title'] = $row['title'];
     $row_array['start_time'] = $row['start_time'];
     $row_array['end_time'] = $row['end_time'];

     array_push($result,$row_array);
   }
   $json = json_encode($result);
   echo $json;
 }

 mysqli_close($mysqli);

?>
