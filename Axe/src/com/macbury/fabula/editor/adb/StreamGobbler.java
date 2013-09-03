package com.macbury.fabula.editor.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class StreamGobbler extends Thread {
  private BufferedReader reader = null;
  private List<String> writer = null;

  public StreamGobbler(InputStream inputStream, List<String> outputList) {
    reader = new BufferedReader(new InputStreamReader(inputStream));
    writer = outputList;
  }

  @Override
  public void run() {
    try {
      String line = null;
      while ((line = reader.readLine()) != null) {
        if (writer != null) writer.add(line);
      }
    } catch (IOException e) {
    }

    try {
      reader.close();
    } catch (IOException e) {     
    }
  }
}
