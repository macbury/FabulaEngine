package com.macbury.fabula.utils;
import com.badlogic.gdx.Gdx;
public class ActionTimer {
  
  private TimerListener listener;
  private float time;
  private float currentTime;
  private boolean running = false;
  public ActionTimer(float time, TimerListener listener) {
    this.listener = listener;
    this.time = time;
  }
  
  public void start() {
    running = true;
  }
  
  public void stop() {
    this.running = false;
  }
  
  public void update(float delta) {
    if (this.running) {
      currentTime += delta;
      if (currentTime > time) {
        currentTime = 0;
        listener.onTimerTick(this);
      }
    }
  }
  
  public interface TimerListener {
    public void onTimerTick(ActionTimer timer);
  }
}
