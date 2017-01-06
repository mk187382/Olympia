package com.olympicwinners.olympia;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by user on 2016-12-05.
 */
public class Songs {
    private String fileCollection;
    private static Songs instance;

    private Songs() {

    }

    public static synchronized Songs getInstance() {
        if (instance == null) {
            instance = new Songs();
        }

        return instance;
    }

    public void addFile(String f) {
        fileCollection = f;
    }

    public String getFiles() {
        return fileCollection;
    }
}
