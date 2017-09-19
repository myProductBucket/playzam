package com.hellcoderz.ashutosh.playzam.helpers;

import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Sid on 6/8/2015.
 */
public class FileHelper {
    private Context context;

    public FileHelper(Context context){
        this.context = context;
    }

    public boolean doesFileExist(String fileName)
    {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Files");

        if (! mediaStorageDir.exists()){
            return false;
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file.exists();
    }

    public void StoreFile(String fileName, JSONArray obj){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Files");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return;
            }
        }

        try {
            String content = obj.toString();
            FileWriter fw = new FileWriter(mediaStorageDir.getPath() + File.separator + fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException f){
            f.printStackTrace();
        }
    }

    public void UpdateFile(String filename,JSONObject obj){
        JSONArray fileContents = this.readFile(filename);
        fileContents.put(obj);
        this.StoreFile(filename,fileContents);
    }

    public JSONArray readFile(String fileName){

        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        JSONArray obj = null;

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Files");

        if (! mediaStorageDir.exists()){
            return null;
        }

        try {
            br = new BufferedReader(new FileReader(mediaStorageDir.getPath() + File.separator + fileName));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            // do exception handling
        } finally {
            try { br.close(); } catch (Exception e) { }
        }

        try {
            obj = new JSONArray(text.toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
        return obj;
    }

}
