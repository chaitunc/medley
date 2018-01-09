package com.example.knallan.medley;

/**
 * Created by knallan on 1/4/2018.
 */

import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Drive API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class DriveFilesTask extends AsyncTask<Void, Void, List<String>> {
    private com.google.api.services.drive.Drive mService = null;
    private Exception mLastError = null;
    private Activity google;
    private GoogleAccountCredential credential;

    DriveFilesTask(GoogleAccountCredential credential,Activity google) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        this.google = google;
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mService = new com.google.api.services.drive.Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Medley")
                .build();
        this.credential = credential;
    }

    /**
     * Background task to call Drive API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        }catch (UserRecoverableAuthIOException userRecoverableException) {
            google.startActivityForResult(
                    userRecoverableException.getIntent(), 1);
            return null;
        } catch (Exception e) {

            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of up to 10 file names and IDs.
     * @return List of Strings describing files, or an empty list if no files
     *         found.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.
        List<String> fileInfo = new ArrayList<String>();

        FileList result = mService.files().list()
                .setQ("mimeType='audio/mp3'")
                .setFields("items(downloadUrl,originalFilename)")
                .execute();
        List<File> files = result.getItems();
        if (files != null) {
            for (File file : files) {
                String id = file.getId();
                file.getDownloadUrl();
                try {
                    String url = file.getDownloadUrl()+"&access_token="
                            +this.credential.getToken();
                    fileInfo.add(url);
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }

            }
        }
        DriveFileService.initDriveInstance(fileInfo);
        return fileInfo;
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(List<String> output) {

    }

    @Override
    protected void onCancelled() {

    }
}
