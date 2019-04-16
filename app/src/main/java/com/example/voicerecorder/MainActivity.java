package com.example.voicerecorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private MyFTPClientFunctions ftpclient = null;
    public FTPClient mFTPClient = null; // Add top of the class

    Button recordBtn;
    //Button saveBtn;

    String path = "";

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private static final int REQUEST_PERMISSION_CODE = 1000;

    public boolean ftpConnect(String host, String username, String password, int port, Context context) {
        try {
            mFTPClient = new FTPClient();
            // connecting to the host
            mFTPClient.connect(host, port);
            boolean status = mFTPClient.login(username, password);
            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password

                /*
                 * Set File Transfer Mode
                 * To avoid corruption issue you must specified a correct
                 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
                 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
                 * transferring text, image, and compressed files.
                 */
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                return status;
            }
        } catch (Exception e) {
            Toast.makeText(context, "Fail to connect to FTP", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ftpclient = new MyFTPClientFunctions();

        recordBtn = findViewById(R.id.recordBtn);
        //saveBtn = findViewById(R.id.saveBtn);

        //ftpConnect("www.mngo.in", "aditya@mngo.in", "1980AMS{4534&MNgo}", 21, MainActivity.this);

        //ftpConnect("103.250.184.230", "aditya@mngo.in", "1980AMS{4534&MNgo}", 21, MainActivity.this);

//        MainActivity.this.runOnUiThread(new Runnable() {
//            public void run() {
//                boolean status = false;
//                // host – your FTP address
//                // username & password – for your secured login
//                // 21 default gateway for FTP
//                status = ftpclient.ftpConnect("ftp.mngo.in", "aditya@mngo.in", "1980AMS{4534&MNgo}", 21, MainActivity.this);
//                if (status == true) {
//                    Toast.makeText(MainActivity.this, "connection success", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "connection failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        if(checkPermissionFromDevice())
        {
            mediaRecorder = new MediaRecorder();

            recordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String btnName = recordBtn.getText().toString();

                    if(btnName.equals("Record"))
                    {
                        recordBtn.setText("Stop");

                        Toast.makeText(MainActivity.this, "Ready", Toast.LENGTH_SHORT).show();

                        String date = new SimpleDateFormat("dd-MM-YY-hh:mm:ss").format(new Date());

                        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                date + "record.3gp";

                        try
                        {
                            setupMediaRecorder();

                            mediaRecorder.prepare();
                            mediaRecorder.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(btnName.equals("Stop"))
                    {
                        recordBtn.setText("Record");

                        mediaRecorder.stop();
                    }
                }
            });
        }
        else
        {
            requestPermission();

            recordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Please accept all the permission and restart the app", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

//function to setup media recorder
    private void setupMediaRecorder()
    {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(path);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
        {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
        }, REQUEST_PERMISSION_CODE);
    }

//for asking for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode)
       {
           case REQUEST_PERMISSION_CODE:
           {
           //restarting app
               finish();
               startActivity(getIntent());

               if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
               {
                   Toast.makeText(this, "Permission Granted! Please Restart the App.", Toast.LENGTH_SHORT);
               }
               else
               {
                   Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT);
               }
           }

           break;
       }
    }

//for checking permission
    private boolean checkPermissionFromDevice()
    {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return (write_external_storage_result == PackageManager.PERMISSION_GRANTED) && (record_audio_result == PackageManager.PERMISSION_GRANTED);
    }
}
