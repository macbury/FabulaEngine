package com.macbury.fabula.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Looper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.macbury.fabula.manager.GameManager;

public class AndroidGameManager extends GameManager {
  
  private MainActivity activity;

  public AndroidGameManager(MainActivity mainActivity) {
    super(Environment.getExternalStorageDirectory() + "/" + GameManager.ANDROID_GAME_DIRECTORY_NAME + "/");
    this.activity = mainActivity;
  }

  @Override
  public Screen getInitialScreen() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void onNoGameData() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setMessage("No Game data. Open World Editor Game->Upload to upload!");
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        builder.create().show();
      }
    });
  }
  

}
