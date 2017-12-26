package com.example.knallan.medley;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.io.File;


public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String GO_UP_FOLDER = "..go up folder";

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private View mLayout;
    private ExoPlayer player;
    boolean medleyplay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        String path = Environment.getExternalStorageDirectory().getPath() + "/Music";
        Log.i("MainActivity", "Base PATH:" + path);


        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory audioTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(audioTrackSelectionFactory);

// 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

// 3. Create the player89
        RenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        FileService fs = new FileService();
        MedlyService service = new MedlyService(fs);

        service.getFile(path);
        String url = service.getSoundFilePath();


        Uri uri = Uri.fromFile(new File(url));

        byte[] bytes = service.getMedleyBytes(MainActivity.this.getApplicationContext());


        final ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(bytes);
        DataSpec dataSpec = new DataSpec(uri);
        byteArrayDataSource.open(dataSpec);

        ByteArrayDataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return byteArrayDataSource;
            }
        };
        MediaSource source = new ExtractorMediaSource(uri, factory, new DefaultExtractorsFactory(), null, null);
        player.prepare(source);

        //exoPlayer.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(true);


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
                medley();
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
