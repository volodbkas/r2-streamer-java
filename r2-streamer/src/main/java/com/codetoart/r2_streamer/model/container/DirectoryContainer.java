package com.codetoart.r2_streamer.model.container;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Shrikant Badwaik on 24-Jan-17.
 */

public class DirectoryContainer implements Container {
    private final String TAG = "DirectoryContainer";
    private String rootPath;

    public DirectoryContainer(String rootPath) {
        this.rootPath = rootPath;
        File epubDirectoryFile = new File(rootPath);
        if (!epubDirectoryFile.exists()) {
            Log.e(TAG, "No such directory exists at path: " + epubDirectoryFile);
            return;
        }

        Log.d(TAG, "Directory exists at path: " + epubDirectoryFile);
    }

    @Override
    public String rawData(String relativePath) throws NullPointerException {
        String filePath = rootPath.concat(relativePath);
        File epubFile = new File(filePath);

        if (epubFile.exists()) {
            Log.d(TAG, relativePath + " File exists at given path");

            try {
                InputStream is = new FileInputStream(epubFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);        //.append('\n');
                }
                Log.d(TAG, "Reading Data: " + sb.toString());

                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!epubFile.exists()) {
            Log.e(TAG, relativePath + " No such file exists at given path");
        }
        return null;
    }

    @Override
    public int rawDataSize(String relativePath) {
        String filePath = rootPath.concat(relativePath);
        File epubFile = new File(filePath);
        return ((int) epubFile.length());
    }

    @Override
    public InputStream rawDataInputStream(final String relativePath) throws NullPointerException {
        try {
            /*String filePath = rootPath.concat(relativePath);
            File directoryFile = new File(filePath);
            InputStream inputStream = new FileInputStream(directoryFile);
            return inputStream;*/

            Callable<InputStream> callable = new Callable<InputStream>() {
                @Override
                public InputStream call() throws Exception {
                    String filePath = rootPath.concat(relativePath);
                    File directoryFile = new File(filePath);
                    return new FileInputStream(directoryFile);
                }
            };
            ExecutorService executorService = Executors.newCachedThreadPool();
            Future<InputStream> future = executorService.submit(callable);
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}