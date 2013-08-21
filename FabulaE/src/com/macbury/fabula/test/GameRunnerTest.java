package com.macbury.fabula.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.macbury.fabula.utils.GameRunner;

public class GameRunnerTest {
  
  public static void main(String[] args) {
    GameRunner gameRunner = new GameRunner();
    gameRunner.addClasspathEntry(System.getProperty("java.class.path"));
    gameRunner.setWorkingDirectory(".");
    gameRunner.setMainClass("com.macbury.fabula.DesktopGame");
    gameRunner.addArgument("--play");
    System.out.println("Starting...");
    try {
      Process process = gameRunner.startProcess();
      process.waitFor();
      System.out.println("Finished");
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      while (true) {
        String line = br.readLine();
        if (line == null) {
          break;
        } else {
          System.out.println("ERR:"+line);
        }
      }
      
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
  
}
