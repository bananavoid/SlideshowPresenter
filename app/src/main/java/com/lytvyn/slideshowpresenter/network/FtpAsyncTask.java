package com.lytvyn.slideshowpresenter.network;

import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTPClient;

public class FtpAsyncTask extends AsyncTask<Void, Void, FTPClient> {
    TaskCallback taskCallback;

    public FtpAsyncTask(TaskCallback callback) {
        this.taskCallback = callback;
    }

    @Override
    protected FTPClient doInBackground(Void... params) {
        return FTPWorker.cacheImagesFromServer();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(FTPClient result) {
        super.onPostExecute(result);
        this.taskCallback.onSuccess();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        this.taskCallback.onError("FTP TASK CANCELLED");
    }
}
