package com.example.knallan.medley;

import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Random;

/**
 * Created by knallan on 12/26/2017.
 */

public class MedleyExoPlayer implements Player.EventListener {

    private ExoPlayer player;
    Context mContext;
    private MedlyService service;
    CountDownTimer timer;
    boolean started = false;

    private static final MedleyExoPlayer ourInstance = new MedleyExoPlayer();

    public static MedleyExoPlayer getInstance() {
        return ourInstance;
    }

    private MedleyExoPlayer() {
    }

    public ExoPlayer initializePlayer(Context context){
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory audioTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(audioTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        player.addListener(ourInstance);
        this.mContext = context;
        service = new MedlyService(context.getSharedPreferences("MedleySettings", 0));
        timer = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try {
                    started = false;
                    addMediaSource();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return player;

    }

    public void play(Context context) throws Exception {
        if(player == null){
            player = initializePlayer(context);
        }
        MediaSource source = getMediaSource();
        player.prepare(source);
        player.setPlayWhenReady(true);
        started = false;

    }

    private MediaSource getMediaSource() throws Exception {

        service.getMedleyBytes();
        String source = service.getSoundFilePath();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(
                        mContext, Util.getUserAgent(mContext, "uamp"), null);

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // The MediaSource represents the media to be played.
        MediaSource mediaSource =
                new ExtractorMediaSource(
                        Uri.parse(source), dataSourceFactory, extractorsFactory, null, null);

        // Prepares media to play (happens on background thread) and triggers
        // {@code onPlayerStateChanged} callback when the stream is ready to play.

        return mediaSource;//new ExtractorMediaSource(uri, factory, new DefaultExtractorsFactory(), null, null);

    }

    private void addMediaSource() throws Exception {
        MediaSource source = getMediaSource();
        player.prepare(source);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if (playbackState == Player.STATE_READY && started == false) {
            final long realDurationMillis = player.getDuration();
            int inSec = (int)(realDurationMillis/1000);
            Random random = new Random();
            int startSec = random.nextInt(inSec - 30);
            player.seekTo(startSec*1000 );
            Log.i("player", "state ready");
            timer.start();
            started = true;
        }
        if( playbackState == Player.STATE_ENDED){

        }

    }

    public void stop() {
        if(player!=null){
            player.stop();
            timer.cancel();
            started = false;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }



    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }


}
