package com.macbury.fabula.player;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.macbury.fabula.manager.GameManager;

import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends AndroidApplication  {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initialize(new GameManager(Environment.getExternalStorageDirectory() + "/game"), true);
  }
}
