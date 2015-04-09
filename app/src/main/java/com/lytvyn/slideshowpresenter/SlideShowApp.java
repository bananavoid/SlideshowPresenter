package com.lytvyn.slideshowpresenter;

import android.app.Application;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class SlideShowApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FTPClient mFTP = new FTPClient();

        String serverAddress = "snackmonsterz.com";
        String userId = "nastya@snackmonsterz.com";
        String password = "Sl1desh0w";
        String remoteDirectory = "/images/monsterz";


        try {
            mFTP.connect(serverAddress);
            mFTP.login(userId, password);
            mFTP.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mFTP.enterLocalPassiveMode();
    }
}
