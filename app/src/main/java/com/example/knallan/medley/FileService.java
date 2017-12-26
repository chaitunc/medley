package com.example.knallan.medley;

/**
 * Created by knallan on 12/21/2017.
 */

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;


public class FileService {


    private ArrayList<File> files;

    public void init() {
        if (files == null) {
            files = new ArrayList<File>();
        }
    }

    public void listf(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathName) {
                if (pathName.isDirectory()) {
                    return true;
                }
                if (pathName.isFile()) {
                    String mimeType = URLConnection.guessContentTypeFromName(pathName.getAbsolutePath());
                    Log.i("FileService", "mimeType: " + mimeType);
                    Log.i("FileService", "mimeType check: " + mimeType.startsWith("audio"));
                    try {
                        if (mimeType.startsWith("audio")) {
                            return true;
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }

    public File getRandomFile(String folderpath) {
        init();
        listf(folderpath, this.files);
        if (this.files != null && this.files.size() > 0) {
            int numOfFIles = this.files.size();
            int randomFileIndex = 0;
            if (numOfFIles > 1) {
                Random random = new Random();
                randomFileIndex = random.nextInt(numOfFIles - 1);
            }
            Log.i("FileService", "fodlerpath: " + folderpath);
            Log.i("FileService", "Totla number of files:" + numOfFIles);
            Log.i("FileService", "Random FIle:" + this.files.get(randomFileIndex).getName());
            return this.files.get(randomFileIndex);

        }
        return null;
    }
}

