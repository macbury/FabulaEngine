package com.macbury.fabula.editor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;

public class ExitListener extends WindowAdapter {

  LwjglCanvas canvas;
  public ExitListener(LwjglCanvas lwjglCanvas) {
    canvas = lwjglCanvas;
  }
  public void windowClosing(WindowEvent e) {
    canvas.stop();
  }
}