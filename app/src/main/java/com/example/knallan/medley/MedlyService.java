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
        soundFile = FileService.getInstance(this.settings).getRandomFile();
        return soundFile;
    }

    MedlyService(SharedPreferences settings) {
        this.settings =settings;
    }


}
