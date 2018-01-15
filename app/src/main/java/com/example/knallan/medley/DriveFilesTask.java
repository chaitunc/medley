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
import java.util.Random;

/**
 * An asynchronous task that handles the Drive API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class DriveFilesTask extends AsyncTask<Void, Void, List<SongMetaData>> {
    private com.google.api.services.drive.Drive mService = null;
    private Exception mLastError = null;
    private Activity google;
    private GoogleAccountCredential credential;

    public boolean isInitialized() {
        return initialized;
    }

    private boolean initialized = false;

    public static DriveFilesTask instance;

    public static enum ORDERBY{
        createdDate, folder, lastViewedByMeDate, modifiedByMeDate, modifiedDate, quotaBytesUsed, recency, sharedWithMeDate, starred, title, title_natural
    }
    private static final Random RANDOM = new Random();
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = RANDOM.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
    public static DriveFilesTask getInstance(GoogleAccountCredential credential, Activity google){
        if(instance!=null){
            return instance;
        }else {
            instance = new DriveFilesTask(credential,google);
            return instance;
        }
    }


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
    protected List<SongMetaData> doInBackground(Void... params) {
        try {
            this.initialized = true;
            return getDataFromApi();
        }catch (UserRecoverableAuthIOException userRecoverableException) {
            google.startActivityForResult(
                    userRecoverableException.getIntent(), 1);
            return null;
        } catch (Exception e) {

            mLastError = e;
            cancel(true);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fetch a list of up to 10 file names and IDs.
     * @return List of Strings describing files, or an empty list if no files
     *         found.
     * @throws IOException
     */
    private List<SongMetaData> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.
        List<SongMetaData> fileInfo = new ArrayList<SongMetaData>();
        String orderby = randomEnum(ORDERBY.class).toString();
        FileList result = mService.files().list()
                .setQ("mimeType='audio/mp3'")
                .setMaxResults(500)
                .setFields("items(downloadUrl,originalFilename)")
                .setOrderBy(orderby)
                .execute();
        List<File> files = result.getItems();
        if (files != null) {
            for (File file : files) {
                String id = file.getId();
                file.getDownloadUrl();
                try {
                    String url = file.getDownloadUrl()+"&access_token="
                            +this.credential.getToken();
                    SongMetaData metaData = new SongMetaData();
                    metaData.setName(file.getOriginalFilename());
                    metaData.setUrl(url);
                    fileInfo.add(metaData);
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
    protected void onPostExecute(List<SongMetaData> output) {

    }

    @Override
    protected void onCancelled() {

    }
}
