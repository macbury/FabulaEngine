package com.macbury.fabula.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.os.Looper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.screens.GamePlayScreen;

public class AndroidGameManager extends GameManager {
  
  private MainActivity activity;

  public AndroidGameManager(MainActivity mainActivity) {
    super(Environment.getExternalStorageDirectory() + "/" + GameManager.ANDROID_GAME_DIRECTORY_NAME + "/");
    this.activity = mainActivity;
  }

  @Override
  public Screen getInitialScreen() {
    return new GamePlayScreen(this);
  }

  @Override
  public void onNoGameData() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setMessage("No Game data. Open World Editor Game->Upload to upload!");
    builder.setCancelable(false);
    builder.setPositiveButton("EXIT", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Gdx.app.exit();
      }
    });
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        builder.create().show();
      }
    });
  }

  @Override
  public void onNoPlayerStartPosition() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setMessage("No player start position!");
    builder.setCancelable(false);
    builder.setPositiveButton("EXIT", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Gdx.app.exit();
      }
    });
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        builder.create().show();
      }
    });
  }
}
