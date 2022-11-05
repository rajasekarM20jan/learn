package com.example.learn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    int REQUEST_CODE_PERMISSIONS=101;

    String [] RequiredPermissions=new String[]{"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};
    TextureView textureView;
    Button imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView=findViewById(R.id.textureView);
        imageButton=findViewById(R.id.imageButton);

        if(permissionsGranted()){
            startCamera();
        }
        else{
            ActivityCompat.requestPermissions(this,RequiredPermissions,REQUEST_CODE_PERMISSIONS);
        }





    }

    private void startCamera() {
        CameraX.unbindAll();

        Rational aspectRatio=new Rational(textureView.getWidth(),textureView.getHeight());
        Size screen=new Size(textureView.getWidth(),textureView.getHeight());

        PreviewConfig config= new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
        Preview preview=new Preview(config);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup abcd=(ViewGroup) textureView.getParent();

                abcd.removeView(textureView);
                abcd.addView(textureView);

                textureView.setSurfaceTexture(output.getSurfaceTexture());
                transform();

            }
        });

        ImageCaptureConfig imgConfig= new ImageCaptureConfig.Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        ImageCapture imgCapture=new ImageCapture(imgConfig);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File dir = new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM), "IMG"+System.currentTimeMillis()+".jpg");
                imgCapture.takePicture(dir, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        System.out.println("Done "+file.getAbsolutePath());
                        Toast.makeText(MainActivity.this, "Done "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        System.out.println("Error "+cause);
                        Toast.makeText(MainActivity.this, "Error "+cause, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        CameraX.bindToLifecycle(MainActivity.this,preview,imgCapture);


    }

    void transform(){
        Matrix mx=new Matrix();

        float height= textureView.getMeasuredHeight();
        float width=textureView.getMeasuredWidth();

        float centerX=width/2f;
        float centerY=height/2f;

        int rotationDegree;
        int rotation= (int) textureView.getRotation();

        switch (rotation){
            case Surface.ROTATION_0:{
                rotationDegree=0;
                break;
            }

            case Surface.ROTATION_90:{
                rotationDegree=90;
                break;
            }

            case Surface.ROTATION_180: {
                rotationDegree=180;
                break;
            }
            case Surface.ROTATION_270:{
                rotationDegree=270;
                break;
            }
            default:{
                rotationDegree=0;
                break;
            }

        }
        mx.postRotate((float)rotationDegree,centerX,centerY);
        textureView.setTransform(mx);
    }





    private boolean permissionsGranted() {

        for(String permission:RequiredPermissions){

            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}