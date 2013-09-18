package com.macbury.fabula.editor.gamerunner;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class GameRunner {
  private String mainClass;
  private int startingHeapSizeInMegabytes = 40;
  private int maximumHeapSizeInMegabytes  = 128;
  private String workingDirectory         = ".";
  private List<String> classpathEntries   = new ArrayList<String>();
  private List<String> mainClassArguments = new ArrayList<String>();
  private String javaRuntime              = "java";
  
  public String getMainClass() {
    return mainClass;
  }
  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }
  public int getStartingHeapSizeInMegabytes() {
    return startingHeapSizeInMegabytes;
  }
  public void setStartingHeapSizeInMegabytes(int startingHeapSizeInMegabytes) {
    this.startingHeapSizeInMegabytes = startingHeapSizeInMegabytes;
  }
  public int getMaximumHeapSizeInMegabytes() {
    return maximumHeapSizeInMegabytes;
  }
  public void setMaximumHeapSizeInMegabytes(int maximumHeapSizeInMegabytes) {
    this.maximumHeapSizeInMegabytes = maximumHeapSizeInMegabytes;
  }
  public String getWorkingDirectory() {
    return workingDirectory;
  }
  public void setWorkingDirectory(String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }
  public List<String> getClasspathEntries() {
    return classpathEntries;
  }
  public void addClasspathEntry(String classpathEntry) {
    this.classpathEntries.add(classpathEntry);
  }
  public List<String> getMainClassArguments() {
    return mainClassArguments;
  }
  public void addArgument(String argument) {
    this.mainClassArguments.add(argument);
  }
  public String getJavaRuntime() {
    return javaRuntime;
  }
  public void setJavaRuntime(String javaRuntime) {
    this.javaRuntime = javaRuntime;
  }
  
  public String getClassPath() {
    StringBuilder builder = new StringBuilder();
    int count = 0;
    
    final int totalSize = classpathEntries.size();
    for (String classpathEntry : classpathEntries) {
      builder.append(classpathEntry);
      count++;
      if (count < totalSize) {
        builder.append(System.getProperty("path.separator"));
        }
      }
    return builder.toString();
  }
  
  public Process startProcess() throws IOException {
    List<String> argumentsList = new ArrayList<String>();
    argumentsList.add(this.javaRuntime);
    argumentsList.add(MessageFormat.format("-Xms{0}M", String.valueOf(this.startingHeapSizeInMegabytes)));
    argumentsList.add(MessageFormat.format("-Xmx{0}M", String.valueOf(this.maximumHeapSizeInMegabytes)));
    argumentsList.add("-classpath");
    argumentsList.add(getClassPath());
    argumentsList.add(this.mainClass);
    for (String arg : mainClassArguments) {
      argumentsList.add(arg);
    }
    
    ProcessBuilder processBuilder = new ProcessBuilder(argumentsList.toArray(new String[argumentsList.size()]));
    processBuilder.redirectErrorStream(true);
    processBuilder.directory(new File(this.workingDirectory));
    return processBuilder.start();
  }
  
  public GameRunner() {
    this.addClasspathEntry(System.getProperty("java.class.path"));
    this.setWorkingDirectory(".");
    this.setMainClass("com.macbury.game.editor.DesktopGame");
    this.addArgument("--play");
  }
}
