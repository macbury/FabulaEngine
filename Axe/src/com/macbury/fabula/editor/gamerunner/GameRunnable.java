package com.macbury.fabula.editor.gamerunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * adb shell am start -n com.package.name/com.package.name.ActivityName
 * adb shell mkdir /sdcard/bc
 * adb push data /sdcard/bc
 */
public class GameRunnable implements Runnable {
  private GameRunner gameRunner;
  private boolean running;
  private Process process;
  private BufferedReader bufferReader;
  private GameRunnableCallback gameListener;
  
  public GameRunnable(GameRunnableCallback listener) {
    this.gameListener = listener;
    this.gameRunner = new GameRunner();
  }
  
  public boolean isRunning() {
    return this.running;
  }
  
  @Override
  public void run() {
    this.running = true;
    try {
      this.process      = gameRunner.startProcess();
      this.bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    } catch (IOException e) {
      e.printStackTrace();
      stop();
    }
    
    gameListener.onGameStart();
    
    while (running) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
        stop();
      }
      try {
        this.process.exitValue();
        stop();
      } catch (IllegalThreadStateException e) {
        
      }
      
      try {
        while (this.bufferReader.ready()) {
          gameListener.onLog(this.bufferReader.readLine());
        }
      } catch (IOException e) {
        e.printStackTrace();
        stop();
      }
    }
    
    stop();
  }
  
  public static interface GameRunnableCallback {
    public void onGameStart();
    public void onGameEnd();
    public void onLog(String line);
  }

  public void stop() {
    if (running) {
      this.process.destroy();
      this.gameListener.onGameEnd();
    }
    running = false;
  }
}
