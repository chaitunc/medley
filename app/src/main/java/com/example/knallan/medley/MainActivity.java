package com.example.knallan.medley;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String GO_UP_FOLDER = "..go up folder";

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private View mLayout;
    private MedleyExoPlayer player;
    boolean medleyplay = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);

        player = new MedleyExoPlayer();
        boolean isOk = checkPermission();
        if (!isOk) {
            requestStoragePermission();
        }

        Log.i("MainActivity", "inside oncreate");
        final Button buttonPlay = findViewById(R.id.play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    medley();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } // END onClick()
        }); // END buttonPlay

        final Button buttonStop = findViewById(R.id.stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMedley();
            } // END onClick()
        }); // END buttonStop
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void medley() throws Exception {

        // Use the current directory as title

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + pref.getString("folder_location", "Music");
        Log.i("MainActivity", "Base PATH:" + path);

        player.play(this);


    }

    private void stopMedley() {

        if (player != null)
            player.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (medleyplay) {
            try {
                //   medley();
            } catch (Exception e) {
                e.printStackTrace();
            }
            medleyplay = false;
        }
    }

    private boolean checkPermission() {
        // BEGIN_INCLUDE(startCamera)
        // Permission is missing and must be requested.
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAPTURE_AUDIO_OUTPUT)
                == PackageManager.PERMISSION_GRANTED;
        // END_INCLUDE(startCamera)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        player.stop();
    }

    private void requestStoragePermission() {

        // Request the permission. The result will be received in onRequestPermissionResult().
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                },
                0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == 0) {
            // Request for camera permission.
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                medleyplay = true;
            } else {
                Log.i("Main", "Permission denied");
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

}
