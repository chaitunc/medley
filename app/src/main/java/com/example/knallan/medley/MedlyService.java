package com.example.knallan.medley;

import android.content.SharedPreferences;

/**
 * Created by knallan on 12/21/2017.
 */

public class MedlyService {
    public static final int DURATION = 30;
    public static final int OFFSET = 2;

    private String soundFile;
    SharedPreferences settings;

    public String getSoundFilePath() {
        if (soundFile != null)
            return soundFile;
        else {
            return null;
        }
    }

    MedlyService(SharedPreferences settings) {
        this.settings =settings;
    }

    public byte[] getMedleyBytes() throws Exception {

        soundFile = FileService.getInstance(this.settings).getRandomFile();

        return null;

    }
}
