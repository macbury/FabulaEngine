package com.macbury.fabula.editor.adb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

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

  public synchronized static void digestCommand(String[] commandAndArgs, String okIndicator) {
    RuntimeException re = null;
    try {
      String c = "";
      for (int i = 0; i < commandAndArgs.length; i++) {
          c += commandAndArgs[i] + " ";
      }
      
      Gdx.app.log(TAG, "shell: " + c);
      StringBuilder sb  = new StringBuilder();
      ProcessBuilder pb = new ProcessBuilder(commandAndArgs).redirectErrorStream(true);
      Process p         = pb.start();

      StreamGobbler errorGobbler   = new StreamGobbler(p.getErrorStream(), "SHELL ERROR");    
      StreamGobbler outputGobbler  = new StreamGobbler(p.getInputStream(), "SHELL OUTPUT");    
      errorGobbler.start();
      outputGobbler.start();
      
      int retCode = p.waitFor();
      
      if (retCode != 0 || (okIndicator != null && !sb.toString().contains(okIndicator))) {
        String msg = sb.toString() + "\nreturn code: " + retCode;
        re = new RuntimeException(msg);
        Gdx.app.log(TAG, "-> error! msg:"+msg);
      } else {
        Gdx.app.log(TAG," -> " + retCode);
      }
    } catch (Exception e) {
      throw new RuntimeException("Exception occurred: " + e.getClass().getName() + ", msg:" + e.getMessage());
    } finally {
      if (re != null) {
        throw re;
      }
    }
  }
  
  public static void adbPush(String source, String target) throws IOException {
    AdbManager.digestCommand(new String[] {"adb", "push", source, target}, null);
  }
  
  public static void startApplication(String pack) throws IOException {
    AdbManager.digestCommand(new String[] {"adb", "shell", "am", "start", "-W", "-S", pack}, null);
  }

  public static void stopApplication(String pack) {
    AdbManager.digestCommand(new String[] {"adb", "shell", "am", "force-stop", pack}, null);
  }

  public static void wakeScreen() {
    AdbManager.digestCommand(new String[] {"adb", "shell", "input", "keyevent", "26"}, null);
  }
    
}
