package com.macbury.fabula.ui.ingame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.screens.GamePlayScreen;

public class GamePlayUI extends Stage {
  private Label statusLabel;
  private Table table;
  private Skin skin;
  private GamePlayScreen screen;
  private Touchpad touchPad;

  public GamePlayUI(GamePlayScreen screen) {
    this.screen = screen;
    this.skin   = G.db.getUiSkin();
    this.table  = new Table();
    this.table.setFillParent(true);
    this.table.top().left();
    
    addActor(this.table);
    
    this.statusLabel = new Label("Loading...", skin);
    table.add(this.statusLabel).padLeft(10).padTop(10);
    table.row();
    
    this.touchPad = new Touchpad(20, this.skin);
    touchPad.setBounds(10, 10, 150, 150);
    addActor(touchPad);
  }

  public void update(float delta) {
    if (screen.getScene() == null) {
      this.statusLabel.setText("Loading... " + "FPS: " + Gdx.graphics.getFramesPerSecond());
    } else {
      this.statusLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
    }
    act(delta);
  }

  public void renderDebug() {
    table.debug();
    Table.drawDebug(this);
  }
  
  public void resize(float viewportWidth, float viewportHeight) {
    this.setViewport(viewportWidth, viewportHeight, true);
  }
  
}
