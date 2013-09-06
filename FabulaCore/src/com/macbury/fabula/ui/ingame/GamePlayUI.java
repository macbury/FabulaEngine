package com.macbury.fabula.ui.ingame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.screens.GamePlayScreen;

public class GamePlayUI extends Stage {
  private Label statusLabel;
  private Table table;
  private Skin skin;
  private GamePlayScreen screen;

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
  }

  public void update(float delta) {
    if (screen.getScene() == null) {
      this.statusLabel.setText("Loading...");
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
