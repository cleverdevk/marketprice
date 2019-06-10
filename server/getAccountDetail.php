<?php

 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf-8');

 $mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

 if(!$mysqli) {

   die('could not connect'.mysql_error());

 } else {

   $no = $_POST['no'];

   $sql = "select * from accounting_elements where accountingno = '".$no."'";

   $res = mysqli_query($mysqli,$sql);

   $result = array();

   while($row = mysqli_fetch_array($res)) {

     $row_array['no'] = $row['no'];
     $row_array['accountingno'] = $row['accountingno'];
     $row_array['name'] = $row['name'];
     $row_array['cost'] = $row['cost'];
     $row_array['date'] = $row['date'];


     array_push($result,$row_array);
   }
   $json = json_encode($result);
   echo $json;
 }

 mysqli_close($mysqli);

?>
