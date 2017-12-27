package com.example.knallan.medley;

import android.content.Context;
import android.net.Uri;

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
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

/**
 * Created by knallan on 12/26/2017.
 */

public class MedleyExoPlayer implements MusicPlayerServices, Player.EventListener {

    private ExoPlayer player;
    private DynamicConcatenatingMediaSource dynamicContent;

    public ExoPlayer initializePlayer(Context context){
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory audioTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(audioTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        dynamicContent = new DynamicConcatenatingMediaSource();
        player.addListener(this);
        return player;

    }

    @Override
    public void play(Context context) throws Exception {
        if(player == null){
            player = initializePlayer(context);
        }
        MediaSource source = getMediaSource();

        player.prepare(source);

        player.setPlayWhenReady(true);

    }

    private MediaSource getMediaSource() throws Exception {

        MedlyService service = new MedlyService();
        byte[] bytes = service.getMedleyBytes();
        Uri uri = Uri.parse(service.getSoundFilePath());
        final ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(bytes);
        DataSpec dataSpec = new DataSpec(uri);
        byteArrayDataSource.open(dataSpec);

        ByteArrayDataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return byteArrayDataSource;
            }
        };

        return new ExtractorMediaSource(uri, factory, new DefaultExtractorsFactory(), null, null);

    }

    private void addMediaSource() throws Exception {
        MediaSource source = getMediaSource();
        player.prepare(source);
        player.setPlayWhenReady(true);


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
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if(playbackState == Player.STATE_ENDED){
            try {
                addMediaSource();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    @Override
    public void stop() {
        if(player!=null){
            player.stop();
        }
    }
}
