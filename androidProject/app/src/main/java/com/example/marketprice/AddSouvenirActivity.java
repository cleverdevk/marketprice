package com.example.marketprice;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddSouvenirActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {

    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;

    Bitmap recordPicture = null;

    private Uri photoUri;
    String datapath = "" ;
    private String timeStamp;
    private String mCurrentPhotoPath;
    File croppedFileName = null;

    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonS3 s3;
    TransferUtility transferUtility;

    private ImageView img;
    private ImageButton camera;
    private EditText souvenirName;
    private EditText souvenirPrice;
    private EditText souvenirReview;
    private EditText souvenirLocation;
    private ImageButton locationBtn;
    private RatingBar ratingBar;
    private ToggleButton shareAccounting;
    private Button upload;

    String name;
    int price;
    String review;
    String location ="";
    float rate;
    double lat;
    double lon;

    private static String souvenirNameValue = "";
    private static String souvenirPriceValue = "";
    private static String souvenirReviewValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsouvenir);


        img = (ImageView) findViewById(R.id.imageView);
        camera = (ImageButton) findViewById(R.id.cameraBtn);
        souvenirName = (EditText) findViewById(R.id.souvenir_name);
        souvenirPrice = (EditText) findViewById(R.id.souvenir_price);
        souvenirReview = (EditText) findViewById(R.id.souvenir_review);
        souvenirLocation = (EditText) findViewById(R.id.souvenir_location);
        locationBtn = (ImageButton) findViewById(R.id.location);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        shareAccounting = (ToggleButton) findViewById(R.id.shareaccounting);
        upload = (Button) findViewById(R.id.save);

        souvenirName.setText(souvenirNameValue);
        souvenirPrice.setText(souvenirPriceValue);
        souvenirReview.setText(souvenirReviewValue);

        ratingBar.setOnRatingBarChangeListener(AddSouvenirActivity.this);
        // 카메라 버튼 리스너
        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // 사진 찍기, 갤러기에서 불러오기 옵션 선택
                showPicutreDialog();
            }
        });


        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:a8b6bd6a-6f45-4d45-bea6-fe77d0d044c5", // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전
        );

        s3 = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3, getApplicationContext());

        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");


        // 입력 완료 버튼 리스너
        upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                name = souvenirName.getText().toString();
                price = Integer.parseInt(souvenirPrice.getText().toString());
                review = souvenirReview.getText().toString();
                location = souvenirLocation.getText().toString();
                TransferObserver observer = transferUtility.upload(
                        "marketprice-s3", /* 업로드 할 버킷 이름 */
                        croppedFileName.getName(), /* 버킷에 저장할 파일의 이름 */
                        croppedFileName /* 버킷에 저장할 파일 */
                );
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        Log.d("log", "state changed. id= "+id+"\tstate = " +state);
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                        int percentage = (int) (bytesCurrent/bytesTotal * 100);
//
//                        publishProgress(percentage);
                        Log.d("log", "onProgressChanged = "  );
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.d("log", "error in uploading. id="+id+"\nException = "+ex);
                    }
                });

                UploadImageToServer(name, price, review);
                Toast.makeText(AddSouvenirActivity.this, "기념품 정보 입력을 완료하였습니다!", Toast.LENGTH_SHORT).show();

            }
        });

        // check permissions
        ActivityCompat.requestPermissions( AddSouvenirActivity.this, permissions, MULTIPLE_PERMISSIONS);
        checkPermissions();

        //위치 추가 버튼 클릭 시
        locationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                souvenirNameValue = souvenirName.getText().toString();
                souvenirPriceValue = souvenirPrice.getText().toString();
                souvenirReviewValue = souvenirReview.getText().toString();
                showGoogleMap();

            }
        });

        // 지도에서 음식점 정보 받아서 넘어옴.
        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            location = "위치 추가";
        }
        else {

            lat = extras.getDouble("lag");
            lon = extras.getDouble("lng");

            Log.d("받아진 위도", " " + lat);

            location = extras.getString("address");
        }
        Log.d("액티비티에서 ", location);
        souvenirLocation.setText(location);

        datapath = getFilesDir() + "/tesseract/";

        checkFile(new File(datapath + "tessdata/"));


    }



    //서버로 전송
    private void UploadImageToServer(String name, int price, String review) {

        OkHttpClient client = new OkHttpClient();

        RequestBody body= new FormBody.Builder()
                .add("id","test")
                .add("lat",Double.toString(lat))
                .add("lng",Double.toString(lon))
                .add("imageurl", croppedFileName.getName())
                .add("content",review)
                .add("rate", Float.toString(rate))
                .add("cost", Integer.toString(price))
                .add("name",name)
                .add("good", "0")
                .add("bad", "0").build();

        Request request = new Request.Builder()
                .url("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/inputFood.php")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.d("[INBAE_FAILURE]",mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body().string();
                Log.d("[INBAE_SUCCESS]",mMessage);
            }


        });

        finish();
    }

    // 구글맵 띄워서 위치 받아오기
    private void showGoogleMap() {
        Intent intent = new Intent(AddSouvenirActivity.this, AddFoodLocation2.class);
        startActivity(intent);




    }


    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void showPicutreDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("옵션 선택");
        String[] pictureDialogItems = {"갤러리에서 선택", "카메라 촬영"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        choosePhotoFromGallary();  // 갤러리에서 선택
                        break;
                    case 1:
                        takePhotoFromCamera(); // 카메라 촬영
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    // 갤러리에서 선택
    public void choosePhotoFromGallary(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // 카메라 촬영
    public void takePhotoFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        Log.d("application uri : ", getApplicationContext().getPackageName());
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(AddSouvenirActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            Log.d("takePhoto Err : ", e.getMessage().toString());
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(AddSouvenirActivity.this,
                    getApplicationContext().getPackageName() + ".fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    // image to file
    private File createImageFile() throws IOException {
        timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String imageFileName = "nostest_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
        Log.d("tq", storageDir.toString());
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) { // 갤러리에서 불러오기
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) { // 카메라로 직접 촬영
            cropImage();
            // 갤러리에 나타나게
            MediaScannerConnection.scanFile(AddSouvenirActivity.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("scan", "completed");
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                //recordPicture = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
                Matrix matrix = new Matrix();
                recordPicture = null;

                recordPicture = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                recordPicture.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 압축

//                ((ImageView)findViewById(R.id.imageView)).setImageBitmap(recordPicture);
                img.setImageBitmap(recordPicture);

            }catch (Exception e){
                Log.e("ERROR", e.getMessage().toString());
            }


        }
    }

    public void cropImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.grantUriPermission("com.android.camera", photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.putExtra("crop", "true");

            intent.putExtra("scale", true);

            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(AddSouvenirActivity.this,
                    getApplicationContext().getPackageName() + ".fileprovider", tempFile);

            Log.d("dkr!!!!", croppedFileName.getName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }


            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                grantUriPermission(res.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }


    private void copyFiles() {
        try{
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {

        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles();
        }

        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles();
            }
        }
    }



    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        rate = rating;

    }
}