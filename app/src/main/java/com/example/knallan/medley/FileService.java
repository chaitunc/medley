package com.example.knallan.medley;

/**
 * Created by knallan on 12/21/2017.
 */

import android.content.SharedPreferences;


public abstract class FileService {

    static FileService LocalInstance;
    static FileService DriveInstance;

    public abstract SongMetaData getRandomFile();


    public static FileService getInstance(SharedPreferences settings) {
        boolean playFromFolder = settings.getBoolean("playFromFolder",false);
        boolean playFromDrive = settings.getBoolean("playFromDrive",false);
        if(playFromFolder){
            if(LocalInstance == null){
                LocalFileService.initLocalInstance(settings);
            }
            return LocalInstance;
        }else{
            return DriveInstance;
        }
    }




}

