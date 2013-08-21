package com.macbury.fabula.editor.gamerunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.macbury.fabula.utils.GameRunner;

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
      running = false;
    }
    
    gameListener.onGameStart();
    
    while (running) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
        running = false;
      }
      try {
        this.process.exitValue();
        running = false;
      } catch (IllegalThreadStateException e) {
        
      }
      
      try {
        while (this.bufferReader.ready()) {
          gameListener.onLog(this.bufferReader.readLine());
        }
      } catch (IOException e) {
        e.printStackTrace();
        running = false;
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
    running = false;
    this.process.destroy();
    this.gameListener.onGameEnd();
  }
}
