package com.iffcokisan.camerademo.ImageUpload;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iffcokisan.camerademo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ImageActivity extends AppCompatActivity {
//    public static final int PERMISSION_REQUEST_CODE = 114;//request code for Camera and External Storage permission
//    private static final int CAMERA_REQUEST_CODE = 133;//request code for capture image
//
//    private Uri fileUri = null;//Uri to capture image
//    private String getImageUrl = "";
//    private ImageView img_view;
String TAG=ImageActivity.class.getSimpleName();
TextView img_view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Button btn_captureImage=(Button)findViewById(R.id.btn_captureImage);
        img_view=(TextView) findViewById(R.id.img_view);
        btn_captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent();
            }
        });
    }

    Uri imageUri;
    String picturePath="";
    protected void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == 101){
            if (resultCode == Activity.RESULT_OK) {
                onCaptureImageResult(data);
            }
        }
    }

    protected void onCaptureImageResult(Intent data) {
        try {
            Bitmap bitmapSelected = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapSelected.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            picturePath=getRealPathFromURI(imageUri);
            uploadImage();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        if (imageUri!=null){
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                cursor = getContentResolver().query(contentUri,  proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return "";
    }

    private void uploadImage() {
        UploadImage uploadImage=new UploadImage(ImageActivity.this, picturePath, 101, new UploadImageResult() {
            @Override
            public void onTaskCompleted(JSONObject result) {
                if (result!=null){

                    Log.d(TAG,result.toString());
                    try {
                        boolean isSuccess=result.getBoolean("IsSuccess");
                        String message=result.getString("Message");
                        if (isSuccess){
                            img_view.setText(""+message);
//                            imageFileName=message;
//                            fetchDetails();
                        }else {
                            Toast.makeText(ImageActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(ImageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
        uploadImage.execute();
    }

    /*private boolean checkPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        for (String permission : getAllPermissions()) {
            int result = checkPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissions.add(permission);
            }
        }
        //If both permissions are granted
        if (permissions.size() == 0)
            allPermissionGranted();
        else
            //if any one of them are not granted then request permission
            requestPermission(permissions.toArray(new String[permissions.size()]));
        return true;
    }

    *//*   on both permission granted  *//*
    private void allPermissionGranted() {
        //Initiate capture image method
        if (isDeviceSupportCamera())
            captureImage();
    }

    *//*  Request permissions  *//*
    private void requestPermission(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    *//*  Permissions string array  *//*
    private String[] getAllPermissions() {
        return new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
    }

    *//*  Method to check permissions  *//*
    public static int checkPermission(final Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    *//*  on Capture image button click check permissions  *//*
    public void captureImage(View view) {
        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    int counter = 0;//counter to traverse all permissions
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //show alert dialog if any of the permission denied
                            showMessageOKCancel(getString(R.string.permission_message),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                //If user click on OK button check permission again.
                                                checkPermission();
                                            }
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(ImageActivity.this, R.string.capture_deny_message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            return;
                        } else {
                            counter++;
                            //If counter is equal to permissions length mean all permission granted.
                            if (counter == permissions.length)
                                allPermissionGranted();
                        }
                    }

                }


                break;
        }
    }


    *//*  Alert dialog on permission denied    *//*
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(ImageActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel, cancelListener)
                .setCancelable(false)
                .create()
                .show();
    }

    // Checking camera supportability
    private boolean isDeviceSupportCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA))
            return true;
        else {
            Toast.makeText(ImageActivity.this, getResources().getString(R.string.camera_not_supported), Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//Start intent with Action_Image_Capture
        fileUri = CameraUtils.getOutputMediaFileUri(this);//get fileUri from CameraUtils
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);//Send fileUri with intent
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                try {
                    //When image is captured successfully
                    if (resultCode == RESULT_OK) {

                        //Check if device SDK is greater than 22 then we get the actual image path via below method
                        if (Build.VERSION.SDK_INT > 22)
                            getImageUrl = ImagePath_MarshMallow.getPath(ImageActivity.this, fileUri);
                        else
                            //else we will get path directly
                            getImageUrl = fileUri.getPath();


                        //After image capture show captured image over image view
                        showCapturedImage();
                    } else
                        Toast.makeText(this, R.string.cancel_message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }


    *//*  Show Captured over ImageView  *//*
    private void showCapturedImage() {
        if (!getImageUrl.equals("") && getImageUrl != null)
            img_view.setImageBitmap(CameraUtils.convertImagePathToBitmap(getImageUrl, false));
        else
            Toast.makeText(this, R.string.capture_image_failed, Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    *//*
     * Here we restore the fileUri again
     *//*
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }*/

}
