<?php

 $mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

 if(!$mysqli) {

   die('could not connect'.mysql_error());

 } else {

   $id = $_POST['id'];

   $sql = "select * from user where id = '".$id."'";

   $result = mysqli_query($mysqli,$sql);

   $rows = mysqli_num_rows($result);

   if($rows == 0) {

     echo "User Not Found";

   } else {

      $result_arr = array();
      while($row = mysqli_fetch_array($result)) {
         $row_array['id'] = $row['id'];
         $row_array['password'] = $row['password'];
         $row_array['salt'] = $row['salt'];
         array_push($result_arr,$row_array);
      }
      $json = json_encode($result_arr);
      echo $json;
   }
}

 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf8');
 mysqli_close($mysqli);

?>