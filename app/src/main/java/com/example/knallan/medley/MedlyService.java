package com.example.knallan.medley;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioTrack;
import android.provider.MediaStore;
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

    public AudioTrack getmAudioTrack() {
        return mAudioTrack;
    }

    public void setmAudioTrack(AudioTrack mAudioTrack) {
        this.mAudioTrack = mAudioTrack;
    }

    private AudioTrack mAudioTrack;

    private FileService fileService;

    public File getSoundFile() {
        return soundFile;
    }

    public String getSoundFilePath() {
        if (soundFile != null)
            return soundFile.getAbsolutePath();
        else
            return null;
    }


    private File soundFile;

    MedlyService(FileService fileService) {
        this.fileService = fileService;
    }

    public void getFile(String folder) {
        soundFile = fileService.getRandomFile(folder);
    }

    public byte[] getMedleyBytes(Context context) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(soundFile));
            String duration = "0";
            Cursor c = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.TRACK,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.DATA,
                            MediaStore.Audio.Media.DURATION,
                            MediaStore.Audio.Media.YEAR
                    },
                    MediaStore.Audio.Media.DATA + " = ?",
                    new String[]{
                            soundFile.getAbsolutePath()
                    },
                    "");

            if (null == c) {
                // ERROR
            }

            while (c.moveToNext()) {
                c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                c.getString(c.getColumnIndex(MediaStore.Audio.Media.TRACK));
                c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                duration = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
                c.getString(c.getColumnIndex(MediaStore.Audio.Media.YEAR));
            }


            long totlalNumberOfBytes = soundFile.length();
            long totalSec = (Integer.parseInt(duration)) / 1000;
            long bytesPerSec = totlalNumberOfBytes / totalSec;
            Random random = new Random();
            long skipBytes = bytesPerSec * random.nextInt((int) (totalSec - DURATION - OFFSET));
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
