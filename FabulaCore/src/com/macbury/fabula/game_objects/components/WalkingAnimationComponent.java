package com.macbury.fabula.game_objects.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class WalkingAnimationComponent extends Component {
  private Animation leftAnimation;
  private Animation rightAnimation;
  private Animation upAnimation;
  private Animation downAnimation;
  private float stateTime;
  
  private final static int DOWN_ANIMATION_START  = 0;
  private final static int DOWN_ANIMATION_END    = 3;
  private final static int LEFT_ANIMATION_START  = 4;
  private final static int LEFT_ANIMATION_END    = 7;
  private final static int RIGHT_ANIMATION_START = 8;
  private final static int RIGHT_ANIMATION_END   = 11;
  private final static int UP_ANIMATION_START    = 12;
  private final static int UP_ANIMATION_END      = 15;
  private static final float WALKING_ANIMATION   = 0.2f;
  
  public WalkingAnimationComponent(Array<AtlasRegion> regions) {
    this.leftAnimation  = new Animation(WALKING_ANIMATION, getKeyFrames(regions, LEFT_ANIMATION_START, LEFT_ANIMATION_END), Animation.LOOP);
    this.rightAnimation = new Animation(WALKING_ANIMATION, getKeyFrames(regions, RIGHT_ANIMATION_START, RIGHT_ANIMATION_END), Animation.LOOP);
    this.upAnimation    = new Animation(WALKING_ANIMATION, getKeyFrames(regions, UP_ANIMATION_START, UP_ANIMATION_END), Animation.LOOP);
    this.downAnimation  = new Animation(WALKING_ANIMATION, getKeyFrames(regions, DOWN_ANIMATION_START, DOWN_ANIMATION_END), Animation.LOOP);
  }

  public Array<TextureRegion> getKeyFrames(Array<AtlasRegion> npcAnimationRegions, int from, int to) {
    Array<TextureRegion> array = new Array<TextureRegion>(to-from);
    for (int i = from; i < to; i++) {
      array.add(npcAnimationRegions.get(i));
    }
    return array;
  }

  public Animation getLeftAnimation() {
    return leftAnimation;
  }

  public Animation getRightAnimation() {
    return rightAnimation;
  }

  public Animation getUpAnimation() {
    return upAnimation;
  }

  public Animation getDownAnimation() {
    return downAnimation;
  }

  public void updateTime(float delta) {
    this.stateTime += delta;
  }

  public float getStateTime() {
    return this.stateTime;
  }

  public void resetTime() {
    this.stateTime = 0.0f;
  }

}
