<?php
$mysqli = mysqli_connect( "marketpricedb.c8v4cewfmjdt.ap-northeast-2.rds.amazonaws.com", "MarketPrice", "price12345" ,"marketprice");

if(!$mysqli) {

die('could not connect'.mysql_error());

} else {

$no = $_POST['no'];
$accountingno = $_POST['accountingno'];
$cost = $_POST['cost'];
$name = $_POST['name'];
$date = $_POST['date'];

$sql = 'insert into accounting_elements(accountingno,name,cost,date) values ('.$accountingno.",'".$name."',".$cost.",'".$date."');";

   $result = mysqli_query($mysqli,$sql);
   echo $result;
   echo "Hello";
}
 header('Content-Type: text/html; charset=utf-8');
 header('Content-Type: application/json; charset=utf8');
 mysqli_close($mysqli);

?>

