package com.example.knallan.medley;

/**
 * Created by knallan on 12/21/2017.
 */

import android.util.Log;

import java.util.List;
import java.util.Random;


public class DriveFileService extends FileService{

    private List<SongMetaData> fileInfo;

    DriveFileService(List<SongMetaData> fileInfo){
        this.fileInfo = fileInfo;
    }

    public static void initDriveInstance(List<SongMetaData> fileInfo) {
        DriveInstance = new DriveFileService(fileInfo);

    }

    public SongMetaData getRandomFile() {

        if (this.fileInfo != null && this.fileInfo.size() > 0) {
            int numOfFIles = this.fileInfo.size();
            int randomFileIndex = 0;
            if (numOfFIles > 1) {
                Random random = new Random();
                randomFileIndex = random.nextInt(numOfFIles - 1);
            }

            Log.i("FileService", "Totla number of files:" + numOfFIles);
            Log.i("FileService", "Random FIle:" + this.fileInfo.get(randomFileIndex));

            return this.fileInfo.get(randomFileIndex);

        }
        return null;
    }


}

