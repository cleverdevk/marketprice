<?php

 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf-8');
 
 $mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");
 $arr['depart_address'] = $_POST['depart_address'];
 $arr['depart_lag'] = $_POST['depart_lag'];
 $arr['depart_lng'] = $_POST['depart_lng'];
 $arr['arrival_address'] = $_POST['arrival_address'];
 $arr['arrival_lag'] = $_POST['arrival_lag'];
 $arr['arrival_lng'] = $_POST['arrival_lng'];
 $arr['transportation'] = $_POST['transportation'];

 $json2 = json_encode($arr,JSON_UNESCAPED_UNICODE);
//echo $json2;


 if(!$mysqli) {
   echo "1";
   die('could not connect'.mysql_error());

  }else{
mysqli_query("set names utf8");
mysqli_query("set session character_set_connection=utf8;"); 
mysqli_query("set session character_set_results=utf8;");
 mysqli_query("set session character_set_client=utf8;");
   $sql = "select * from review_transport";

$template = 'select distance, cost, timeslot, start_address, end_address, (6371*acos(cos(radians( $first ))* cos(radians(start_lat))*cos(radians(start_lng)-radians( $second )) +sin(radians( $third ))*sin(radians(start_lat)))) AS distance2 from review_transport having distance2<=5 ORDER BY distance DESC LIMIT 0,10';

$vars = array(
  '$first' => $_POST['depart_lag'],
  '$second' => $_POST['depart_lng'],
  '$third' => $arr['depart_lag'],
);

$sql2 = strtr($template, $vars); 

 // $sql2 = "select distance, cost, timeslot, start_address, end_address, (6371*acos(cos(radians($_POST['depart_lag']))* cos(radians(start_lat))*cos(radians(start_lng)-radians($_POST['depart_lng'])) +sin(radians($arr['depart_lag']))*sin(radians(start_lat)))) AS distance from review_transport having distance<=5 ORDER BY distance DESC LIMIT 0,10";
   
   $res = mysqli_query($mysqli,$sql);
   $res2 = mysqli_query($mysqli,$sql2);
   
   $result = array();
   $arr_cost = 0;
   $arr_distance = 0;
   $average_cost = 0;
   $count = 0;

/*
$row = mysqli_fetch_array($res);
$row = mysqli_fetch_array($res);
$row = mysqli_fetch_array($res);
$row = mysqli_fetch_array($res);
$row = mysqli_fetch_array($res);
$row = mysqli_fetch_array($res);

$ressss['first'] = $arr['depart_lag'] == $row['start_lat'];
$ressss['second'] = $arr['depart_lng'] == $row['start_lng'];
$ressss['third'] = $arr['arrival_lag'] == $row['end_lat'];
$ressss['fourth'] = $arr['arrival_lng'] == $row['end_lng'];
$ressss['fifth'] = $arr['transportation'] == $row['type'];
$ressss['sixth'] = "
echo json_encode($ressss, JSON_UNESCAPED_UNICODE);
}
*/
   
    while($row = mysqli_fetch_array($res)) {
    if($arr['depart_lag'] == $row['start_lat'] && $arr['depart_lng'] == $row['start_lng'] && $arr['arrival_lag'] == $row['end_lat'] && $arr['arrival_lng'] == $row['end_lng'] && $arr['transportation'] == $row['type']){
$count++;
      $arr_cost += $row['cost'];
      $arr_distance = $row['distance'];
    
    }else{
      

    }     
   }
if($count != 0){
    $average_cost = $arr_cost/$count;
   }

   $first_array['distance'] = $arr_distance;
   $first_array['average_cost'] = $average_cost;
//echo var_dump($first_array);
   array_push($result, $first_array);
   // $first_json = json_encode($result, JSON_UNESCAPED_UNICODE);
   // echo $first_json;
//return;
   $second_array = array();
   while($row2 = mysqli_fetch_array($res2)){
    $temp['distance'] = $row2['distance'];
    $temp['cost'] = $row2['cost'];
    $temp['timeslot'] = $row2['timeslot'];
    $temp['start_address'] = $row2['start_address'];
    $temp['end_address'] = $row2['end_address'];
    array_push($second_array, $temp);
   }
   array_push($result, $second_array);
   $second_json = json_encode($result, JSON_UNESCAPED_UNICODE);
   echo $second_json;



}


 mysqli_close($mysqli);

?>
