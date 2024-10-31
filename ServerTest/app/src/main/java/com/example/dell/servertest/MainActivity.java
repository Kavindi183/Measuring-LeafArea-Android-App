package com.example.dell.servertest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    ImageView camera, gallery, upload, image, getArea, show;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;

    TextView area;

    public String path = "/sdcard/leafArea/area.txt"; //path of the folder which the text file is downloaded

    //Button btn;

    String selectedPhoto;

    final int CAMERA_REQUEST = 13323;
    final int GALLERY_REQUEST = 22131;



    /*give path of the folder in server which the text file is saved.the value in that text file should be read

    here 192.168.43.39 is the IP address of the server.

    If we change the server the code should be changed here


    */


    String fileDownloadPath="http://192.168.43.39/news/upload/area.txt";
    //String fileDownloadPath = "http://192.168.43.212/news/upload/area.txt";
    //String fileDownloadPath="http://10.0.3.2/news/upload/area.txt";
    //String saveFilePath="/data/data/com.example.FileDownloadDemo/area.txt";
    String saveFilePath = "/sdcard/leafArea/area.txt";

    ProgressDialog dialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image);
        camera = (ImageView) findViewById(R.id.camera);
        gallery = (ImageView) findViewById(R.id.gallery);
        upload = (ImageView) findViewById(R.id.upload);
        getArea = (ImageView) findViewById(R.id.result);
        area = (TextView) findViewById(R.id.resultShow);

        //btn=(Button)findViewById(R.id.result);

        cameraPhoto = new CameraPhoto(getApplicationContext());
        galleryPhoto = new GalleryPhoto(getApplicationContext());

        final File file = new File(path);


        //Area calculate button

        getArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(MainActivity.this, "", "Calculated Area is Being Downloaded", true);
                //create thread

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        downloadFile(fileDownloadPath, saveFilePath);//call the file download method to download the text file from the server

                    }
                }).start();


                //StringBuilder text=new StringBuilder();
                if(file.exists()){
                    StringBuilder text=new StringBuilder();
                    try {
                        BufferedReader br=new BufferedReader(new FileReader(file));
                        String line;
                        while ((line=br.readLine())!=null){
                            text.append(line);
                            //text.append('\n');
                        }


                    }catch (IOException e){
                        Toast.makeText(getApplicationContext(), "file not Downloaded", Toast.LENGTH_SHORT).show();
                    }

                    area.setText(text);
                    //text.replace(0,text.length(),"");
                }else {

                    area.setText("Sorry area can not calculated");
                    //text.replace(0,text.length(),"");
                }

            }
        });


        // Camera opening button

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                    cameraPhoto.addToGallery();

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Something Wrong While Taking Photos" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //gellery opening button

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });


        //image upload button

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedPhoto.equals("") || selectedPhoto == null) {
                    Toast.makeText(getApplicationContext(), "No Image Selected." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(1024, 1024).getBitmap();
                    String encodedImage = ImageBase64.encode(bitmap);
                    Log.d(TAG, encodedImage);

                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("image", encodedImage);

                    PostResponseAsyncTask task = new PostResponseAsyncTask(MainActivity.this, postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            if (s.contains("uploaded_success")) {
                                Toast.makeText(getApplicationContext(), "Image Uploaded Successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error While Uploading",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                    //here the upload.php file in the server is executed


                    //task.execute("http://192.168.43.212/news/upload.php");
                    task.execute("http://192.168.43.39/news/upload.php");
                    //task.execute("http://10.0.3.2/news/upload.php");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Toast.makeText(getApplicationContext(), "Cannot Connect to server.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Toast.makeText(getApplicationContext(), "Cannot Connect to server.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Toast.makeText(getApplicationContext(), "URL Error.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Toast.makeText(getApplicationContext(), "Protocol Error.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Encoding Error", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    //To download the text  file in the server


    public void downloadFile(String fileDownloadPath, String saveFilePath) {

        try {

            File SaveFile = new File(saveFilePath);
            URL u = new URL(fileDownloadPath);
            URLConnection con = u.openConnection();
            int lengthfoContent = con.getContentLength();

            DataInputStream DIStream = new DataInputStream(u.openStream());
            byte[] buffer = new byte[lengthfoContent];
            DIStream.readFully(buffer);
            DIStream.close();


            DataOutputStream DOStream = new DataOutputStream(new FileOutputStream(saveFilePath));
            DOStream.write(buffer);
            DOStream.flush();
            DOStream.close();

            hideProgressIndicater();


        } catch (FileNotFoundException e) {

            Toast.makeText(getApplicationContext(), "File Can not be found.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error while Downloading.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error while Downloading.", Toast.LENGTH_SHORT).show();
        }


    }


    public void hideProgressIndicater() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                String photoPath = cameraPhoto.getPhotoPath();
                selectedPhoto = photoPath;
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    image.setImageBitmap(getRotatedBitMap(bitmap, 90));

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Something Wrong While Loading Photos", Toast.LENGTH_SHORT).show();
                }

                Log.d(TAG, photoPath);
            } else if (requestCode == GALLERY_REQUEST) {
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);

                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;

                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    image.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Something Wrong While Choosing a Photo", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private Bitmap getRotatedBitMap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap bitmap1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return bitmap1;
    }

}
