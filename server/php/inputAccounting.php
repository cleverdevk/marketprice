<?php
$mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

if(!$mysqli) {

die('could not connect'.mysql_error());

} else {

$id = $_POST['id'];
$title = $_POST['title'];
$lat = $_POST['lat'];
$lng = $_POST['lng'];
$content = $_POST['content'];
$start_time = $_POST['start_time'];
$end_time = $_POST['end_time'];
$member = $_POST['member'];
$share = $_POST['share'];

$sql = "insert into accounting(id, title, content, lat, lng, start_time, end_time, member, share)
        values ('".$id."','".$title."','".$content."',".$lat.",".$lng.",'".$start_time."','"
        .$end_time."',".$member.",".$share.");";
echo $sql;
$result = mysqli_query($mysqli, $sql);
echo $result;
}
 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf8');
 mysqli_close($mysqli);

?>

