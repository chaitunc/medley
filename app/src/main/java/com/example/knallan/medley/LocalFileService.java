package com.example.knallan.medley;

/**
 * Created by knallan on 12/21/2017.
 */

import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;


public class LocalFileService extends FileService{

    private ArrayList<File> files;

    LocalFileService(String folderPath){
        files = new ArrayList<>();
        parseFolders(folderPath,files);
    }

    public static void initLocalInstance(SharedPreferences settings) {
        boolean playFromFolder = settings.getBoolean("playFromFolder",false);
        String folderPath = settings.getString("folderPath",null);

        if(playFromFolder){
            LocalInstance = new LocalFileService(folderPath);
        }
    }

    protected void parseFolders(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathName) {
                if (pathName.isDirectory()) {
                    return true;
                }
                if (pathName.isFile()) {
                    try {
                        String mimeType = URLConnection.guessContentTypeFromName(pathName.getAbsolutePath());

                        if (mimeType!=null && mimeType.startsWith("audio")) {
                            return true;
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                       // e.printStackTrace();
                        return false;
                    }
                }
                return false;
            }
        });
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                parseFolders(file.getAbsolutePath(), files);
            }
        }
    }

    public String getRandomFile() {

        if (this.files != null && this.files.size() > 0) {
            int numOfFIles = this.files.size();
            int randomFileIndex = 0;
            if (numOfFIles > 1) {
                Random random = new Random();
                randomFileIndex = random.nextInt(numOfFIles - 1);
            }

            Log.i("FileService", "Totla number of files:" + numOfFIles);
            Log.i("FileService", "Random FIle:" + this.files.get(randomFileIndex).getName());
            return this.files.get(randomFileIndex).getAbsolutePath();

        }
        return null;
    }


}

