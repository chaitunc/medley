package com.example.knallan.medley;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

/**
 * Created by knallan on 12/21/2017.
 */

public class MedlyService {
    public static final int DURATION = 30;
    public static final int OFFSET = 2;

    private FileService fileService;
    private File soundFile;


    public String getSoundFilePath() {
        if (soundFile != null)
            return soundFile.getAbsolutePath();
        else {
            return null;
        }
    }

    MedlyService() {
        this.fileService = FileService.getInstance();
    }

    public byte[] getMedleyBytes() throws Exception {

        soundFile = fileService.getRandomFile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(soundFile));
            String duration = "0";

            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(soundFile.getAbsolutePath());
            duration = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);




            long totlalNumberOfBytes = soundFile.length();
            long totalSec = (Integer.parseInt(duration)) / 1000;
            if(totalSec==0)
                totalSec = 1;
            long bytesPerSec = totlalNumberOfBytes / totalSec;
            Random random = new Random();
            long skipBytes = 0;
            if(totalSec>(DURATION + OFFSET)){
                skipBytes = bytesPerSec * random.nextInt((int) (totalSec - DURATION - OFFSET));
            }

            Log.i("MedleyService", "start after Bytes" + skipBytes);
            long endBytes = skipBytes + (bytesPerSec * DURATION);
            Log.i("MedleyService", "end at Bytes" + endBytes);
            int bufSiz = 4096;
            byte[] buffer = new byte[bufSiz];
            int bytesRead;
            long totalBytesRead = 0;
            while ((bytesRead = buf.read(buffer)) > 0) {
                totalBytesRead += bytesRead;
                if (totalBytesRead > skipBytes && totalBytesRead < endBytes)
                    baos.write(buffer, 0, bytesRead);
                // mAudioTrack.write(buffer,0,bytesRead);
            }

            //Log.i("MedleyService","total bytes sent " + baos.toByteArray().length);

        } catch (Exception ex) {
            Log.i("MedleyService", "Error playing the audio file.");
            ex.printStackTrace();
        }
        return baos.toByteArray();

    }
}
