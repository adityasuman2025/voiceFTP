package com.example.voicerecorder;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;

public class MyFTPClientFunctions
{
   public FTPClient mFTPClient = null; // Add top of the class

    public boolean ftpConnect(String host, String username, String password, int port, Context context) {
        try {
            mFTPClient = new FTPClient();
            // connecting to the host
            mFTPClient.connect(host, port);
            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password
                boolean status = mFTPClient.login(username, password);
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

    public boolean ftpDisconnect(Context context) {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Error occurred while disconnecting from ftp server.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean ftpUpload(String srcFilePath, String desFileName, String desDirectory, Context context) {
        boolean status = false;
        try {
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);
            // change working directory to the destination directory
            // if (ftpChangeDirectory(desDirectory)) {
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            // }
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "upload failed", Toast.LENGTH_SHORT).show();
        }
        return status;
    }
}
