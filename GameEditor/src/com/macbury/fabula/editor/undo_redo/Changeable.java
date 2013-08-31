package com.macbury.fabula.editor.undo_redo;

public interface Changeable {
  public void undo();
  public void redo();
}
