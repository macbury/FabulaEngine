package com.macbury.fabula.player;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.macbury.fabula.manager.GameManager;

import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends AndroidApplication  {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    config.hideStatusBar = true;
    config.useWakelock   = true;
    config.useGL20       = true;
    initialize(new AndroidGameManager(this), config);
  }
}
