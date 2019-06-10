<?php
$mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

if(!$mysqli) {

die('could not connect'.mysql_error());

} else {

$id = $_POST['id'];
$start_lat = $_POST['start_lat'];
$start_lng = $_POST['start_lng'];
$end_lat = $_POST['end_lat'];
$end_lng = $_POST['end_lng'];
$distance = $_POST['distance'];
$cost = $_POST['cost'];
$start_address = $_POST['start_address'];
$end_address = $_POST['end_address'];
$type = $_POST['type'];
$timeslot = $_POST['timeslot'];
$json = $_POST['json'];

$sql = "insert into review_transport(id,start_lat,start_lng,end_lat,end_lng,distance,cost,start_address,end_address,json,type,timeslot) values ('".$id."',".$start_lat.",".$start_lng.",".$end_lat.",".$end_lng.",".$distance.",".$cost.",'".$start_address."','".$end_address."','".$json."','".$type."',".$timeslot.");";
   echo $sql;
   $result = mysqli_query($mysqli,$sql);
   echo $result;
   echo "Hello";
}
 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf8');
 mysqli_close($mysqli);

?>

