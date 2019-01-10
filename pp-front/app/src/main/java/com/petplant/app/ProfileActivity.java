package com.petplant.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    Toolbar myToolbar;
    static final int REQUEST_PERMISSION_CAMERA = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_ALBUM = 2;
    static final int REQUEST_IMAGE_CROP = 3;

    Uri imageURI;
    String mCurrentPhotoPath;

    ImageView iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myToolbar = (Toolbar)findViewById(R.id.profile_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menuicon);
        iv = (ImageView)findViewById(R.id.profile_photo);

        Button cameraBtn = (Button)findViewById(R.id.profile_takepicture_btn);
        cameraBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureCamera();
            }
        });

        Button searchBtn = (Button)findViewById(R.id.profile_search_btn);
        searchBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });

        checkPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            default:
                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.profile_drawer);
                if(!drawer.isDrawerOpen(Gravity.LEFT)){
                    drawer.openDrawer(Gravity.LEFT);
                }else{
                    drawer.closeDrawer(Gravity.LEFT);
                }
                return super.onOptionsItemSelected(item);
        }
    }

    private void captureCamera(){
        String state = Environment.getExternalStorageState();
        //외장 메모리 검사
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(takePictureIntent.resolveActivity(getPackageManager())!=null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch(IOException e){
                    Log.e("captureCamera Error",e.toString());
                }
                if(photoFile != null){
                    //getUriForFile의 두번째 인자는 Menifast provider의 authorites와 일치해야 함
                    Uri providerURI = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                    imageURI = providerURI;

                    //인텐트에 전달할 때는 FileProvider의 Return값인 content://로만 하는 것이 중요!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,providerURI);

                    startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
                }
            }
        }else{
            Toast.makeText(this,"저장공간이 접근 불가능한 기기입니다.",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private File createImageFile() throws IOException{
        //이미지 파일 이름 생성
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PetPlant_"+timestamp+".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/Pictures","PetPlant");

        if(!storageDir.exists()){
            Log.i("mCurrentPhotoPath1",storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir,imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void galleryAddPic(){
        Log.i("galleryAddPic","Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this,"Image saved in album",Toast.LENGTH_SHORT).show();
    }

    private void cropSingleImage(Uri photoUriPath){
        Log.i("cropSingleImage","call");
        Log.i("cropSingleImage","photoUriPath : "+photoUriPath);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        cropIntent.setDataAndType(photoUriPath,"image/*");
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("outputX",224);
        cropIntent.putExtra("outputY",224);
        Log.i("cropSingleImage","photoUriPath2 : "+photoUriPath);

        cropIntent.putExtra("scale",true);
        cropIntent.putExtra("output",photoUriPath);

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(cropIntent,0);
        grantUriPermission(list.get(0).activityInfo.packageName,photoUriPath,Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = new Intent(cropIntent);
        ResolveInfo res = list.get(0);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        grantUriPermission(res.activityInfo.packageName,photoUriPath,Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setComponent(new ComponentName(res.activityInfo.packageName,res.activityInfo.name));

        startActivityForResult(intent,REQUEST_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode == Activity.RESULT_OK){
                    try{
                        Log.i("REQUEST_IMAGE_CAPTURE","OK");
                        cropSingleImage(imageURI);
                    }catch (Exception e){
                        Log.e("REQUEST_IMAGE_CAPTURE",e.toString());
                    }
                }else{
                    Toast.makeText(ProfileActivity.this,"Iamge capture canceled",Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_IMAGE_CROP:
                if(resultCode == Activity.RESULT_OK){
                    galleryAddPic();
                    iv.setImageURI(imageURI);
                }
                break;
        }
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                new AlertDialog.Builder(this)
                        .setTitle("Notice")
                        .setMessage("Permission Denied. If you want to use this app, Please allow this permission")
                        .setNeutralButton("Setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:"+getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},REQUEST_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case REQUEST_PERMISSION_CAMERA:
                for(int i=0;i<grantResults.length;i++){
                    //grantResults[] : 허용 권한은 0, 거부 권한은 -1
                    if(grantResults[i]<0){
                        Toast.makeText(ProfileActivity.this,"Please allow "+permissions[i],Toast.LENGTH_SHORT).show();
                        finishAffinity();
                        System.runFinalization();
                        System.exit(0);
                        return;
                    }
                    break;
                }
        }
    }

    private void sendImage(){

    }
}
