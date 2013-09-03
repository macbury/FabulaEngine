package com.macbury.fabula.editor.adb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;

public class AdbManager {
  private static final String TAG = "AdbManager";
  private static String appdata_folder;
  public static String os = System.getProperty("os.name");
  public static String uh = System.getProperty("user.home");
  public static String ds = System.getProperty("file.separator");
  public static String APPNAME = "adbManager";
  public static String device_uuid = "";
  public static String adb_location = "";

  public static String getAppDataFolder(){
    if(os.indexOf("Windows") == 0){
      appdata_folder=uh+ds+"AppData\\Roaming"+ds+APPNAME+ds;
    }
    else if(os.indexOf("Linux") == 0) {
      appdata_folder=uh+ds+"."+APPNAME+ds;
    }
    return appdata_folder;
  }

  public static String whereIsAdb(){
    String padb = "";

    if(os.indexOf("Windows") == 0){
      List<String> result = null;
        
        result = AdbManager.execute("adb version");
        
        if (result != null) {
          for (String line : result) {
            if(!line.isEmpty()){
              if(line.indexOf("Android Debug Bridge version") == 0){
                adb_location="adb.exe";
                return "adb.exe";
              }
            }
          }
        }     
    }
    else if(os.indexOf("Linux") == 0) {
      List<String> result = null;
        
        result = AdbManager.execute("adb version");
        
        if (result != null) {
          for (String line : result) {
            if(!line.isEmpty()){
              if(line.indexOf("Android Debug Bridge version") == 0){
                adb_location="adb";
                return "adb";
              }
            }
          }
        }

    }
    adb_location="";
    return "";
  }

    public static void PleaseCloseMe(JFrame f){
      f.setVisible(false);
      f.dispose();
    }
  
  public static List<String> execute(String commands, boolean log) {
    List<String> res = AdbManager.execute(commands);
    
    return res;
  }
    
  public static List<String> execute(String commands) {
    Gdx.app.log(TAG, commands);
    List<String> res = Collections.synchronizedList(new ArrayList<String>());
    BufferedReader bufferReader = null;
    try {
      Process process = Runtime.getRuntime().exec(commands);
      bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      
      while(true) {
        while (bufferReader.ready()) {
          String line = bufferReader.readLine();
          res.add(line);
          Gdx.app.log(TAG, line);
        }
        
        try {
          process.exitValue();
          break;
        } catch (IllegalThreadStateException e) {
          
        }
      }
      
      process.destroy();
      bufferReader.close();
      if (process.exitValue() == 255) {
        res = null;
      }     
    } catch (IOException e) {
      e.printStackTrace();
      if (bufferReader != null) {
        try {
          bufferReader.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
      res = null;
    }
    return res;
  }
}
