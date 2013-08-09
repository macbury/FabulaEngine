package com.macbury.fabula.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class EditorCamController extends InputAdapter {

  private static final String TAG = "EditorCamController";
  private Camera camera;
  private float startX = 0;
  private float startY = 0;
  private int button = -1;
  private Vector3 tmpV1 = new Vector3();
  private Vector3 tmpV2 = new Vector3();
  public Vector3 target = new Vector3();
  public float translateUnits = 30f;
  public float rotateAngle = 360f;
  public float scrollFactor = -0.1f;
  
  public EditorCamController(final Camera camera) {
    this.camera = camera;
  }

  public void update() {
    target.set(camera.position);
    target.y = 0;
  }

  @Override
  public boolean touchDown (int screenX, int screenY, int pointer, int button) {
    if (this.button == -1) {
      startX      = screenX;
      startY      = screenY;
      this.button = button;
      
      return true;
    } else {
      return false;
    }
  }
  
  @Override
  public boolean touchDragged (int screenX, int screenY, int pointer) {
    if (this.button == -1)
      return false;
    float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
    float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
    Gdx.app.log(TAG, "Delta " + deltaX + "x" + deltaY);
    Gdx.app.log(TAG, "Screen " + screenX + "x" + screenY);
    startX = screenX;
    startY = screenY;
    return process(deltaX, deltaY, button);
  }

  private boolean process(float deltaX, float deltaY, int button) {
    
    if (button == Buttons.MIDDLE) {
      tmpV1 = tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits);
      tmpV1.y = 0;
      camera.translate(tmpV1);
      camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits));
      //if (translateTarget)
      //  target.add(tmpV1).add(tmpV2); 
    }
    
    if (button == Buttons.RIGHT) {
      tmpV1.set(camera.direction).crs(camera.up).y = 0f;
      camera.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle);
      camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle);
    }
    
    return true;
  }

  @Override
  public boolean touchUp (int screenX, int screenY, int pointer, int button) {
    if (button == this.button)
      this.button = -1;
    return true;
  }
  
  @Override
  public boolean scrolled (int amount) {
    camera.translate(tmpV1.set(camera.direction).scl(amount * scrollFactor * translateUnits));
    return true;
  }

}
