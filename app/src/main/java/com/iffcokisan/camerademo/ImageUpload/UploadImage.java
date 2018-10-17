package com.iffcokisan.camerademo.ImageUpload;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Taufiq on 25-04-2017.
 */

public class UploadImage extends AsyncTask<Bitmap, String,String> {
    Context context;

    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;

    UploadImageResult uploadImageResult;
    JSONObject result=null;
    int taskCode;
    String resultNew="",imagePath="";
//    PrefencesManager prefencesManager;
    ProgressDialog mProgressDialog;
    File sourceFile;
    int totalSize = 0;
    String FILE_UPLOAD_URL = "";

    public UploadImage(Context context,String imagePath, int taskCode, UploadImageResult uploadImageResult) {
        this.context = context;
        this.imagePath=imagePath;
        this.taskCode=taskCode;
        this.uploadImageResult = uploadImageResult;
//        prefencesManager=new PrefencesManager(context);
        FILE_UPLOAD_URL = "http://43.242.124.76:8056/api/DocumentUpload/MediaUpload?TokenId=2a3e5ad1-294d-47f4-9737-8e177057191e";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        imagePath=compressImage(imagePath);
        sourceFile = new File(imagePath);
        totalSize = (int)sourceFile.length();

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Uploading image, Please wait...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mProgressDialog.setProgress(Integer.parseInt(values[0]));
    }

    @Override
    protected String doInBackground(Bitmap... args) {
        try {
//            if (taskCode==Constances.UPLOAD_AGRIBOT_IMAGE){
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection connection = null;

                try {
                    connection = (HttpURLConnection) new URL(FILE_UPLOAD_URL).openConnection();
                    connection.setRequestMethod("POST");
                    String boundary = "---------------------------boundary";
                    String tail = "\r\n--" + boundary + "--\r\n";
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                    String metadataPart = "--" + boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"FileType\"\r\n\r\n"
                            + "Image" + "\r\n";

                    String fileHeader1 = "--" + boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"files\"; filename=\""
                            + "image.png" + "\"\r\n"
                            + "Content-Type: application/octet-stream\r\n"
                            + "Content-Transfer-Encoding: binary\r\n";

                    long fileLength = sourceFile.length() + tail.length();
                    String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                    String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
                    String stringData = metadataPart + fileHeader;

                    long requestLength = stringData.length() + fileLength;
                    connection.setRequestProperty("Content-length", "" + requestLength);
                    connection.setFixedLengthStreamingMode((int) requestLength);
                    connection.connect();

                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(stringData);
                    out.flush();

                    int progress = 0;
                    int bytesRead = 0;
                    byte buf[] = new byte[1024];
                    BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFile));
                    while ((bytesRead = bufInput.read(buf)) != -1) {
                        // write output
                        out.write(buf, 0, bytesRead);
                        out.flush();
                        progress += bytesRead; // Here progress is total uploaded bytes

                        publishProgress(""+(int)((progress*100)/totalSize)); // sending progress percent to publishProgress
                    }

                    // Write closing boundary and close stream
                    out.writeBytes(tail);
                    out.flush();
                    out.close();

                    // Get server response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    StringBuilder builder = new StringBuilder();
                    while((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    resultNew=builder.toString();

                } catch (Exception e) {
                    // Exception
                } finally {
                    if (connection != null) connection.disconnect();
                }
                return null;
//            }else {
//
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mProgressDialog!=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        if (!resultNew.equalsIgnoreCase("")){
            try {
                result=new JSONObject(resultNew);
                uploadImageResult.onTaskCompleted(result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mProgressDialog!=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    public String compressImage(String imagePath) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if(bmp!=null)
        {
            bmp.recycle();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filepath = getFilename();
        try {
            out = new FileOutputStream(filepath);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filepath;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files/Compressed");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }

        String mImageName="IMG_"+ String.valueOf(System.currentTimeMillis()) +".jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);;
        return uriString;
    }
}
