package com.lytvyn.slideshowpresenter.network;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public final class FTPWorker {
    private static File STORAGE_DIR = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SlideshowImages/");
    private static String SERVER_ADDRESS = "snackmonsterz.com";
    private static String FTP_USER = "nastya@snackmonsterz.com";
    private static String FTP_PASSWORD = "Sl1desh0w";
    private static String REMOTE_DIRECTORY = "/images/monsterz";

    public static FTPClient cacheImagesFromServer() {
        boolean status ;
        FTPClient mFtpClient = new FTPClient();
        String[] okFileExtensions =  new String[] {"jpg", "png","jpeg"};
        try {
            mFtpClient.setConnectTimeout(10 * 1000);
            mFtpClient.connect(InetAddress.getByName(SERVER_ADDRESS));

            status = mFtpClient.login(FTP_USER, FTP_PASSWORD);
            Log.d("isFTPConnected", String.valueOf(status));
            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                mFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFtpClient.enterLocalPassiveMode();
                mFtpClient.changeWorkingDirectory(REMOTE_DIRECTORY);
                FTPFile[] mImagesArray = mFtpClient.listFiles();

                Log.d("Size mImagesArray", String.valueOf(mImagesArray.length));

                for (int i = 0; i < mImagesArray.length; ++i) {
                    for (String extension : okFileExtensions)
                    {
                        if (mImagesArray[i].getName().toLowerCase().endsWith(extension))
                        {
                            String remoteFile = REMOTE_DIRECTORY + '/' + mImagesArray[i].getName();

                            File image = new File(STORAGE_DIR + "/" + mImagesArray[i].getName());

                            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(image));

                            boolean success = mFtpClient.retrieveFile(remoteFile, outputStream1);
                            outputStream1.flush();
                            outputStream1.close();

                            if (success) {
                                Log.d("FTPTask", "File " + mImagesArray[i].getName() +  " has been downloaded successfully.");
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mFtpClient.isConnected()) {
                    mFtpClient.logout();
                    mFtpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return mFtpClient;
    }
}
