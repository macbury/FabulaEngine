package com.macbury.fabula.editor.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.badlogic.gdx.Gdx;

public class StreamGobbler extends Thread {
  private BufferedReader reader = null;
  private String tag;
  private InputStream is;

  public StreamGobbler(InputStream inputStream, String tag) {
    this.is = inputStream;
    this.tag = tag;
  }

  @Override
  public void run() {
    try {
      reader = new BufferedReader(new InputStreamReader(is));
      String line = null;
      while ((line = reader.readLine()) != null) {
        Gdx.app.log(tag, line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      reader.close();
    } catch (IOException e) {  
      e.printStackTrace();
    }
  }
}
