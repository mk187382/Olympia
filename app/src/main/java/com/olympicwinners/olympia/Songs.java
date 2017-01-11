package com.olympicwinners.olympia;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by user on 2016-12-05.
 */
public class Songs {
    private String file;
    private ArrayList<String> fileColection;
    private static Songs instance;

    public Songs(ArrayList<String> fileColection, String file) {
        this.fileColection = fileColection;
        this.file = file;
    }

    private Songs() {

    }

    static synchronized Songs getInstance() {
        if (instance == null) {
            instance = new Songs();
        }
        return instance;
    }

    public String getFile() {
        return file;
    }

    public void addFiles(ArrayList<String> s) {
        fileColection = new ArrayList<>();
        for(String track: s){
            fileColection.add(track);
        }
    }
    public ArrayList<String> getFiles() {
        return fileColection;
    }

    public void clear(){
        Songs.getInstance().fileColection.clear();
    }
}
