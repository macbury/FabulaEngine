package com.macbury.fabula.ui.ingame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.screens.GamePlayScreen;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
public class GamePlayUI extends Stage {
  protected static final String TAG = "GamePlayUI";
  private Label statusLabel;
  private Table table;
  private Skin skin;
  private GamePlayScreen screen;
  private Touchpad touchPad;
  private Button moveUpButton;
  private Button moveDownButton;
  private TextureAtlas guiAtlas;
  private Image centerTouchPad;
  private Button moveLeftButton;
  private Button moveRightButton;

  public GamePlayUI(GamePlayScreen screen) {
    this.screen   = screen;
    this.skin     = G.db.getUiSkin();
    this.guiAtlas = G.db.getAtlas("gui");
    this.table    = new Table();
    this.table.setFillParent(true);
    this.table.top().left();
    
    addActor(this.table);
    
    this.statusLabel = new Label("Loading...", skin);
    
    table.add(this.statusLabel).padLeft(10).padTop(10).top().left().colspan(4);
    table.row();
    
    table.add().colspan(3).expandY();
    table.add().expandX();
    table.row();
    
    this.moveUpButton      = new Button(new TextureRegionDrawable(guiAtlas.findRegion("up_button")));
    this.moveDownButton    = new Button(new TextureRegionDrawable(guiAtlas.findRegion("down_button")));
    this.moveLeftButton    = new Button(new TextureRegionDrawable(guiAtlas.findRegion("left_button")));
    this.moveRightButton   = new Button(new TextureRegionDrawable(guiAtlas.findRegion("right_button")));
    this.centerTouchPad    = new Image(new TextureRegionDrawable(guiAtlas.findRegion("center_button")));
    table.row();
    table.add(moveUpButton).colspan(3).padLeft(10).bottom().width(moveUpButton.getWidth());
    table.add().expandX();
    table.row();
    table.add(moveLeftButton).padLeft(10).right().width(moveLeftButton.getWidth());
    table.add(centerTouchPad).pad(0).width(centerTouchPad.getWidth()).height(centerTouchPad.getHeight()).center();
    table.add(moveRightButton).left().width(moveRightButton.getWidth());
    table.add().expandX();
    table.row();
    table.add(moveDownButton).padLeft(10).colspan(3).padBottom(10).top().width(moveDownButton.getWidth());
    table.add().expandX();
    table.row();
    
    this.moveUpButton.addListener(touchPadGestureListener);
    this.moveDownButton.addListener(touchPadGestureListener);
    this.moveLeftButton.addListener(touchPadGestureListener);
    this.moveRightButton.addListener(touchPadGestureListener);
    
    
    table.layout();
  }

  public void update(float delta) {
    if (screen.getScene() == null) {
      this.statusLabel.setText("Loading... " + "FPS: " + Gdx.graphics.getFramesPerSecond());
    } else {
      this.statusLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + " Camera: " + screen.get3DCamera().position.y);
     // this.screen.getScene().getPlayerSystem().setVelocity(this.touchPad.getKnobPercentX(), this.touchPad.getKnobPercentY() * -1);
    }
    act(delta);
  }

  public void renderDebug() {
    table.debug();
    Table.drawDebug(this);
  }
  
  public void resize(float viewportWidth, float viewportHeight) {
    this.setViewport(viewportWidth, viewportHeight, true);
    table.layout();
  }

  private ActorGestureListener touchPadGestureListener = new ActorGestureListener() {
    @Override
    public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
      if (event.getTarget() == moveUpButton) {
        GamePlayUI.this.screen.getScene().getPlayerSystem().setVelocity(0,-1.0f);
      } else if (event.getTarget() == moveDownButton) {
        GamePlayUI.this.screen.getScene().getPlayerSystem().setVelocity(0,1.0f);
      } else if (event.getTarget() == moveLeftButton) {
        GamePlayUI.this.screen.getScene().getPlayerSystem().setVelocity(-1.0f,0);
      } else if (event.getTarget() == moveRightButton) {
        GamePlayUI.this.screen.getScene().getPlayerSystem().setVelocity(1.0f,0);
      }
      
      
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
      GamePlayUI.this.screen.getScene().getPlayerSystem().setVelocity(0,0);
    }
  };
}
