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

import android.content.pm.PackageManager;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    int REQUEST_CODE_PERMISSIONS=101;

    String [] RequiredPermissions=new String[]{"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};
    TextureView textureView;
    ImageButton imageButton;
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

            }
        });

        ImageCaptureConfig imgConfig= new ImageCaptureConfig.Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        ImageCapture imgCapture=new ImageCapture(imgConfig);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file=new File("sdcard/DCIM/OpenCamera"+System.currentTimeMillis());
                imgCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


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