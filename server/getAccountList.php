<?php

 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf-8');

 $mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

 if(!$mysqli) {

   die('could not connect'.mysql_error());

 } else {

   $result = array();

   $sql0 = "SELECT count(*) as count from accounting";

   $res0 = mysqli_query($mysqli,$sql0);

   while($row = mysqli_fetch_array($res0)) {

     $row_ar['count'] = $row['count'];

     array_push($result,$row_ar);
   }
   

   $sql1 = "select * from accounting ";

   $res1 = mysqli_query($mysqli,$sql1);



   while($row = mysqli_fetch_array($res1)) {

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

   $sql2 = "select accounting.no, SUM(IFNULL(cost, 0)) as cost from accounting_elements right outer join accounting on accounting_elements.accountingno = accounting.no group by accounting.no;";

   $res2 = mysqli_query($mysqli,$sql2);


   while($row = mysqli_fetch_array($res2)) {

     $row_arr['no'] = $row['no'];
     $row_arr['cost'] = $row['cost'];

     array_push($result,$row_arr);
   }


   $json = json_encode($result);
   echo $json;
 }

 mysqli_close($mysqli);

?>
